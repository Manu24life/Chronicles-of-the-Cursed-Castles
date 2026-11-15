package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.Combattente;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.RisultatoAzione;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.Eroe;

/**
 * Rappresenta una strategia per un'abilità posseduta da un eroe.
 */
public interface AbilitaEroe {

    /**
     * Restituisce il nome dell’abilità.
     *
     * @return nome dell’abilità
     */
    String nome();

    /**
     * Tenta l’attivazione dell’abilità sull’eroe e sul bersaglio indicati.
     *
     * @param utilizzatore eroe che attiva l'abilità
     * @param bersaglio combattente destinatario dell'effetto
     * @return esito dell’azione, con successo o motivo del fallimento e variazioni effettive di vita
     */
    RisultatoAzione usa(Eroe utilizzatore, Combattente bersaglio);

    /**
     * Verifica se la ricarica consente un nuovo utilizzo.
     *
     * @return {@code true} se l’azione è disponibile
     */
    boolean isDisponibile();

    /**
     * Restituisce i turni ancora necessari prima di un nuovo utilizzo.
     *
     * @return numero di turni residui, zero quando la ricarica è pronta
     */
    int turniRimanenti();

    /**
     * Aggiorna lo stato della ricarica all’inizio di un nuovo turno dell’eroe.
     */
    void avanzaTurno();

    /**
     * Ripristina lo stato iniziale dell’abilità.
     */
    void reset();

    /**
     * Indica se l’abilità richiede la selezione di un nemico.
     *
     * @return {@code true} se deve essere selezionato un nemico
     */
    default boolean richiedeBersaglio() {
        return true;
    }
}
