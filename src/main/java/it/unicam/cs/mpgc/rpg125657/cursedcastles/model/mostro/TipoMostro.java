package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.Statistiche;

import java.util.Objects;

/** Configurazione immutabile di un tipo di nemico. */
public record TipoMostro(String codice, String nome, Statistiche statistiche) {

    public TipoMostro(String nome, Statistiche statistiche) {
        this(codiceDaNome(nome), nome, statistiche);
    }

    public TipoMostro {
        Objects.requireNonNull(codice, "Il codice del tipo non può essere null");
        Objects.requireNonNull(nome, "Il nome del tipo non può essere null");
        Objects.requireNonNull(statistiche, "Le statistiche non possono essere null");
        if (codice.isBlank() || !codice.matches("[a-z0-9]+(?:-[a-z0-9]+)*")) {
            throw new IllegalArgumentException("Il codice deve usare lettere minuscole, numeri e trattini");
        }
        if (nome.isBlank()) {
            throw new IllegalArgumentException("Il nome del tipo non può essere vuoto");
        }
        nome = nome.strip();
    }

    private static String codiceDaNome(String nome) {
        Objects.requireNonNull(nome, "Il nome del tipo non può essere null");
        return nome.strip().toLowerCase(java.util.Locale.ROOT).replaceAll("[^a-z0-9]+", "-");
    }
}
