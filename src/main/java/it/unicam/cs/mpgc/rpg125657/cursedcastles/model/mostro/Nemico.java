package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.Combattente;

/**
 * Rappresenta il contratto di un avversario controllato dal gioco.
 */
public interface Nemico extends Combattente {

    /**
     * Restituisce l’archetipo del nemico.
     *
     * @return tipo del nemico
     */
    TipoMostro tipo();
}
