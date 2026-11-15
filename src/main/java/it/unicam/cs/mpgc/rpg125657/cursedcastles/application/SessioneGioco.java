package it.unicam.cs.mpgc.rpg125657.cursedcastles.application;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.partita.Partita;

import java.util.Objects;
import java.util.Optional;

/** Mantiene la partita runtime gestita dal servizio applicativo, senza contenere regole di gioco. */
final class SessioneGioco {

    private Partita partita;

    /**
     * Restituisce la partita attiva, se presente.
     *
     * @return partita attiva, oppure {@link java.util.Optional#empty()} se la sessione è vuota
     */
    public Optional<Partita> partita() {
        return Optional.ofNullable(partita);
    }

    /**
     * Restituisce la partita attiva, richiedendone la presenza.
     *
     * @return partita attiva
     * @throws IllegalStateException se non esiste una partita attiva
     */
    public Partita richiediPartita() {
        if (partita == null) {
            throw new IllegalStateException("Non esiste una partita attiva");
        }
        return partita;
    }

    /**
     * Sostituisce la partita attiva con quella indicata.
     *
     * @param partita partita o snapshot da elaborare
     * @throws NullPointerException se almeno uno tra {@code partita} è {@code null}
     */
    public void imposta(Partita partita) {
        this.partita = Objects.requireNonNull(partita, "La partita non può essere null");
    }

    /**
     * Rimuove la partita dalla sessione.
     *
     */
    public void termina() {
        partita = null;
    }
}
