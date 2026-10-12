package it.unicam.cs.mpgc.rpg125657.cursedcastles.application.progressione;

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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Coordina progressione persistente e ricostruzione del modello runtime. */
public final class ServizioProgressione {

    private final ProgressiRepository repository;
    private final FabbricaEroi fabbricaEroi;
    private final List<ClasseEroe> classi;
    private final List<Castello> castelli;

    public ServizioProgressione(ProgressiRepository repository) {
        this(repository, new FabbricaEroiStandard(), ClassiEroe.tutte(), Castelli.tutti());
    }

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

    /** Sostituisce il vecchio slot con una nuova progressione priva di castelli completati. */
    public ProgressiPartita nuovaPartita(String nomeEroe, ClasseEroe classe) {
        ClasseEroe classeRegistrata = classeDaCodice(
                Objects.requireNonNull(classe, "La classe non può essere null").codice()
        );
        ProgressiPartita progressi = ProgressiPartita.nuovi(nomeEroe, classeRegistrata.codice());
        repository.sostituisci(progressi);
        return progressi;
    }

    public Optional<ProgressiPartita> carica() {
        Optional<ProgressiPartita> progressi = repository.carica();
        progressi.ifPresent(this::verificaRiferimenti);
        return progressi;
    }

    public Eroe ricreaEroe(ProgressiPartita progressi) {
        Objects.requireNonNull(progressi, "I progressi non possono essere null");
        verificaRiferimenti(progressi);
        return fabbricaEroi.crea(progressi.nomeEroe(), classeDaCodice(progressi.codiceClasse()));
    }

    /**
     * Ricrea solo lo stato necessario ad affrontare il primo castello non completato.
     * Restituisce empty quando la campagna è già stata completata.
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

    public ProgressiPartita registraCompletamento(Castello castello) {
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

    /** Registra il castello solo se la battaglia corrente è stata effettivamente vinta. */
    public ProgressiPartita registraVittoria(Partita partita) {
        Objects.requireNonNull(partita, "La partita non può essere null");
        if (partita.battagliaCorrente().stato() != StatoBattaglia.VITTORIA) {
            throw new IllegalStateException("Il castello corrente non è stato completato");
        }
        return registraCompletamento(partita.castelloCorrente());
    }

    /** Punto applicativo che persiste la vittoria prima di far avanzare il modello. */
    public void avanzaDopoVittoria(Partita partita) {
        registraVittoria(partita);
        partita.avanzaAlProssimoCastello();
    }

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

    private void verificaRiferimenti(ProgressiPartita progressi) {
        classeDaCodice(progressi.codiceClasse());
        for (String codiceCastello : progressi.codiciCastelliCompletati()) {
            indiceCastello(codiceCastello);
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
