package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento;

import java.util.Objects;

/**
 * Rappresenta le informazioni prodotte da un'azione e utilizzabili anche dall'interfaccia grafica.
 *
 * @param riuscita indicatore di successo dell'azione
 * @param dannoInflitto danno effettivo, non negativo
 * @param puntiVitaRecuperati cura effettiva, non negativa
 * @param descrizione testo descrittivo dell'esito
 * @param codice codice coerente con il successo o il fallimento dell’azione
 */
public record RisultatoAzione(
        boolean riuscita,
        int dannoInflitto,
        int puntiVitaRecuperati,
        String descrizione,
        EsitoAzione codice
) {
    /**
     * Inizializza i dati immutabili di {@code RisultatoAzione}, verificando i vincoli dei componenti.
     *
     * @param riuscita indicatore di successo dell'azione
     * @param dannoInflitto danno effettivo, non negativo
     * @param puntiVitaRecuperati cura effettiva, non negativa
     * @param descrizione testo descrittivo dell'esito
     */
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

    /**
     * Inizializza i dati immutabili di {@code RisultatoAzione}, verificando i vincoli dei componenti.
     *
     * @param riuscita indicatore di successo dell'azione
     * @param dannoInflitto danno effettivo, non negativo
     * @param puntiVitaRecuperati cura effettiva, non negativa
     * @param descrizione testo descrittivo dell'esito
     * @param codice codice coerente con il successo o il fallimento dell’azione
     * @throws NullPointerException se almeno uno tra {@code descrizione}, {@code codice} è {@code null}
     * @throws IllegalArgumentException se danno, cura e codice non sono coerenti con l’esito dichiarato
     */
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

    /**
     * Crea un esito riuscito con danno e cura effettivi e codice {@link EsitoAzione#SUCCESSO}.
     *
     * @param dannoInflitto danno effettivo, non negativo
     * @param puntiVitaRecuperati cura effettiva, non negativa
     * @param descrizione testo descrittivo dell'esito
     * @return esito dell’azione, con successo o motivo del fallimento e variazioni effettive di vita
     */
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

    /**
     * Crea un esito fallito senza danno o cura, associato a un codice diverso da {@link EsitoAzione#SUCCESSO}.
     *
     * @param codice codice coerente con il successo o il fallimento dell’azione
     * @param descrizione testo descrittivo dell'esito
     * @return esito dell’azione, con successo o motivo del fallimento e variazioni effettive di vita
     * @throws IllegalArgumentException se il codice è {@link EsitoAzione#SUCCESSO}
     */
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
