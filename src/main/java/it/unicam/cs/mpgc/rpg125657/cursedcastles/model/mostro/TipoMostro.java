package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.Statistiche;

import java.util.Objects;

/**
 * Rappresenta la configurazione immutabile di un tipo di nemico.
 *
 * @param codice identificativo stabile usato per riconoscere la configurazione
 * @param nome nome visualizzato
 * @param statistiche statistiche base immutabili del personaggio
 */
public record TipoMostro(String codice, String nome, Statistiche statistiche) {

    /**
     * Crea la configurazione ricavando il codice stabile dal nome fornito.
     *
     * @param nome nome visualizzato
     * @param statistiche statistiche base immutabili del personaggio
     */
    public TipoMostro(String nome, Statistiche statistiche) {
        this(codiceDaNome(nome), nome, statistiche);
    }

    /**
     * Inizializza i dati immutabili di {@code TipoMostro}, verificando i vincoli dei componenti.
     *
     * @param codice identificativo stabile usato per riconoscere la configurazione
     * @param nome nome visualizzato
     * @param statistiche statistiche base immutabili del personaggio
     * @throws NullPointerException se almeno uno tra {@code codice}, {@code nome}, {@code statistiche} è
     *     {@code null}
     * @throws IllegalArgumentException se il nome è vuoto o il codice non rispetta il formato di lettere minuscole,
     *     numeri e trattini
     */
    public TipoMostro {
        Objects.requireNonNull(codice, "Il codice del tipo non può essere null");
        Objects.requireNonNull(nome, "Il nome del tipo non può essere null");
        Objects.requireNonNull(statistiche, "Le statistiche non possono essere null");
        if (codice.isBlank() || !codice.matches("[a-z0-9]+(?:-[a-z0-9]+)*")) {
            throw new IllegalArgumentException("Il codice deve usare lettere minuscole, numeri e trattini");
        }
        if (nome.isBlank()) {
            throw new IllegalArgumentException("Il nome del tipo non può essere vuoto");
        }
        nome = nome.strip();
    }

    private static String codiceDaNome(String nome) {
        Objects.requireNonNull(nome, "Il nome del tipo non può essere null");
        return nome.strip().toLowerCase(java.util.Locale.ROOT).replaceAll("[^a-z0-9]+", "-");
    }
}
