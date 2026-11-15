package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.partita;

/**
 * Rappresenta le fasi del ciclo di vita della campagna.
 */
public enum StatoPartita {
    /** Indica una campagna configurata e non ancora avviata. */
    NON_INIZIATA,
    /** Indica una campagna con un castello attivo. */
    IN_CORSO,
    /** Indica il completamento dell’intera campagna. */
    COMPLETATA,
    /** Indica la sconfitta dell’eroe. */
    SCONFITTA
}
