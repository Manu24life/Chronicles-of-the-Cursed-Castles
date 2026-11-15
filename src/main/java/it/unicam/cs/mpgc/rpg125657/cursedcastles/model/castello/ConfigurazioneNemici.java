package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro.TipoMostro;

import java.util.Objects;

/**
 * Descrive quanti nemici di un certo tipo sono presenti in un castello.
 *
 * @param tipo archetipo del mostro
 * @param quantita numero strettamente positivo di mostri da creare
 */
public record ConfigurazioneNemici(TipoMostro tipo, int quantita) {

    /**
     * Inizializza i dati immutabili di {@code ConfigurazioneNemici}, verificando i vincoli dei componenti.
     *
     * @param tipo archetipo del mostro
     * @param quantita numero strettamente positivo di mostri da creare
     * @throws NullPointerException se almeno uno tra {@code tipo} è {@code null}
     * @throws IllegalArgumentException se la quantità non è positiva
     */
    public ConfigurazioneNemici {
        Objects.requireNonNull(tipo, "Il tipo di mostro non può essere null");
        if (quantita <= 0) {
            throw new IllegalArgumentException("La quantità deve essere positiva");
        }
    }
}
