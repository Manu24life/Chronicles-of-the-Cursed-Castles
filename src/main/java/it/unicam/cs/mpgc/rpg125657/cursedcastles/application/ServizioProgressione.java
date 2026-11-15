package it.unicam.cs.mpgc.rpg125657.cursedcastles.application;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello.Castelli;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello.Castello;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello.FabbricaNemici;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.StatoBattaglia;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.ClasseEroe;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.ClassiEroe;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.Eroe;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.FabbricaEroi;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.FabbricaEroiStandard;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.partita.Partita;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.progressione.ProgressiPartita;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.progressione.ProgressiRepository;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.progressione.SalvataggioNonValidoException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Coordina progressione persistente e ricostruzione del modello runtime.
 */
public final class ServizioProgressione {

    private final ProgressiRepository repository;
    private final FabbricaEroi fabbricaEroi;
    private final List<ClasseEroe> classi;
    private final List<Castello> castelli;

    /**
     * Configura il repository e i cataloghi usati per validare e ricostruire la progressione.
     *
     * @param repository repository dello slot di salvataggio
     * @throws NullPointerException se uno degli argomenti è null
     */
    public ServizioProgressione(ProgressiRepository repository) {
        this(repository, new FabbricaEroiStandard(), ClassiEroe.tutte(), Castelli.tutti());
    }

    /**
     * Configura il repository e i cataloghi usati per validare e ricostruire la progressione.
     *
     * @param repository repository dello slot di salvataggio
     * @param fabbricaEroi fabbrica utilizzata per ricostruire gli eroi
     * @param classi catalogo delle classi selezionabili
     * @param castelli sequenza ordinata dei castelli della campagna
     * @throws NullPointerException se uno degli argomenti è null
     * @throws IllegalArgumentException se un catalogo è vuoto, contiene null o presenta codici duplicati
     */
    public ServizioProgressione(
            ProgressiRepository repository,
            FabbricaEroi fabbricaEroi,
            List<ClasseEroe> classi,
            List<Castello> castelli
    ) {
        this.repository = Objects.requireNonNull(repository, "Il repository non può essere null");
        this.fabbricaEroi = Objects.requireNonNull(fabbricaEroi, "La fabbrica degli eroi non può essere null");
        this.classi = copiaValidata(classi, "Le classi");
        this.castelli = copiaValidata(castelli, "I castelli");
        verificaCodiciUnivoci();
    }

    /**
     * Costruisce una nuova partita prima di sostituire lo slot; conserva il precedente salvataggio se la
     * costruzione fallisce.
     *
     * @param nomeEroe nome dell'eroe, ripulito dagli spazi iniziali e finali
     * @param classe archetipo dell'eroe con statistiche e abilità predefinita
     * @param fabbricaNemici fabbrica che crea nuovi avversari per ogni castello
     * @return partita appena creata e avviata
     * @throws NullPointerException se almeno uno tra {@code classe} è {@code null}
     * @throws it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza se l’accesso al
     *     salvataggio non riesce
     */
    public Partita nuovaPartita(String nomeEroe, ClasseEroe classe, FabbricaNemici fabbricaNemici) {
        ClasseEroe classeRegistrata = classeDaCodice(
                Objects.requireNonNull(classe, "La classe non può essere null").codice()
        );
        ProgressiPartita progressi = ProgressiPartita.nuovi(nomeEroe, classeRegistrata.codice());
        Partita candidata = ricostruisciPartita(progressi, fabbricaNemici).orElseThrow();
        repository.sostituisci(progressi);
        return candidata;
    }

    /**
     * Legge lo slot di salvataggio, se presente.
     *
     * @return salvataggio presente, oppure {@link java.util.Optional#empty()} se lo slot è vuoto
     * @throws it.unicam.cs.mpgc.rpg125657.cursedcastles.model.progressione.SalvataggioNonValidoException se i
     *     progressi contengono riferimenti sconosciuti o castelli completati fuori ordine
     * @throws it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza se l’accesso al
     *     salvataggio non riesce
     */
    public Optional<ProgressiPartita> carica() {
        Optional<ProgressiPartita> progressi = repository.carica();
        progressi.ifPresent(this::verificaRiferimenti);
        return progressi;
    }

