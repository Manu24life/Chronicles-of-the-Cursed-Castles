package it.unicam.cs.mpgc.rpg125657.cursedcastles.controller;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.ClasseEroe;

import java.util.Objects;

/** Dati immutabili di una classe selezionabile dal menu. */
public record ClasseDisponibile(
        String codice,
        String nome,
        int puntiVitaMassimi,
        int attacco,
        int difesa
) {

    public ClasseDisponibile {
        Objects.requireNonNull(codice, "Il codice non può essere null");
        Objects.requireNonNull(nome, "Il nome non può essere null");
        if (puntiVitaMassimi <= 0 || attacco < 0 || difesa < 0) {
            throw new IllegalArgumentException("Le statistiche della classe non sono valide");
        }
    }

    static ClasseDisponibile da(ClasseEroe classe) {
        Objects.requireNonNull(classe, "La classe non può essere null");
        return new ClasseDisponibile(
                classe.codice(),
                classe.nome(),
                classe.statistiche().puntiVitaMassimi(),
                classe.statistiche().attacco(),
                classe.statistiche().difesa()
        );
    }
}
