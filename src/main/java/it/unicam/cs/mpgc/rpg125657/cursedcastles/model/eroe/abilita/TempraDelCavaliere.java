package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.Combattente;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.Ricarica;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.RisultatoAzione;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.Eroe;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.ModificatoreStatistiche;

/**
 * Aggiunge a ogni utilizzo un bonus del 15% all'attacco e del 40% alla difesa.
 * I bonus si sommano e vengono azzerati all'ingresso nel castello successivo.
 */
public final class TempraDelCavaliere extends AbilitaConRicarica {

    private static final ModificatoreStatistiche BONUS = new ModificatoreStatistiche(15, 40);

    /**
     * Inizializza un’istanza di {@code TempraDelCavaliere} con le dipendenze ricevute.
     *
     * @param ricarica politica che regola la disponibilità dell'abilità
     */
    public TempraDelCavaliere(Ricarica ricarica) {
        super("Tempra del cavaliere", ricarica);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean richiedeBersaglio() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RisultatoAzione applica(Eroe utilizzatore, Combattente bersaglio) {
        utilizzatore.applicaBonus(BONUS);
        return new RisultatoAzione(
                true,
                0,
                0,
                utilizzatore.nome() + " ottiene +15% attacco e +40% difesa"
        );
    }
}
