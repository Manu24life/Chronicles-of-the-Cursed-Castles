package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento;

/**
 * Rappresenta il turno attivo e gli esiti conclusivi del combattimento.
 */
public enum StatoBattaglia {
    /** Indica l’attesa di un’azione dell’eroe. */
    TURNO_EROE,
    /** Indica l’attesa della risposta di un solo nemico. */
    TURNO_NEMICI,
    /** Indica la sconfitta di tutti i nemici della battaglia. */
    VITTORIA,
    /** Indica la sconfitta dell’eroe. */
    SCONFITTA
}
