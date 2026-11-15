package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio;

/**
 * Rappresenta la capacità di ricevere bonus permanenti e accumulabili.
 */
public interface Potenziabile {

    /**
     * Somma i modificatori percentuali ai bonus già accumulati.
     *
     * @param modificatore incrementi percentuali da sommare ai bonus esistenti
     */
    void applicaBonus(ModificatoreStatistiche modificatore);

    /**
     * Restituisce il bonus percentuale complessivo di attacco.
     *
     * @return bonus percentuale di attacco
     */
    int bonusAttaccoPercentuale();

    /**
     * Restituisce il bonus percentuale complessivo di difesa.
     *
     * @return bonus percentuale di difesa
     */
    int bonusDifesaPercentuale();

    /**
     * Azzera tutti i bonus percentuali accumulati.
     */
    void azzeraBonus();
}
