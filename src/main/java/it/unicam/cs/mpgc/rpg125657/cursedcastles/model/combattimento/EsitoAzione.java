package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento;

/**
 * Rappresenta un codice stabile che permette alla View di tradurre un risultato senza analizzare testi.
 */
public enum EsitoAzione {
    /** Indica un’azione eseguita con successo. */
    SUCCESSO,
    /** Indica un’azione non ammessa nelle condizioni correnti. */
    AZIONE_NON_CONSENTITA,
    /** Indica che il bersaglio selezionato è già sconfitto. */
    BERSAGLIO_SCONFITTO,
    /** Indica un’abilità non ancora riutilizzabile. */
    ABILITA_IN_RICARICA,
    /** Indica una cura superflua perché la vita è al massimo. */
    VITA_GIA_PIENA,
    /** Indica l’esaurimento delle cure del castello. */
    CURE_ESAURITE
}
