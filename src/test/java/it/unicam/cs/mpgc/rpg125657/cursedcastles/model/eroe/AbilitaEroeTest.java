package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.RicaricaATurni;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.RisultatoAzione;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita.TempraDelCavaliere;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro.Mostro;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro.TipoMostro;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.Statistiche;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbilitaEroeTest {

    private final FabbricaEroi fabbricaEroi = new FabbricaEroiStandard();

    @Test
    void ilMagoInfliggeDannoECuraIlTrentaPercentoDellaVitaMassima() {
        Eroe mago = fabbricaEroi.crea("Merlino", ClassiEroe.MAGO);
        Mostro bersaglio = creaBersaglio();
        mago.subisciDanno(30);

        RisultatoAzione risultato = mago.abilita().usa(mago, bersaglio);

        assertTrue(risultato.riuscita());
        assertEquals(8, risultato.dannoInflitto());
        assertEquals(24, risultato.puntiVitaRecuperati());
        assertEquals(74, mago.puntiVita());
        assertEquals(92, bersaglio.puntiVita());
        assertEquals(3, mago.abilita().turniRimanenti());
    }

    @Test
    void ilCavaliereAccumulaBonusPermanentiInModoAdditivo() {
        Eroe cavaliere = fabbricaEroi.crea(
                "Lancillotto",
                ClassiEroe.CAVALIERE,
                new TempraDelCavaliere(new RicaricaATurni(1, 0))
        );
        Mostro bersaglio = creaBersaglio();

        cavaliere.abilita().usa(cavaliere, bersaglio);
        assertEquals(15, cavaliere.bonusAttaccoPercentuale());
        assertEquals(40, cavaliere.bonusDifesaPercentuale());
        assertEquals(20, cavaliere.attacco());
        assertEquals(13, cavaliere.difesa());

        cavaliere.avanzaTurno();
        cavaliere.abilita().usa(cavaliere, bersaglio);
        assertEquals(30, cavaliere.bonusAttaccoPercentuale());
        assertEquals(80, cavaliere.bonusDifesaPercentuale());
        assertEquals(22, cavaliere.attacco());
        assertEquals(16, cavaliere.difesa());
    }

    @Test
    void unAbilitaInRicaricaNonVieneApplicataDiNuovo() {
        Eroe cavaliere = fabbricaEroi.crea(
                "Lancillotto",
                ClassiEroe.CAVALIERE,
                new TempraDelCavaliere(new RicaricaATurni(2, 0))
        );
        Mostro bersaglio = creaBersaglio();

        assertTrue(cavaliere.abilita().usa(cavaliere, bersaglio).riuscita());
        RisultatoAzione secondoTentativo = cavaliere.abilita().usa(cavaliere, bersaglio);

        assertFalse(secondoTentativo.riuscita());
        assertEquals(15, cavaliere.bonusAttaccoPercentuale());
        assertEquals(2, cavaliere.abilita().turniRimanenti());
    }

    @Test
    void eroiDiversiNonCondividonoLoStatoDellaRicarica() {
        Eroe primo = fabbricaEroi.crea("Primo", ClassiEroe.MAGO);
        Eroe secondo = fabbricaEroi.crea("Secondo", ClassiEroe.MAGO);

        primo.abilita().usa(primo, creaBersaglio());

        assertFalse(primo.abilita().isDisponibile());
        assertTrue(secondo.abilita().isDisponibile());
    }

    private Mostro creaBersaglio() {
        return new Mostro(
                "Bersaglio",
                new TipoMostro("Fantoccio", new Statistiche(100, 1, 0))
        );
    }
}
