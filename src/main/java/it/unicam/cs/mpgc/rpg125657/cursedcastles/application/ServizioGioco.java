package it.unicam.cs.mpgc.rpg125657.cursedcastles.application;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello.FabbricaNemici;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.Battaglia;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.RisultatoAzione;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.StatoBattaglia;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.ClasseEroe;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.partita.Partita;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.progressione.ProgressiPartita;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Unico coordinatore della sessione runtime e dei suoi checkpoint persistenti.
 */
public final class ServizioGioco {
    private final ServizioProgressione progressione;
    private final FabbricaNemici fabbricaNemici;
    private final SessioneGioco sessione = new SessioneGioco();
    private boolean vittoriaSalvata;

    /**
     * Collega la progressione persistente alla fabbrica dei nemici e crea una sessione inizialmente vuota.
     *
     * @param progressione servizio che coordina il repository e la ricostruzione della campagna
     * @param fabbricaNemici fabbrica che crea nuovi avversari per ogni castello
     * @throws NullPointerException se almeno uno tra {@code progressione}, {@code fabbricaNemici} è {@code null}
     */
    public ServizioGioco(ServizioProgressione progressione, FabbricaNemici fabbricaNemici) {
        this.progressione = Objects.requireNonNull(progressione);
        this.fabbricaNemici = Objects.requireNonNull(fabbricaNemici);
    }

    /**
     * Restituisce le classi registrate per la campagna come lista immutabile.
     *
     * @return lista immutabile delle classi selezionabili
     */
    public List<ClasseEroe> classiDisponibili() { return progressione.classiDisponibili(); }
    /**
     * Legge lo slot di salvataggio, se presente.
     *
     * @return salvataggio presente, oppure {@link java.util.Optional#empty()} se lo slot è vuoto
     * @throws it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza se l’accesso al
     *     salvataggio non riesce
     */
    public Optional<ProgressiPartita> carica() { return progressione.carica(); }
    /**
     * Verifica che tutti i castelli della campagna siano completati, dopo aver validato i riferimenti del
     * salvataggio.
     *
     * @param progressi dati persistenti della progressione
     * @return {@code true} se tutti i castelli configurati risultano completati
     */
    public boolean campagnaCompletata(ProgressiPartita progressi) {
        return progressione.campagnaCompletata(progressi);
    }
    /**
     * Restituisce la partita attiva, se presente.
     *
     * @return partita attiva, oppure {@link java.util.Optional#empty()} se la sessione è vuota
     */
    public Optional<Partita> partita() { return sessione.partita(); }
    /**
     * Restituisce la partita attiva, richiedendone la presenza.
     *
     * @return partita attiva
     * @throws IllegalStateException se manca una partita attiva o il suo stato non consente il comando
     */
    public Partita richiediPartita() { return sessione.richiediPartita(); }
    /**
     * Indica se la vittoria corrente è già stata registrata nel salvataggio.
     *
     * @return {@code true} se il completamento corrente è già persistito
     */
    public boolean vittoriaSalvata() { return vittoriaSalvata; }

    /**
     * Risolve la classe dal codice, crea e salva una nuova partita e la installa nella sessione solo dopo il
     * successo.
     *
     * @param nome nome visualizzato
     * @param codiceClasse identificativo della classe dell'eroe
     * @throws NullPointerException se almeno uno tra {@code codiceClasse} è {@code null}
     * @throws IllegalArgumentException se il nome non è valido o il codice della classe è sconosciuto
     * @throws it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza se l’accesso al
     *     salvataggio non riesce
     */
    public void nuovaPartita(String nome, String codiceClasse) {
        Objects.requireNonNull(codiceClasse, "Il codice della classe non può essere null");
        ClasseEroe classe = classiDisponibili().stream()
                .filter(c -> c.codice().equals(codiceClasse))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Classe sconosciuta: " + codiceClasse));
        Partita candidata = progressione.nuovaPartita(nome, classe, fabbricaNemici);
        sessione.imposta(candidata);
        vittoriaSalvata = false;
    }

    /**
     * Carica il salvataggio e avvia una nuova battaglia dal primo castello incompleto.
     *
     * @throws it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza se l’accesso al
     *     salvataggio non riesce
     * @throws IllegalStateException se manca un salvataggio utilizzabile o la campagna salvata è già completata
     */
    public void continuaPartita() {
        ProgressiPartita progressi = carica().orElseThrow(
                () -> new IllegalStateException("Non esiste un salvataggio da caricare"));
        Partita candidata = progressione.ricostruisciPartita(progressi, fabbricaNemici)
                .orElseThrow(() -> new IllegalStateException("La campagna salvata è già completata"));
        sessione.imposta(candidata);
        vittoriaSalvata = false;
    }

