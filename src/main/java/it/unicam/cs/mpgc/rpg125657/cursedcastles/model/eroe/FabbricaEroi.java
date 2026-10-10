package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita.AbilitaEroe;

/** Punto di creazione sostituibile degli eroi. */
@FunctionalInterface
public interface FabbricaEroi {

    Eroe crea(String nome, ClasseEroe classe, AbilitaEroe abilita);

    /** Crea un eroe usando l'abilità dichiarata dalla sua classe. */
    default Eroe crea(String nome, ClasseEroe classe) {
        return crea(nome, classe, classe.creaAbilita());
    }
}
