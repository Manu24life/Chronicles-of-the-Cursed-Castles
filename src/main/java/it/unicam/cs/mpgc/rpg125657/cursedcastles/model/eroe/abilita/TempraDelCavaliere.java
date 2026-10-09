package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.Combattente;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.Ricarica;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.RisultatoAzione;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.Eroe;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.ModificatoreStatistiche;

/** Applica bonus permanenti e additivi del 15% all'attacco e del 40% alla difesa. */
public final class TempraDelCavaliere extends AbilitaConRicarica {

    private static final ModificatoreStatistiche BONUS = new ModificatoreStatistiche(15, 40);

    public TempraDelCavaliere(Ricarica ricarica) {
        super("Tempra del cavaliere", ricarica);
    }

    @Override
    public boolean richiedeBersaglio() {
        return false;
    }

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