    /**
     * Elimina il salvataggio e termina la sessione solo dopo la riuscita dell’operazione persistente.
     *
     * @throws it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza se l’accesso al
     *     salvataggio non riesce
     */
    public void eliminaSalvataggio() {
        progressione.eliminaSalvataggio();
        sessione.termina();
        vittoriaSalvata = false;
    }

    /**
     * Esegue l’attacco dell’eroe contro il nemico selezionato.
     * Se l'attacco conclude la battaglia con una vittoria, tenta di salvarla dopo aver applicato l'azione.
     *
     * @param indice indice nella sequenza originale, a partire da zero
     * @return esito dell’azione, con successo o motivo del fallimento e variazioni effettive di vita
     * @throws IllegalStateException se manca una partita attiva o il suo stato non consente il comando
     * @throws IllegalArgumentException se l'indice non identifica un nemico vivo
     * @throws it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza se il salvataggio fallisce dopo che
     *     l'azione ha portato la battaglia alla vittoria
     */
    public RisultatoAzione attacca(int indice) { return esegui(b -> b.attacca(indice)); }
    /**
     * Attiva l'abilità dell'eroe, usando l'indice solo se serve un bersaglio.
     * Se l'abilità conclude la battaglia con una vittoria, tenta di salvarla dopo aver applicato l'azione.
     *
     * @param indice indice nella sequenza originale, a partire da zero
     * @return esito dell’azione, con successo o motivo del fallimento e variazioni effettive di vita
     * @throws IllegalStateException se manca una partita attiva o il suo stato non consente il comando
     * @throws IllegalArgumentException se l'abilità richiede un bersaglio e l'indice non identifica un nemico vivo
     * @throws it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza se il salvataggio fallisce dopo che
     *     l'azione ha portato la battaglia alla vittoria
     */
    public RisultatoAzione usaAbilita(int indice) { return esegui(b -> b.usaAbilitaEroe(indice)); }
    /**
     * Attiva l'abilità dell'eroe senza selezionare un bersaglio. Restituisce un fallimento se serve un nemico.
     * Se l'abilità conclude la battaglia con una vittoria, tenta di salvarla dopo aver applicato l'azione.
     *
     * @return esito dell’azione, con successo o motivo del fallimento e variazioni effettive di vita
     * @throws IllegalStateException se manca una partita attiva o il suo stato non consente il comando
     * @throws it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza se il salvataggio fallisce dopo che
     *     l'azione ha portato la battaglia alla vittoria
     */
    public RisultatoAzione usaAbilita() { return esegui(Battaglia::usaAbilitaEroe); }
    /**
     * Tenta la cura dell’eroe usando gli utilizzi disponibili nel castello.
     *
     * @return esito dell’azione, con successo o motivo del fallimento e variazioni effettive di vita
     * @throws IllegalStateException se manca una partita attiva o il suo stato non consente il comando
     */
    public RisultatoAzione cura() { return esegui(Battaglia::curaEroe); }
    /**
     * Esegue la risposta di un solo nemico e aggiorna lo stato del combattimento.
     *
     * @return esito dell’azione, con successo o motivo del fallimento e variazioni effettive di vita
     * @throws IllegalStateException se manca una partita attiva o il suo stato non consente il comando
     */
    public RisultatoAzione eseguiTurnoNemico() { return esegui(Battaglia::eseguiTurnoNemico); }

    private RisultatoAzione esegui(Function<Battaglia, RisultatoAzione> comando) {
        Battaglia battaglia = richiediPartita().battagliaCorrente();
        RisultatoAzione risultato = comando.apply(battaglia);
        if (battaglia.stato() == StatoBattaglia.VITTORIA) salvaVittoria();
        return risultato;
    }

    /**
     * Registra la vittoria corrente una sola volta. In caso di errore di persistenza, consente di ritentare.
     *
     * @throws IllegalStateException se manca una partita o un salvataggio utilizzabile, oppure la battaglia non è
     *     stata vinta
     * @throws it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza se l’accesso al
     *     salvataggio non riesce
     */
    public void salvaVittoria() {
        Partita partita = richiediPartita();
        if (partita.battagliaCorrente().stato() != StatoBattaglia.VITTORIA) {
            throw new IllegalStateException("La battaglia corrente non è stata vinta");
        }
        if (!vittoriaSalvata) {
            progressione.registraVittoria(partita);
            vittoriaSalvata = true;
        }
    }

    /**
     * Salva la vittoria corrente e avanza nella campagna, completandola se si trova all’ultimo castello.
     *
     * @throws IllegalStateException se manca una partita attiva o il suo stato non consente il comando
     * @throws it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza se il salvataggio della vittoria
     *     non riesce
     */
    public void vaiAlCastelloSuccessivo() {
        salvaVittoria();
        richiediPartita().avanzaAlProssimoCastello();
        vittoriaSalvata = false;
    }
}
