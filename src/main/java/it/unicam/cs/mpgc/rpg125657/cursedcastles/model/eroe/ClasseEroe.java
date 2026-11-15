package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.Statistiche;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita.AbilitaEroe;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita.FabbricaAbilitaEroe;

import java.util.Objects;

/**
 * Rappresenta un archetipo immutabile: due eroi della stessa classe condividono questi valori.
 *
 * @param codice identificativo stabile usato per riconoscere la configurazione
 * @param nome nome visualizzato
 * @param statistiche statistiche base immutabili del personaggio
 * @param fabbricaAbilita fabbrica di abilità indipendenti; può essere {@code null} per classi senza abilità
 *     predefinita
 */
public record ClasseEroe(
        String codice,
        String nome,
        Statistiche statistiche,
        FabbricaAbilitaEroe fabbricaAbilita
) {

    /**
     * Crea la configurazione ricavando il codice dal nome, senza assegnare una fabbrica di abilità.
     * Richiede un’abilità esplicita al momento della creazione dell’eroe.
     *
     * @param nome nome visualizzato
     * @param statistiche statistiche base immutabili del personaggio
     */
    public ClasseEroe(String nome, Statistiche statistiche) {
        this(codiceDaNome(nome), nome, statistiche, null);
    }

    /**
     * Crea la configurazione ricavando il codice stabile dal nome fornito.
     *
     * @param nome nome visualizzato
     * @param statistiche statistiche base immutabili del personaggio
     * @param fabbricaAbilita fabbrica di abilità indipendenti; può essere {@code null} per classi senza abilità
     *     predefinita
     */
    public ClasseEroe(
            String nome,
            Statistiche statistiche,
            FabbricaAbilitaEroe fabbricaAbilita
    ) {
        this(codiceDaNome(nome), nome, statistiche, fabbricaAbilita);
    }

    /**
     * Inizializza i dati immutabili di {@code ClasseEroe}, verificando i vincoli dei componenti.
     *
     * @param codice identificativo stabile usato per riconoscere la configurazione
     * @param nome nome visualizzato
     * @param statistiche statistiche base immutabili del personaggio
     * @param fabbricaAbilita fabbrica di abilità indipendenti; può essere {@code null} per classi senza abilità
     *     predefinita
     * @throws NullPointerException se almeno uno tra {@code codice}, {@code nome}, {@code statistiche} è
     *     {@code null}
     * @throws IllegalArgumentException se il nome è vuoto o il codice non rispetta il formato di lettere minuscole,
     *     numeri e trattini
     */
    public ClasseEroe {
        Objects.requireNonNull(codice, "Il codice della classe non può essere null");
        Objects.requireNonNull(nome, "Il nome della classe non può essere null");
        Objects.requireNonNull(statistiche, "Le statistiche non possono essere null");
        if (codice.isBlank() || !codice.matches("[a-z0-9]+(?:-[a-z0-9]+)*")) {
            throw new IllegalArgumentException("Il codice deve usare lettere minuscole, numeri e trattini");
        }
        if (nome.isBlank()) {
            throw new IllegalArgumentException("Il nome della classe non può essere vuoto");
        }
        nome = nome.strip();
    }

    /**
     * Crea una nuova abilità usando la fabbrica dell’archetipo.
     *
     * @return nuova abilità dell’archetipo
     * @throws IllegalStateException se la classe non definisce una fabbrica di abilità
     * @throws NullPointerException se la fabbrica restituisce null
     */
    public AbilitaEroe creaAbilita() {
        if (fabbricaAbilita == null) {
            throw new IllegalStateException("La classe non definisce un'abilità predefinita");
        }
        return Objects.requireNonNull(
                fabbricaAbilita.crea(),
                "La fabbrica non può restituire un'abilità null"
        );
    }

    /**
     * Verifica se l’archetipo specifica una fabbrica di abilità.
     *
     * @return {@code true} se è presente una fabbrica di abilità
     */
    public boolean haAbilitaPredefinita() {
        return fabbricaAbilita != null;
    }

    /**
     * Verifica se l’abilità ha la stessa classe concreta di quella predefinita; accetta ogni abilità quando manca
     * una fabbrica.
     *
     * @param abilita abilità assegnata all'eroe
     * @return {@code true} se l’abilità è accettata dall’archetipo
     * @throws NullPointerException se almeno uno tra {@code abilita} è {@code null}
     */
    public boolean isAbilitaCompatibile(AbilitaEroe abilita) {
        Objects.requireNonNull(abilita, "L'abilità non può essere null");
        return !haAbilitaPredefinita() || creaAbilita().getClass().equals(abilita.getClass());
    }

    private static String codiceDaNome(String nome) {
        Objects.requireNonNull(nome, "Il nome della classe non può essere null");
        return nome.strip().toLowerCase(java.util.Locale.ROOT).replaceAll("[^a-z0-9]+", "-");
    }
}
