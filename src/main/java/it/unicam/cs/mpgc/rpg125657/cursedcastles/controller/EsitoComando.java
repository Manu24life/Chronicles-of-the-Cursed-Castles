package it.unicam.cs.mpgc.rpg125657.cursedcastles.controller;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.RisultatoAzione;

import java.util.Objects;

/**
 * Rappresenta il risultato di un comando di combattimento insieme allo stato aggiornato.
 *
 * @param risultato esito dell'azione appena eseguita
 * @param stato snapshot aggiornato della partita
 */
public record EsitoComando(RisultatoAzione risultato, VistaPartita stato) {

    /**
     * Inizializza i dati immutabili di {@code EsitoComando}, verificando i vincoli dei componenti.
     *
     * @param risultato esito dell'azione appena eseguita
     * @param stato snapshot aggiornato della partita
     * @throws NullPointerException se almeno uno tra {@code risultato}, {@code stato} è {@code null}
     */
    public EsitoComando {
        Objects.requireNonNull(risultato, "Il risultato non può essere null");
        Objects.requireNonNull(stato, "Lo stato non può essere null");
    }
}
