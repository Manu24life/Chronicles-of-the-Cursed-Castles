package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.Statistiche;

import java.util.Objects;

/** Archetipo immutabile: due eroi della stessa classe condividono questi valori. */
public record ClasseEroe(String nome, Statistiche statistiche) {

    public ClasseEroe {
        Objects.requireNonNull(nome, "Il nome della classe non può essere null");
        Objects.requireNonNull(statistiche, "Le statistiche non possono essere null");
        if (nome.isBlank()) {
            throw new IllegalArgumentException("Il nome della classe non può essere vuoto");
        }
        nome = nome.strip();
    }
}
