package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.Combattente;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita.AbilitaEroe;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.Potenziabile;

/**
 * Rappresenta il contratto del personaggio controllato dal giocatore.
 */
public interface Eroe extends Combattente, Potenziabile {

    /**
     * Restituisce l’archetipo a cui appartiene l’eroe.
     *
     * @return classe dell’eroe
     */
    ClasseEroe classe();

    /**
     * Restituisce l’abilità posseduta dall’eroe.
     *
     * @return abilità dell’eroe
     */
    AbilitaEroe abilita();

    /**
     * Aggiorna lo stato della ricarica all’inizio di un nuovo turno dell’eroe.
     */
    void avanzaTurno();

    /**
     * Ripristina tutti i punti vita, azzera i bonus e reimposta la ricarica all’ingresso di un nuovo castello.
     *
     */
    default void preparaNuovoCastello() {
        recuperaPuntiVita(statisticheBase().puntiVitaMassimi());
        azzeraBonus();
        abilita().reset();
    }
}
