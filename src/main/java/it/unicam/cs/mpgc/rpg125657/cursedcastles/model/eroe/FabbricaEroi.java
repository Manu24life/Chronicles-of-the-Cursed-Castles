package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita.AbilitaEroe;

/**
 * Rappresenta il punto di creazione sostituibile degli eroi.
 */
@FunctionalInterface
public interface FabbricaEroi {

    /**
     * Crea un nuovo eroe con la classe e l’abilità esplicite.
     *
     * @param nome nome non vuoto del nuovo eroe
     * @param classe archetipo dell'eroe con statistiche e abilità predefinita
     * @param abilita abilità assegnata all'eroe
     * @return nuova istanza indipendente
     * @throws NullPointerException se nome, classe o abilità risultano null
     * @throws IllegalArgumentException se il nome è vuoto o l’abilità non è compatibile con la classe
     */
    Eroe crea(String nome, ClasseEroe classe, AbilitaEroe abilita);

    /**
     * Crea un nuovo eroe usando una nuova abilità generata dalla classe.
     *
     * @param nome nome non vuoto del nuovo eroe
     * @param classe archetipo dell'eroe con statistiche e abilità predefinita
     * @return nuova istanza indipendente
     * @throws NullPointerException se nome, classe o abilità risultano null
     * @throws IllegalArgumentException se il nome è vuoto o l’abilità non è compatibile con la classe
     * @throws IllegalStateException se viene richiesta l’abilità predefinita di una classe che non la specifica
     */
    default Eroe crea(String nome, ClasseEroe classe) {
        return crea(nome, classe, classe.creaAbilita());
    }
}
