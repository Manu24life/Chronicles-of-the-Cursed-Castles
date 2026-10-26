package it.unicam.cs.mpgc.rpg125657.cursedcastles.controller;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.RisultatoAzione;

import java.util.Objects;

/** Risultato di un comando di combattimento insieme allo stato aggiornato. */
public record EsitoComando(RisultatoAzione risultato, VistaPartita stato) {

    public EsitoComando {
        Objects.requireNonNull(risultato, "Il risultato non può essere null");
        Objects.requireNonNull(stato, "Lo stato non può essere null");
    }
}
