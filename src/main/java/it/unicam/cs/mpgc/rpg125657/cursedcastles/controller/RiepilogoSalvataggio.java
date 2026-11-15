package it.unicam.cs.mpgc.rpg125657.cursedcastles.controller;

import java.util.Objects;
import java.util.Set;

/**
 * Rappresenta le informazioni del salvataggio necessarie al menu, prive di dettagli infrastrutturali.
 *
 * @param nomeEroe nome dell'eroe, ripulito dagli spazi iniziali e finali
 * @param codiceClasse identificativo della classe dell'eroe
 * @param codiciCastelliCompletati insieme dei codici dei castelli già completati, copiato in forma immutabile
 * @param campagnaCompletata indicatore di completamento di tutti i castelli
 */
public record RiepilogoSalvataggio(
        String nomeEroe,
        String codiceClasse,
        Set<String> codiciCastelliCompletati,
        boolean campagnaCompletata
) {

    /**
     * Inizializza i dati immutabili di {@code RiepilogoSalvataggio}, verificando i vincoli dei componenti.
     *
     * @param nomeEroe nome dell'eroe, ripulito dagli spazi iniziali e finali
     * @param codiceClasse identificativo della classe dell'eroe
     * @param codiciCastelliCompletati insieme dei codici dei castelli già completati, copiato in forma immutabile
     * @param campagnaCompletata indicatore di completamento di tutti i castelli
     * @throws NullPointerException se almeno uno tra {@code nomeEroe}, {@code codiceClasse},
     *     {@code codiciCastelliCompletati} è {@code null}
     */
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
