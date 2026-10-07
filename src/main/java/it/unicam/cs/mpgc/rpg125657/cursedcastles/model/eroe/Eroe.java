package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.Combattente;

/** Contratto del personaggio controllato dal giocatore. */
public interface Eroe extends Combattente {

    ClasseEroe classe();
}
