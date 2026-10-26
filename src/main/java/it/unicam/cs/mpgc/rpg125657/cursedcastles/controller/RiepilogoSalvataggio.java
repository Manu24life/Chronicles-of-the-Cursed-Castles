package it.unicam.cs.mpgc.rpg125657.cursedcastles.controller;

import java.util.Objects;
import java.util.Set;

/** Informazioni del salvataggio necessarie al menu, prive di dettagli infrastrutturali. */
public record RiepilogoSalvataggio(
        String nomeEroe,
        String codiceClasse,
        Set<String> codiciCastelliCompletati,
        boolean campagnaCompletata
) {

    public RiepilogoSalvataggio {
        Objects.requireNonNull(nomeEroe, "Il nome dell'eroe non può essere null");
        Objects.requireNonNull(codiceClasse, "Il codice della classe non può essere null");
        Objects.requireNonNull(
                codiciCastelliCompletati,
                "I codici dei castelli completati non possono essere null"
        );
        codiciCastelliCompletati = Set.copyOf(codiciCastelliCompletati);
    }
}
