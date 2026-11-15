package it.unicam.cs.mpgc.rpg125657.cursedcastles.controller;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.partita.StatoPartita;

import java.util.Objects;
import java.util.Optional;

/**
 * Rappresenta lo stato completo e immutabile esposto dal controller alla vista.
 *
 * @param statoPartita fase corrente della campagna
 * @param eroe eroe che affronta la campagna
 * @param castello castello da rappresentare o di cui creare i nemici
 */
public record VistaPartita(
        StatoPartita statoPartita,
        StatoEroe eroe,
        Optional<StatoCastello> castello
) {

    /**
     * Inizializza i dati immutabili di {@code VistaPartita}, verificando i vincoli dei componenti.
     *
     * @param statoPartita fase corrente della campagna
     * @param eroe eroe che affronta la campagna
     * @param castello castello da rappresentare o di cui creare i nemici
     * @throws NullPointerException se almeno uno tra {@code statoPartita}, {@code eroe}, {@code castello} è
     *     {@code null}
     */
    public VistaPartita {
        Objects.requireNonNull(statoPartita, "Lo stato della partita non può essere null");
        Objects.requireNonNull(eroe, "Lo stato dell'eroe non può essere null");
        Objects.requireNonNull(castello, "Lo stato del castello non può essere null");
    }
}
