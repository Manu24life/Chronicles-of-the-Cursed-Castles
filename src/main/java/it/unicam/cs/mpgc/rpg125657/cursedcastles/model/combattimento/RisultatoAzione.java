package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento;

import java.util.Objects;

/** Informazioni prodotte da un'azione e utilizzabili anche dall'interfaccia grafica. */
public record RisultatoAzione(
        boolean riuscita,
        int dannoInflitto,
        int puntiVitaRecuperati,
        String descrizione,
        EsitoAzione codice
) {
    public RisultatoAzione(
            boolean riuscita,
            int dannoInflitto,
            int puntiVitaRecuperati,
            String descrizione
    ) {
        this(
                riuscita,
                dannoInflitto,
                puntiVitaRecuperati,
                descrizione,
                riuscita
                        ? EsitoAzione.SUCCESSO
                        : EsitoAzione.AZIONE_NON_CONSENTITA
        );
    }

    public RisultatoAzione {
        if (dannoInflitto < 0 || puntiVitaRecuperati < 0) {
            throw new IllegalArgumentException("Danno e cura non possono essere negativi");
        }
        if (!riuscita && (dannoInflitto != 0 || puntiVitaRecuperati != 0)) {
            throw new IllegalArgumentException("Un'azione fallita non può modificare il combattimento");
        }
        Objects.requireNonNull(descrizione, "La descrizione non può essere null");
        Objects.requireNonNull(codice, "Il codice non può essere null");
        if (riuscita != (codice == EsitoAzione.SUCCESSO)) {
            throw new IllegalArgumentException("Il codice deve essere coerente con l'esito");
        }
    }

    public static RisultatoAzione successo(
            int dannoInflitto,
            int puntiVitaRecuperati,
            String descrizione
    ) {
        return new RisultatoAzione(
                true,
                dannoInflitto,
                puntiVitaRecuperati,
                descrizione,
                EsitoAzione.SUCCESSO
        );
    }

    public static RisultatoAzione fallimento(
            EsitoAzione codice,
            String descrizione
    ) {
        if (codice == EsitoAzione.SUCCESSO) {
            throw new IllegalArgumentException("Un fallimento richiede un codice di errore");
        }
        return new RisultatoAzione(false, 0, 0, descrizione, codice);
    }
}
