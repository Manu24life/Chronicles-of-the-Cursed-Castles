package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.Eroe;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.FabbricaEroiStandard;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.ClassiEroe;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita.AssorbimentoMagico;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.RicaricaATurni;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonaggioTest {

    @Test
    void usaLeStatisticheFisseDellaClasseESalvaSoloLoStatoCorrente() {
        Eroe eroe = creaMago();

        eroe.subisciDanno(30);
        eroe.recuperaPuntiVita(10);

        assertEquals(ClassiEroe.MAGO.statistiche(), eroe.statisticheBase());
        assertEquals(60, eroe.puntiVita());
    }

    @Test
    void puntiVitaNonScendonoSottoZero() {
        Eroe eroe = creaMago();

        eroe.subisciDanno(1_000);

        assertEquals(0, eroe.puntiVita());
        assertFalse(eroe.isVivo());
    }

    @Test
    void rifiutaValoriDiDannoInvalidi() {
        Eroe eroe = creaMago();

        assertThrows(IllegalArgumentException.class, () -> eroe.subisciDanno(-1));
    }

    private Eroe creaMago() {
        return new FabbricaEroiStandard().crea(
                "Merlino",
                ClassiEroe.MAGO,
                new AssorbimentoMagico(new RicaricaATurni(3, 0))
        );
    }
}
