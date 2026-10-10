package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Objects;

/** Configurazione immutabile di un castello e del suo gruppo di nemici. */
@Getter
@Accessors(fluent = true)
public final class Castello {

    private final String codice;
    private final String nome;
    private final List<ConfigurazioneNemici> configurazioni;

    public Castello(String nome, List<ConfigurazioneNemici> configurazioni) {
        this(codiceDaNome(nome), nome, configurazioni);
    }

    public Castello(String codice, String nome, List<ConfigurazioneNemici> configurazioni) {
        Objects.requireNonNull(codice, "Il codice non può essere null");
        if (codice.isBlank() || !codice.matches("[a-z0-9]+(?:-[a-z0-9]+)*")) {
            throw new IllegalArgumentException("Il codice deve usare lettere minuscole, numeri e trattini");
        }
        Objects.requireNonNull(nome, "Il nome non può essere null");
        if (nome.isBlank()) {
            throw new IllegalArgumentException("Il nome non può essere vuoto");
        }
        Objects.requireNonNull(configurazioni, "Le configurazioni non possono essere null");
        if (configurazioni.isEmpty()) {
            throw new IllegalArgumentException("Un castello deve contenere almeno un nemico");
        }
        this.codice = codice;
        this.nome = nome.strip();
        this.configurazioni = List.copyOf(configurazioni);
    }

    private static String codiceDaNome(String nome) {
        Objects.requireNonNull(nome, "Il nome non può essere null");
        return nome.strip().toLowerCase(java.util.Locale.ROOT).replaceAll("[^a-z0-9]+", "-");
    }
}
