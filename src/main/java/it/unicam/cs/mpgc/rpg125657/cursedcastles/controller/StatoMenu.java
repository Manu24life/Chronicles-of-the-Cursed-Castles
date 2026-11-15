package it.unicam.cs.mpgc.rpg125657.cursedcastles.controller;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Rappresenta uno snapshot immutabile dei dati richiesti dalla schermata iniziale.
 *
 * @param classiDisponibili elenco delle classi selezionabili, copiato in forma immutabile
 * @param salvataggio riepilogo del salvataggio, oppure {@link java.util.Optional#empty()} se assente
 */
public record StatoMenu(
        List<ClasseDisponibile> classiDisponibili,
        Optional<RiepilogoSalvataggio> salvataggio
) {

    /**
     * Inizializza i dati immutabili di {@code StatoMenu}, verificando i vincoli dei componenti.
     *
     * @param classiDisponibili elenco delle classi selezionabili, copiato in forma immutabile
     * @param salvataggio riepilogo del salvataggio, oppure {@link java.util.Optional#empty()} se assente
     * @throws NullPointerException se almeno uno tra {@code classiDisponibili}, {@code salvataggio} è {@code null}
     */
    public StatoMenu {
        Objects.requireNonNull(classiDisponibili, "Le classi non possono essere null");
        Objects.requireNonNull(salvataggio, "Il salvataggio non può essere null");
        classiDisponibili = List.copyOf(classiDisponibili);
    }
}
