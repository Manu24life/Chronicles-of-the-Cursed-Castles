package it.unicam.cs.mpgc.rpg125657.cursedcastles.controller;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.ClasseEroe;

import java.util.Objects;

/**
 * Rappresenta i dati immutabili di una classe selezionabile dal menu.
 *
 * @param codice identificativo stabile usato per riconoscere la configurazione
 * @param nome nome visualizzato
 * @param puntiVitaMassimi limite massimo dei punti vita, strettamente positivo
 * @param attacco valore di attacco, non negativo
 * @param difesa valore di difesa
 */
public record ClasseDisponibile(
        String codice,
        String nome,
        int puntiVitaMassimi,
        int attacco,
        int difesa
) {

    /**
     * Inizializza i dati immutabili di {@code ClasseDisponibile}, verificando i vincoli dei componenti.
     *
     * @param codice identificativo stabile usato per riconoscere la configurazione
     * @param nome nome visualizzato
     * @param puntiVitaMassimi limite massimo dei punti vita, strettamente positivo
     * @param attacco valore di attacco, non negativo
     * @param difesa valore di difesa
     * @throws NullPointerException se almeno uno tra {@code codice}, {@code nome} è {@code null}
     * @throws IllegalArgumentException se la vita massima non è positiva oppure attacco o difesa sono negativi
     */
    public ClasseDisponibile {
        Objects.requireNonNull(codice, "Il codice non può essere null");
        Objects.requireNonNull(nome, "Il nome non può essere null");
        if (puntiVitaMassimi <= 0 || attacco < 0 || difesa < 0) {
            throw new IllegalArgumentException("Le statistiche della classe non sono valide");
        }
    }

    static ClasseDisponibile da(ClasseEroe classe) {
        Objects.requireNonNull(classe, "La classe non può essere null");
        return new ClasseDisponibile(
                classe.codice(),
                classe.nome(),
                classe.statistiche().puntiVitaMassimi(),
                classe.statistiche().attacco(),
                classe.statistiche().difesa()
        );
    }
}
