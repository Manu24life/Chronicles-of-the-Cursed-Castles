package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.Combattimento;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.RisultatoAzione;

import java.util.Optional;

/**
 * Definisce i comportamenti esclusivi di un eroe.
 * L'attacco di base e lo stato vitale appartengono invece a {@link Combattimento}.
 */
public interface Eroe extends Combattimento {

    RisultatoAzione usaAbilita(Combattimento bersaglio);

    Optional<RisultatoAzione> contrattacca(Combattimento attaccante);

    boolean puoUsareAbilita();

    boolean puoContrattaccare();

    /** Aggiorna gli effetti e le ricariche al termine del turno dell'eroe. */
    void concludiTurno();
}
