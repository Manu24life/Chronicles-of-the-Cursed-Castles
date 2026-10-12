package it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.progressione;

import java.util.Optional;

/** Porta di persistenza dell'unico slot, indipendente dalla tecnologia usata. */
public interface ProgressiRepository {

    Optional<ProgressiPartita> carica();

    /** Crea o sostituisce integralmente il salvataggio in una singola transazione. */
    void sostituisci(ProgressiPartita progressi);

    void elimina();
}
