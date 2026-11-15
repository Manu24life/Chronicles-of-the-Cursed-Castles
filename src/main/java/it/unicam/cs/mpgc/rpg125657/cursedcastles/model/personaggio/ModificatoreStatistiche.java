package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio;

/**
 * Rappresenta gli incrementi percentuali da applicare alle statistiche correnti.
 *
 * @param bonusAttaccoPercentuale incremento percentuale accumulato dell'attacco
 * @param bonusDifesaPercentuale incremento percentuale accumulato della difesa
 */
public record ModificatoreStatistiche(
        int bonusAttaccoPercentuale,
        int bonusDifesaPercentuale
) {

    /**
     * Inizializza i dati immutabili di {@code ModificatoreStatistiche}, verificando i vincoli dei componenti.
     *
     * @param bonusAttaccoPercentuale incremento percentuale accumulato dell'attacco
     * @param bonusDifesaPercentuale incremento percentuale accumulato della difesa
     * @throws IllegalArgumentException se almeno un bonus percentuale è negativo
     */
    public ModificatoreStatistiche {
        if (bonusAttaccoPercentuale < 0 || bonusDifesaPercentuale < 0) {
            throw new IllegalArgumentException("I bonus percentuali non possono essere negativi");
        }
    }
}
