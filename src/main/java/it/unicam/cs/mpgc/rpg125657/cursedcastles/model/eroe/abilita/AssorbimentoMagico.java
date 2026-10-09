package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.Combattente;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.Ricarica;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.RisultatoAzione;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.Eroe;

/** Infligge un danno magico lieve e cura il 30% dei punti vita massimi. */
public final class AssorbimentoMagico extends AbilitaConRicarica {

    private static final int DANNO_MAGICO = 8;
    private static final int CURA_PERCENTUALE = 30;

    public AssorbimentoMagico(Ricarica ricarica) {
        super("Assorbimento magico", ricarica);
    }

    @Override
    protected RisultatoAzione applica(Eroe utilizzatore, Combattente bersaglio) {
        if (!bersaglio.isVivo()) {
            return new RisultatoAzione(false, 0, 0, "Il bersaglio è già sconfitto");
        }

        int dannoEffettivo = Math.min(DANNO_MAGICO, bersaglio.puntiVita());
        bersaglio.subisciDanno(dannoEffettivo);

        int curaRichiesta = utilizzatore.statisticheBase().puntiVitaMassimi()
                * CURA_PERCENTUALE / 100;
        int vitaPrima = utilizzatore.puntiVita();
        utilizzatore.recuperaPuntiVita(curaRichiesta);
        int curaEffettiva = utilizzatore.puntiVita() - vitaPrima;

        return new RisultatoAzione(
                true,
                dannoEffettivo,
                curaEffettiva,
                utilizzatore.nome() + " infligge " + dannoEffettivo
                        + " danni e recupera " + curaEffettiva + " punti vita"
        );
    }
}
