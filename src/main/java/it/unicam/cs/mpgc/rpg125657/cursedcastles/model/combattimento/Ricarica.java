package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento;

/**
 * Rappresenta l’astrazione di una politica di ricarica.
 * Implementazioni diverse possono basarsi sui turni o su altre condizioni.
 */
public interface Ricarica {

    /**
     * Verifica se la ricarica consente un nuovo utilizzo.
     *
     * @return {@code true} se l’azione è disponibile
     */
    boolean isDisponibile();

    /**
     * Avvia il periodo di ricarica dopo un utilizzo.
     * @throws IllegalStateException se la ricarica non è disponibile
     */
    void utilizza();

    /**
     * Aggiorna lo stato della ricarica all’inizio di un nuovo turno dell’eroe.
     */
    void avanzaTurno();

    /**
     * Ripristina lo stato iniziale della ricarica.
     */
    void reset();

    /**
     * Restituisce i turni ancora necessari prima di un nuovo utilizzo.
     *
     * @return numero di turni residui, zero quando la ricarica è pronta
     */
    int turniRimanenti();
}
