package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento;

/**
 * Crea una regola di cura con stato indipendente per ogni castello.
 */
@FunctionalInterface
public interface FabbricaRegolaCura {

    /**
     * Crea una regola di cura con utilizzi indipendenti per un nuovo castello.
     *
     * @return nuova istanza indipendente
     */
    RegolaCura crea();
}
