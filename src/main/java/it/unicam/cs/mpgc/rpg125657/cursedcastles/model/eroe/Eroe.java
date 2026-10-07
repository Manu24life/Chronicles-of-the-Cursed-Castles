package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.Combattente;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita.AbilitaEroe;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.Potenziabile;

/** Contratto del personaggio controllato dal giocatore. */
public interface Eroe extends Combattente, Potenziabile {

    ClasseEroe classe();

    AbilitaEroe abilita();

    /** Aggiorna gli stati dipendenti dai turni quando inizia un nuovo turno dell'eroe. */
    void avanzaTurno();
}
