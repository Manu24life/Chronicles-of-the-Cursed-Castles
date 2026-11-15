package it.unicam.cs.mpgc.rpg125657.cursedcastles.controller;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.StatoBattaglia;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.StatoNemico;

import java.util.List;
import java.util.Objects;

/**
 * Rappresenta uno snapshot immutabile del castello e della battaglia corrente.
 *
 * @param codice identificativo stabile usato per riconoscere la configurazione
 * @param nome nome visualizzato
 * @param numero numero del castello corrente, a partire da uno
 * @param totale numero totale dei castelli
 * @param statoBattaglia fase corrente del combattimento
 * @param nemici avversari della battaglia, conservati nello stesso ordine
 * @param cureRimanenti numero di cure ancora disponibili nel castello
 * @param nomeAbilita nome visualizzato dell'abilità
 * @param abilitaDisponibile indicatore di disponibilità dell'abilità
 * @param turniRicaricaRimanenti turni mancanti al riutilizzo dell'abilità
 * @param abilitaRichiedeBersaglio indicatore della necessità di selezionare un nemico
 * @param vittoriaSalvata indicatore di vittoria già registrata nel salvataggio
 * @param puoAvanzare indicatore di avanzamento consentito dopo una vittoria salvata
 */
public record StatoCastello(
        String codice,
        String nome,
        int numero,
        int totale,
        StatoBattaglia statoBattaglia,
        List<StatoNemico> nemici,
        int cureRimanenti,
        String nomeAbilita,
        boolean abilitaDisponibile,
        int turniRicaricaRimanenti,
        boolean abilitaRichiedeBersaglio,
        boolean vittoriaSalvata,
        boolean puoAvanzare
) {

    /**
     * Inizializza i dati immutabili di {@code StatoCastello}, verificando i vincoli dei componenti.
     *
     * @param codice identificativo stabile usato per riconoscere la configurazione
     * @param nome nome visualizzato
     * @param numero numero del castello corrente, a partire da uno
     * @param totale numero totale dei castelli
     * @param statoBattaglia fase corrente del combattimento
     * @param nemici avversari della battaglia, conservati nello stesso ordine
     * @param cureRimanenti numero di cure ancora disponibili nel castello
     * @param nomeAbilita nome visualizzato dell'abilità
     * @param abilitaDisponibile indicatore di disponibilità dell'abilità
     * @param turniRicaricaRimanenti turni mancanti al riutilizzo dell'abilità
     * @param abilitaRichiedeBersaglio indicatore della necessità di selezionare un nemico
     * @param vittoriaSalvata indicatore di vittoria già registrata nel salvataggio
     * @param puoAvanzare indicatore di avanzamento consentito dopo una vittoria salvata
     * @throws NullPointerException se almeno uno tra {@code codice}, {@code nome}, {@code statoBattaglia},
     *     {@code nemici}, {@code nomeAbilita} è {@code null}
     * @throws IllegalArgumentException se numero, totale o contatori non sono validi, oppure l’avanzamento non
     *     corrisponde a una vittoria salvata
     */
    public StatoCastello {
        Objects.requireNonNull(codice, "Il codice del castello non può essere null");
        Objects.requireNonNull(nome, "Il nome del castello non può essere null");
        Objects.requireNonNull(statoBattaglia, "Lo stato della battaglia non può essere null");
        Objects.requireNonNull(nemici, "I nemici non possono essere null");
        Objects.requireNonNull(nomeAbilita, "Il nome dell'abilità non può essere null");
        if (numero < 1 || totale < numero || cureRimanenti < 0 || turniRicaricaRimanenti < 0) {
            throw new IllegalArgumentException("Lo stato del castello non è valido");
        }
        if (puoAvanzare && (!vittoriaSalvata || statoBattaglia != StatoBattaglia.VITTORIA)) {
            throw new IllegalArgumentException("L'avanzamento richiede una vittoria salvata");
        }
        nemici = List.copyOf(nemici);
    }
}
