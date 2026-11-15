package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita;

/**
 * Crea una nuova abilità, con stato di ricarica non condiviso, per ogni eroe.
 */
@FunctionalInterface
public interface FabbricaAbilitaEroe {

    /**
     * Crea una nuova abilità con stato indipendente da quello degli altri eroi.
     *
     * @return nuova istanza indipendente
     */
    AbilitaEroe crea();
}
