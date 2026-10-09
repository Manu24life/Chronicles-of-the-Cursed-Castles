package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio;

/** Capacità di ricevere bonus permanenti e accumulabili. */
public interface Potenziabile {

    void applicaBonus(ModificatoreStatistiche modificatore);

    int bonusAttaccoPercentuale();

    int bonusDifesaPercentuale();

    void azzeraBonus();
}
