package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.Statistiche;

import java.util.Objects;

/** Configurazione immutabile di un tipo di nemico. */
public record TipoMostro(String nome, Statistiche statistiche) {

    public TipoMostro {
        Objects.requireNonNull(nome, "Il nome del tipo non può essere null");
        Objects.requireNonNull(statistiche, "Le statistiche non possono essere null");
        if (nome.isBlank()) {
            throw new IllegalArgumentException("Il nome del tipo non può essere vuoto");
        }
        nome = nome.strip();
    }
}
