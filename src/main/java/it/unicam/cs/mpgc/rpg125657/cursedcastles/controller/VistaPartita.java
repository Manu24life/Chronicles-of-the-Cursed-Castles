package it.unicam.cs.mpgc.rpg125657.cursedcastles.controller;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.partita.StatoPartita;

import java.util.Objects;
import java.util.Optional;

/** Stato completo e immutabile esposto dal Controller alla futura View. */
public record VistaPartita(
        StatoPartita statoPartita,
        StatoEroe eroe,
        Optional<StatoCastello> castello
) {

    public VistaPartita {
        Objects.requireNonNull(statoPartita, "Lo stato della partita non può essere null");
        Objects.requireNonNull(eroe, "Lo stato dell'eroe non può essere null");
        Objects.requireNonNull(castello, "Lo stato del castello non può essere null");
    }
}
