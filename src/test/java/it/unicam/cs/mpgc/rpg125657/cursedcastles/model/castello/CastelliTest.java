package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro.Nemico;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class CastelliTest {

    private final FabbricaNemici fabbricaNemici = new FabbricaNemiciStandard();

    @Test
    void contieneEsattamenteISetupDeiTreCastelli() {
        assertEquals(3, Castelli.tutti().size());
        assertEquals(3, fabbricaNemici.creaNemici(Castelli.OSSA).size());
        assertEquals(3, fabbricaNemici.creaNemici(Castelli.FERRO).size());
        assertEquals(1, fabbricaNemici.creaNemici(Castelli.MALEDIZIONE).size());
    }

    @Test
    void ogniBattagliaRiceveNuoveIstanzeDeiNemici() {
        List<Nemico> primoGruppo = fabbricaNemici.creaNemici(Castelli.OSSA);
        List<Nemico> secondoGruppo = fabbricaNemici.creaNemici(Castelli.OSSA);

        primoGruppo.getFirst().subisciDanno(10);

        assertNotSame(primoGruppo.getFirst(), secondoGruppo.getFirst());
        assertEquals(secondoGruppo.getFirst().statisticheBase().puntiVitaMassimi(),
                secondoGruppo.getFirst().puntiVita());
    }
}
