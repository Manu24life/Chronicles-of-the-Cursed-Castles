package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.Eroe;

/**
 * Rappresenta la regola sostituibile per la cura disponibile durante un castello.
 */
public interface RegolaCura {

    /**
     * Tenta la cura dell’eroe secondo la politica e gli utilizzi disponibili.
     *
     * @param eroe eroe che affronta la campagna
     * @return esito dell’azione, con successo o motivo del fallimento e variazioni effettive di vita
     */
    RisultatoAzione usa(Eroe eroe);

    /**
     * Restituisce il numero di cure ancora disponibili.
     *
     * @return numero di cure residue
     */
    int utilizziRimanenti();
}
