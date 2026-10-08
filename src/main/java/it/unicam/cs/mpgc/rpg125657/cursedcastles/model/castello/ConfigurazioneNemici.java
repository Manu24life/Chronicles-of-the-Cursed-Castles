package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro.TipoMostro;

import java.util.Objects;

/** Descrive quanti nemici di un certo tipo sono presenti in un castello. */
public record ConfigurazioneNemici(TipoMostro tipo, int quantita) {

    public ConfigurazioneNemici {
        Objects.requireNonNull(tipo, "Il tipo di mostro non può essere null");
        if (quantita <= 0) {
            throw new IllegalArgumentException("La quantità deve essere positiva");
        }
    }
}