    /**
     * Restituisce le classi registrate per la campagna come lista immutabile.
     *
     * @return lista immutabile delle classi selezionabili
     */
    public List<ClasseEroe> classiDisponibili() {
        return classi;
    }

    /**
     * Restituisce la sequenza immutabile dei castelli della campagna.
     *
     * @return lista immutabile dei castelli della campagna
     */
    public List<Castello> castelliCampagna() {
        return castelli;
    }

    /**
     * Ricostruisce un eroe con le statistiche iniziali della classe salvata, dopo aver validato i riferimenti della
     * progressione.
     *
     * @param progressi dati persistenti della progressione
     * @return nuovo eroe con statistiche iniziali
     * @throws NullPointerException se almeno uno tra {@code progressi} è {@code null}
     * @throws it.unicam.cs.mpgc.rpg125657.cursedcastles.model.progressione.SalvataggioNonValidoException se i
     *     progressi contengono riferimenti sconosciuti o castelli completati fuori ordine
     */
    public Eroe ricreaEroe(ProgressiPartita progressi) {
        Objects.requireNonNull(progressi, "I progressi non possono essere null");
        verificaRiferimenti(progressi);
        return fabbricaEroi.crea(progressi.nomeEroe(), classeDaCodice(progressi.codiceClasse()));
    }

    /**
     * Ricostruisce una nuova battaglia dal primo castello incompleto, senza ripristinare danni o bonus dello
     * scontro interrotto.
     *
     * @param progressi dati persistenti della progressione
     * @param fabbricaNemici fabbrica che crea nuovi avversari per ogni castello
     * @return partita avviata, oppure {@link java.util.Optional#empty()} se la campagna è completa
     * @throws NullPointerException se almeno uno tra {@code progressi}, {@code fabbricaNemici} è {@code null}
     * @throws it.unicam.cs.mpgc.rpg125657.cursedcastles.model.progressione.SalvataggioNonValidoException se i
     *     progressi contengono riferimenti sconosciuti o castelli completati fuori ordine
     */
    public Optional<Partita> ricostruisciPartita(
            ProgressiPartita progressi,
            FabbricaNemici fabbricaNemici
    ) {
        Objects.requireNonNull(progressi, "I progressi non possono essere null");
        Objects.requireNonNull(fabbricaNemici, "La fabbrica dei nemici non può essere null");
        verificaRiferimenti(progressi);

        int indice = indicePrimoCastelloNonCompletato(progressi);
        if (indice == castelli.size()) {
            return Optional.empty();
        }

        Partita partita = new Partita(ricreaEroe(progressi), castelli, fabbricaNemici);
        partita.iniziaDa(indice);
        return Optional.of(partita);
    }

    private ProgressiPartita registraCompletamento(Castello castello) {
        Objects.requireNonNull(castello, "Il castello non può essere null");
        int indiceCastello = indiceCastello(castello.codice());
        ProgressiPartita correnti = carica().orElseThrow(
                () -> new IllegalStateException("Non esiste un salvataggio da aggiornare")
        );
        verificaOrdineProgressione(correnti, indiceCastello);

        ProgressiPartita aggiornati = correnti.conCastelloCompletato(castello.codice());
        if (aggiornati != correnti) {
            repository.sostituisci(aggiornati);
        }
        return aggiornati;
    }

    /**
     * Registra il completamento del castello corrente solo dopo una vittoria, rispettando l’ordine della campagna.
     *
     * @param partita partita o snapshot da elaborare
     * @return progressione aggiornata e salvata
     * @throws NullPointerException se almeno uno tra {@code partita} è {@code null}
     * @throws IllegalStateException se manca un salvataggio, la battaglia non è vinta o i castelli sono completati
     *     fuori ordine
     * @throws it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza se l’accesso al
     *     salvataggio non riesce
     */
    public ProgressiPartita registraVittoria(Partita partita) {
        Objects.requireNonNull(partita, "La partita non può essere null");
        if (partita.battagliaCorrente().stato() != StatoBattaglia.VITTORIA) {
            throw new IllegalStateException("Il castello corrente non è stato completato");
        }
        return registraCompletamento(partita.castelloCorrente());
    }

