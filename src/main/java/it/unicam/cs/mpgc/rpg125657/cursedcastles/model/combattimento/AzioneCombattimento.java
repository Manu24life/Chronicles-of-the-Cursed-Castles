package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento;

/**
 * Rappresenta una strategia sostituibile per un'azione eseguita durante una battaglia.
 */
@FunctionalInterface
public interface AzioneCombattimento {

    /**
     * Esegue l’azione sul bersaglio e ne descrive l’esito, inclusi danno e cura effettivi.
     *
     * @param esecutore combattente che esegue l'azione
     * @param bersaglio combattente destinatario dell'effetto
     * @return esito dell’azione, con successo o motivo del fallimento e variazioni effettive di vita
     */
    RisultatoAzione esegui(Combattente esecutore, Combattente bersaglio);
}
