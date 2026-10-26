package it.unicam.cs.mpgc.rpg125657.cursedcastles.controller;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.StatoBattaglia;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.StatoNemico;

import java.util.List;
import java.util.Objects;

/** Snapshot immutabile del castello e della battaglia corrente. */
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
