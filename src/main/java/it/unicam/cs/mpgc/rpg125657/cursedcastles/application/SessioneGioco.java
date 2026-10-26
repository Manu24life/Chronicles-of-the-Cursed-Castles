package it.unicam.cs.mpgc.rpg125657.cursedcastles.application;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.partita.Partita;

import java.util.Objects;
import java.util.Optional;

/** Mantiene la partita runtime gestita dal servizio applicativo, senza contenere regole di gioco. */
final class SessioneGioco {

    private Partita partita;

    public Optional<Partita> partita() {
        return Optional.ofNullable(partita);
    }

    public Partita richiediPartita() {
        if (partita == null) {
            throw new IllegalStateException("Non esiste una partita attiva");
        }
        return partita;
    }

    public void imposta(Partita partita) {
        this.partita = Objects.requireNonNull(partita, "La partita non può essere null");
    }

    public void termina() {
        partita = null;
    }
}
