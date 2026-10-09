package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento;

/** Crea una regola di cura con stato indipendente per ogni castello. */
@FunctionalInterface
public interface FabbricaRegolaCura {

    RegolaCura crea();
}
