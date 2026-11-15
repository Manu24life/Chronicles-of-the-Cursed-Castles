package it.unicam.cs.mpgc.rpg125657.cursedcastles.controller;

import java.util.Objects;

/**
 * Rappresenta uno snapshot immutabile dell'eroe controllato dal giocatore.
 *
 * @param nome nome visualizzato
 * @param codiceClasse identificativo della classe dell'eroe
 * @param nomeClasse nome visualizzato della classe dell'eroe
 * @param puntiVita punti vita attuali
 * @param puntiVitaMassimi limite massimo dei punti vita, strettamente positivo
 * @param attacco valore di attacco
 * @param difesa valore di difesa
 * @param bonusAttaccoPercentuale incremento percentuale accumulato dell'attacco
 * @param bonusDifesaPercentuale incremento percentuale accumulato della difesa
 */
public record StatoEroe(
        String nome,
        String codiceClasse,
        String nomeClasse,
        int puntiVita,
        int puntiVitaMassimi,
        int attacco,
        int difesa,
        int bonusAttaccoPercentuale,
        int bonusDifesaPercentuale
) {

    /**
     * Inizializza i dati immutabili di {@code StatoEroe}, verificando i vincoli dei componenti.
     *
     * @param nome nome visualizzato
     * @param codiceClasse identificativo della classe dell'eroe
     * @param nomeClasse nome visualizzato della classe dell'eroe
     * @param puntiVita punti vita attuali
     * @param puntiVitaMassimi limite massimo dei punti vita, strettamente positivo
     * @param attacco valore di attacco
     * @param difesa valore di difesa
     * @param bonusAttaccoPercentuale incremento percentuale accumulato dell'attacco
     * @param bonusDifesaPercentuale incremento percentuale accumulato della difesa
     * @throws NullPointerException se almeno uno tra {@code nome}, {@code codiceClasse}, {@code nomeClasse} è
     *     {@code null}
     * @throws IllegalArgumentException se i punti vita non rientrano nei limiti indicati
     */
    public StatoEroe {
        Objects.requireNonNull(nome, "Il nome dell'eroe non può essere null");
        Objects.requireNonNull(codiceClasse, "Il codice della classe non può essere null");
        Objects.requireNonNull(nomeClasse, "Il nome della classe non può essere null");
        if (puntiVita < 0 || puntiVitaMassimi <= 0 || puntiVita > puntiVitaMassimi) {
            throw new IllegalArgumentException("I punti vita non sono validi");
        }
    }
}
