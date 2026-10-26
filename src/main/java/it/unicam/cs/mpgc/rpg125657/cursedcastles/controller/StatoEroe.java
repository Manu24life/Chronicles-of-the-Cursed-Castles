package it.unicam.cs.mpgc.rpg125657.cursedcastles.controller;

import java.util.Objects;

/** Snapshot immutabile dell'eroe controllato dal giocatore. */
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

    public StatoEroe {
        Objects.requireNonNull(nome, "Il nome dell'eroe non può essere null");
        Objects.requireNonNull(codiceClasse, "Il codice della classe non può essere null");
        Objects.requireNonNull(nomeClasse, "Il nome della classe non può essere null");
        if (puntiVita < 0 || puntiVitaMassimi <= 0 || puntiVita > puntiVitaMassimi) {
            throw new IllegalArgumentException("I punti vita non sono validi");
        }
    }
}
