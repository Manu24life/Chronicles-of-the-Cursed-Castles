package it.unicam.cs.mpgc.rpg125657.cursedcastles.controller;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Snapshot immutabile dei dati richiesti dalla schermata iniziale. */
public record StatoMenu(
        List<ClasseDisponibile> classiDisponibili,
        Optional<RiepilogoSalvataggio> salvataggio
) {

    public StatoMenu {
        Objects.requireNonNull(classiDisponibili, "Le classi non possono essere null");
        Objects.requireNonNull(salvataggio, "Il salvataggio non può essere null");
        classiDisponibili = List.copyOf(classiDisponibili);
    }
}
