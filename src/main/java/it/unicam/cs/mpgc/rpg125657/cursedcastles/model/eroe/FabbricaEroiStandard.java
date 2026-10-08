package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita.AbilitaEroe;

/** Crea l'implementazione standard senza conoscere le abilità concrete. */
public final class FabbricaEroiStandard implements FabbricaEroi {

    @Override
    public Eroe crea(String nome, ClasseEroe classe, AbilitaEroe abilita) {
        return new EroeGiocabile(nome, classe, abilita);
    }
}
