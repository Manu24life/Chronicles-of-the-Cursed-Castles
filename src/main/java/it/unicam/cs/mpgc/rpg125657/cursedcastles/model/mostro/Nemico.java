package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.Combattente;

/** Contratto di un avversario controllato dal gioco. */
public interface Nemico extends Combattente {

    TipoMostro tipo();
}