    /**
     * Elimina lo slot persistente di salvataggio.
     *
     * @throws it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza se l’accesso al
     *     salvataggio non riesce
     */
    public void eliminaSalvataggio() {
        repository.elimina();
    }

    private int indicePrimoCastelloNonCompletato(ProgressiPartita progressi) {
        for (int indice = 0; indice < castelli.size(); indice++) {
            if (!progressi.isCastelloCompletato(castelli.get(indice).codice())) {
                return indice;
            }
        }
        return castelli.size();
    }

    private void verificaOrdineProgressione(ProgressiPartita progressi, int indiceCastello) {
        for (int indice = 0; indice < indiceCastello; indice++) {
            if (!progressi.isCastelloCompletato(castelli.get(indice).codice())) {
                throw new IllegalStateException("Non è possibile completare i castelli fuori ordine");
            }
        }
    }

    /**
     * Verifica che tutti i castelli della campagna siano completati, dopo aver validato i riferimenti del
     * salvataggio.
     *
     * @param progressi dati persistenti della progressione
     * @return {@code true} se tutti i castelli configurati risultano completati
     * @throws NullPointerException se almeno uno tra {@code progressi} è {@code null}
     * @throws it.unicam.cs.mpgc.rpg125657.cursedcastles.model.progressione.SalvataggioNonValidoException se i
     *     progressi contengono riferimenti sconosciuti o castelli completati fuori ordine
     */
    public boolean campagnaCompletata(ProgressiPartita progressi) {
        verificaRiferimenti(Objects.requireNonNull(progressi));
        return indicePrimoCastelloNonCompletato(progressi) == castelli.size();
    }

    private void verificaRiferimenti(ProgressiPartita progressi) {
        try {
            classeDaCodice(progressi.codiceClasse());
            for (String codiceCastello : progressi.codiciCastelliCompletati()) {
                indiceCastello(codiceCastello);
            }
        } catch (IllegalStateException errore) {
            throw new SalvataggioNonValidoException(errore.getMessage(), errore);
        }
        boolean trovatoIncompleto = false;
        for (Castello castello : castelli) {
            if (!progressi.isCastelloCompletato(castello.codice())) {
                trovatoIncompleto = true;
            } else if (trovatoIncompleto) {
                throw new SalvataggioNonValidoException("Il salvataggio contiene castelli completati fuori ordine");
            }
        }
    }

    private ClasseEroe classeDaCodice(String codice) {
        return classi.stream()
                .filter(classe -> classe.codice().equals(codice))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Classe sconosciuta nel salvataggio: " + codice));
    }

    private int indiceCastello(String codice) {
        for (int indice = 0; indice < castelli.size(); indice++) {
            if (castelli.get(indice).codice().equals(codice)) {
                return indice;
            }
        }
        throw new IllegalStateException("Castello sconosciuto nel salvataggio: " + codice);
    }

    private void verificaCodiciUnivoci() {
        if (classi.stream().map(ClasseEroe::codice).distinct().count() != classi.size()) {
            throw new IllegalArgumentException("I codici delle classi devono essere univoci");
        }
        if (castelli.stream().map(Castello::codice).distinct().count() != castelli.size()) {
            throw new IllegalArgumentException("I codici dei castelli devono essere univoci");
        }
    }

    private static <T> List<T> copiaValidata(List<T> elementi, String descrizione) {
        Objects.requireNonNull(elementi, descrizione + " non possono essere null");
        if (elementi.isEmpty() || elementi.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(descrizione + " devono contenere almeno un elemento non null");
        }
        return List.copyOf(elementi);
    }
}
