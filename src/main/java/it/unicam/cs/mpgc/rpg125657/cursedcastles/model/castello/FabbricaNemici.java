package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro.Nemico;

import java.util.List;

/** Crea i nemici descritti dalla configurazione di un castello. */
@FunctionalInterface
public interface FabbricaNemici {

    List<Nemico> creaNemici(Castello castello);
}
