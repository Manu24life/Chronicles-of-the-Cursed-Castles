package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio;

/** Incrementi percentuali da applicare alle statistiche correnti. */
public record ModificatoreStatistiche(
        int bonusAttaccoPercentuale,
        int bonusDifesaPercentuale
) {

    public ModificatoreStatistiche {
        if (bonusAttaccoPercentuale < 0 || bonusDifesaPercentuale < 0) {
            throw new IllegalArgumentException("I bonus percentuali non possono essere negativi");
        }
    }
}
