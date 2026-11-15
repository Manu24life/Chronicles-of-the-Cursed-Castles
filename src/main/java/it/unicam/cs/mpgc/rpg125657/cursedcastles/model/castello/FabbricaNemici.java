package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro.Nemico;

import java.util.List;

/**
 * Crea i nemici descritti dalla configurazione di un castello.
 */
@FunctionalInterface
public interface FabbricaNemici {

    /**
     * Crea nuovi nemici secondo i gruppi configurati nel castello.
     *
     * @param castello castello da rappresentare o di cui creare i nemici
     * @return lista dei nemici creati nell’ordine delle configurazioni
     */
    List<Nemico> creaNemici(Castello castello);
}
