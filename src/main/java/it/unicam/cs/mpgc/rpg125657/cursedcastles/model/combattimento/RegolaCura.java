package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.Eroe;

/** Regola sostituibile per la cura disponibile durante un castello. */
public interface RegolaCura {

    RisultatoAzione usa(Eroe eroe);

    int utilizziRimanenti();
}
