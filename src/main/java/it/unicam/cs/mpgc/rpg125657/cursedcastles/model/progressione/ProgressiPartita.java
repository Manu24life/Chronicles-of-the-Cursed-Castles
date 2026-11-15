package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.progressione;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Rappresenta i dati durevoli dell'unico slot di salvataggio.
 *
 * @param nomeEroe nome dell'eroe, ripulito dagli spazi iniziali e finali
 * @param codiceClasse identificativo della classe dell'eroe
 * @param codiciCastelliCompletati insieme dei codici dei castelli già completati, copiato in forma immutabile
 */
public record ProgressiPartita(
        String nomeEroe,
        String codiceClasse,
        Set<String> codiciCastelliCompletati
) {

    private static final int LUNGHEZZA_MASSIMA_NOME = 100;
    private static final String FORMATO_CODICE = "[a-z0-9]+(?:-[a-z0-9]+)*";

    /**
     * Inizializza i dati immutabili di {@code ProgressiPartita}, verificando i vincoli dei componenti.
     *
     * @param nomeEroe nome dell'eroe, ripulito dagli spazi iniziali e finali
     * @param codiceClasse identificativo della classe dell'eroe
     * @param codiciCastelliCompletati insieme dei codici dei castelli già completati, copiato in forma immutabile
     * @throws NullPointerException se almeno uno tra {@code nomeEroe}, {@code codiceClasse},
     *     {@code codiciCastelliCompletati} è {@code null}
     * @throws IllegalArgumentException se il nome è vuoto o supera 100 caratteri, oppure i codici non rispettano il
     *     formato o contengono null
     */
    public ProgressiPartita {
        Objects.requireNonNull(nomeEroe, "Il nome dell'eroe non può essere null");
        Objects.requireNonNull(codiceClasse, "Il codice della classe non può essere null");
        Objects.requireNonNull(
                codiciCastelliCompletati,
                "I codici dei castelli completati non possono essere null"
        );

        nomeEroe = nomeEroe.strip();
        if (nomeEroe.isEmpty()) {
            throw new IllegalArgumentException("Il nome dell'eroe non può essere vuoto");
        }
        if (nomeEroe.length() > LUNGHEZZA_MASSIMA_NOME) {
            throw new IllegalArgumentException("Il nome dell'eroe non può superare 100 caratteri");
        }
        verificaCodice(codiceClasse, "Il codice della classe");
        if (codiciCastelliCompletati.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("I codici dei castelli non possono contenere null");
        }
        codiciCastelliCompletati.forEach(codice -> verificaCodice(codice, "Il codice del castello"));
        codiciCastelliCompletati = Set.copyOf(codiciCastelliCompletati);
    }

    /**
     * Crea una progressione iniziale senza castelli completati.
     *
     * @param nomeEroe nome dell'eroe, ripulito dagli spazi iniziali e finali
     * @param codiceClasse identificativo della classe dell'eroe
     * @return progressione senza castelli completati
     * @throws NullPointerException se nome o codice sono null
     * @throws IllegalArgumentException se il nome è vuoto o supera 100 caratteri oppure il codice non rispetta il
     *     formato previsto
     */
    public static ProgressiPartita nuovi(String nomeEroe, String codiceClasse) {
        return new ProgressiPartita(nomeEroe, codiceClasse, Set.of());
    }

    /**
     * Restituisce una progressione con il castello completato; conserva la stessa istanza se il codice è già
     * presente.
     *
     * @param codiceCastello identificativo del castello
     * @return progressione aggiornata o la stessa istanza se il codice era già presente
     * @throws NullPointerException se il codice è null
     * @throws IllegalArgumentException se il codice non rispetta il formato di lettere minuscole, numeri e trattini
     */
    public ProgressiPartita conCastelloCompletato(String codiceCastello) {
        verificaCodice(codiceCastello, "Il codice del castello");
        if (codiciCastelliCompletati.contains(codiceCastello)) {
            return this;
        }
        Set<String> aggiornati = new HashSet<>(codiciCastelliCompletati);
        aggiornati.add(codiceCastello);
        return new ProgressiPartita(nomeEroe, codiceClasse, aggiornati);
    }

    /**
     * Verifica la presenza del castello tra quelli completati.
     *
     * @param codiceCastello identificativo del castello
     * @return {@code true} se il codice compare tra i castelli completati
     */
    public boolean isCastelloCompletato(String codiceCastello) {
        return codiciCastelliCompletati.contains(codiceCastello);
    }

    private static void verificaCodice(String codice, String descrizione) {
        Objects.requireNonNull(codice, descrizione + " non può essere null");
        if (codice.isBlank() || !codice.matches(FORMATO_CODICE)) {
            throw new IllegalArgumentException(
                    descrizione + " deve usare lettere minuscole, numeri e trattini"
            );
        }
    }
}
