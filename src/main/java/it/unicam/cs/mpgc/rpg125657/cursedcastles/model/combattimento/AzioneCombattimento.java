package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento;

/** Strategia sostituibile per un'azione eseguita durante una battaglia. */
@FunctionalInterface
public interface AzioneCombattimento {

    RisultatoAzione esegui(Combattente esecutore, Combattente bersaglio);
}
