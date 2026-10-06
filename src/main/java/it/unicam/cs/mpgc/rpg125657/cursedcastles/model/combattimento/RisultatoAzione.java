package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento;

import java.util.Objects;

/** Informazioni prodotte da un'azione e utilizzabili anche dall'interfaccia grafica. */
public record RisultatoAzione(
        boolean riuscita,
        int dannoInflitto,
        int puntiVitaRecuperati,
        String descrizione
) {
    public RisultatoAzione {
        if (dannoInflitto < 0 || puntiVitaRecuperati < 0) {
            throw new IllegalArgumentException("Danno e cura non possono essere negativi");
        }
        Objects.requireNonNull(descrizione, "La descrizione non può essere null");
    }
}
