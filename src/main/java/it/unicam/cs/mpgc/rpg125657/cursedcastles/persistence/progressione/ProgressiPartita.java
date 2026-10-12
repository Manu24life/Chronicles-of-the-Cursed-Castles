package it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.progressione;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/** Dati durevoli dell'unico slot di salvataggio. */
public record ProgressiPartita(
        String nomeEroe,
        String codiceClasse,
        Set<String> codiciCastelliCompletati
) {

    private static final int LUNGHEZZA_MASSIMA_NOME = 100;
    private static final String FORMATO_CODICE = "[a-z0-9]+(?:-[a-z0-9]+)*";

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

    public static ProgressiPartita nuovi(String nomeEroe, String codiceClasse) {
        return new ProgressiPartita(nomeEroe, codiceClasse, Set.of());
    }

    public ProgressiPartita conCastelloCompletato(String codiceCastello) {
        verificaCodice(codiceCastello, "Il codice del castello");
        if (codiciCastelliCompletati.contains(codiceCastello)) {
            return this;
        }
        Set<String> aggiornati = new HashSet<>(codiciCastelliCompletati);
        aggiornati.add(codiceCastello);
        return new ProgressiPartita(nomeEroe, codiceClasse, aggiornati);
    }

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
