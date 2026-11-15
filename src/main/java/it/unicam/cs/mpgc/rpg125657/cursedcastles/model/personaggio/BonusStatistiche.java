package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio;

import java.util.Objects;

/**
 * Rappresenta lo stato accumulabile dei potenziamenti di un singolo personaggio.
 */
public final class BonusStatistiche {

    private int attaccoPercentuale;
    private int difesaPercentuale;

    /**
     * Somma i bonus percentuali senza modificare le statistiche base del personaggio.
     *
     * @param modificatore incrementi percentuali da sommare ai bonus esistenti
     * @throws NullPointerException se almeno uno tra {@code modificatore} è {@code null}
     * @throws ArithmeticException se la somma dei bonus supera il limite di un intero; i bonus restano invariati
     */
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

    /**
     * Calcola l’attacco aumentato dei bonus accumulati, arrotondando all’intero più vicino.
     *
     * @param valoreBase statistica base a cui applicare il bonus percentuale
     * @return attacco aumentato e arrotondato
     * @throws ArithmeticException se il risultato non è rappresentabile come intero
     */
    public int calcolaAttacco(int valoreBase) {
        return applicaPercentuale(valoreBase, attaccoPercentuale);
    }

    /**
     * Calcola la difesa aumentata dei bonus accumulati, arrotondando all’intero più vicino.
     *
     * @param valoreBase statistica base a cui applicare il bonus percentuale
     * @return difesa aumentata e arrotondata
     * @throws ArithmeticException se il risultato non è rappresentabile come intero
     */
    public int calcolaDifesa(int valoreBase) {
        return applicaPercentuale(valoreBase, difesaPercentuale);
    }

    /**
     * Restituisce la percentuale di bonus accumulata per l’attacco.
     *
     * @return bonus percentuale di attacco
     */
    public int attaccoPercentuale() {
        return attaccoPercentuale;
    }

    /**
     * Restituisce la percentuale di bonus accumulata per la difesa.
     *
     * @return bonus percentuale di difesa
     */
    public int difesaPercentuale() {
        return difesaPercentuale;
    }

    /**
     * Azzera i bonus percentuali accumulati per attacco e difesa.
     *
     */
    public void reset() {
        attaccoPercentuale = 0;
        difesaPercentuale = 0;
    }

    private int applicaPercentuale(int valoreBase, int percentuale) {
        long valoreCalcolato = Math.round(valoreBase * (100L + percentuale) / 100.0);
        return Math.toIntExact(valoreCalcolato);
    }
}
