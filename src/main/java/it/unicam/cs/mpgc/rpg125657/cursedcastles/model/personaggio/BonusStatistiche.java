package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio;

import java.util.Objects;

/** Stato accumulabile dei potenziamenti di un singolo personaggio. */
public final class BonusStatistiche {

    private int attaccoPercentuale;
    private int difesaPercentuale;

    public void applica(ModificatoreStatistiche modificatore) {
        Objects.requireNonNull(modificatore, "Il modificatore non può essere null");
        int nuovoAttaccoPercentuale = Math.addExact(
                attaccoPercentuale,
                modificatore.bonusAttaccoPercentuale()
        );
        int nuovaDifesaPercentuale = Math.addExact(
                difesaPercentuale,
                modificatore.bonusDifesaPercentuale()
        );
        attaccoPercentuale = nuovoAttaccoPercentuale;
        difesaPercentuale = nuovaDifesaPercentuale;
    }

    public int calcolaAttacco(int valoreBase) {
        return applicaPercentuale(valoreBase, attaccoPercentuale);
    }

    public int calcolaDifesa(int valoreBase) {
        return applicaPercentuale(valoreBase, difesaPercentuale);
    }

    public int attaccoPercentuale() {
        return attaccoPercentuale;
    }

    public int difesaPercentuale() {
        return difesaPercentuale;
    }

    public void reset() {
        attaccoPercentuale = 0;
        difesaPercentuale = 0;
    }

    private int applicaPercentuale(int valoreBase, int percentuale) {
        long valoreCalcolato = Math.round(valoreBase * (100L + percentuale) / 100.0);
        return Math.toIntExact(valoreCalcolato);
    }
}
