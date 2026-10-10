package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello.Castello;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello.ConfigurazioneNemici;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello.FabbricaNemiciStandard;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.ClasseEroe;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.Eroe;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.FabbricaEroiStandard;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita.TempraDelCavaliere;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro.Mostro;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro.Nemico;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro.TipoMostro;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.partita.Partita;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.Statistiche;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BattagliaTest {

    @Test
    void alternaIlTurnoConUnSoloNemicoEGestisceLeCure() {
        Eroe eroe = new FabbricaEroiStandard().crea(
                "Arthas",
                new ClasseEroe("Guerriero", new Statistiche(100, 20, 5)),
                new TempraDelCavaliere(new RicaricaATurni(3, 0))
        );
        TipoMostro tipoOrco = new TipoMostro("Orco", new Statistiche(50, 12, 2));
        Nemico primoMostro = new Mostro("Orco 1", tipoOrco);
        Nemico secondoMostro = new Mostro("Orco 2", tipoOrco);
        Battaglia battaglia = new Battaglia(
                eroe,
                List.of(primoMostro, secondoMostro),
                new AttaccoBase()
        );

        battaglia.attacca(0);
        assertEquals(StatoBattaglia.TURNO_NEMICI, battaglia.stato());

        battaglia.eseguiTurnoNemico();
        assertEquals(StatoBattaglia.TURNO_EROE, battaglia.stato());
        assertEquals(93, eroe.puntiVita());
        assertEquals(50, secondoMostro.puntiVita());

        RisultatoAzione primaCura = battaglia.curaEroe();
        assertTrue(primaCura.riuscita());
        assertEquals(5, primaCura.puntiVitaRecuperati());
        assertEquals(1, battaglia.cureRimanenti());
        battaglia.eseguiTurnoNemico();

        assertTrue(battaglia.curaEroe().riuscita());
        assertEquals(0, battaglia.cureRimanenti());
        battaglia.eseguiTurnoNemico();

        RisultatoAzione terzaCura = battaglia.curaEroe();
        assertFalse(terzaCura.riuscita());
        assertEquals(EsitoAzione.CURE_ESAURITE, terzaCura.codice());
        assertEquals(StatoBattaglia.TURNO_EROE, battaglia.stato());
    }

    @Test
    void avanzaAlCastelloSuccessivoEAzzeraGliStatiTemporanei() {
        Eroe eroe = new FabbricaEroiStandard().crea(
                "Arthas",
                new ClasseEroe("Guerriero", new Statistiche(100, 20, 5)),
                new TempraDelCavaliere(new RicaricaATurni(3, 0))
        );
        TipoMostro tipoMelma = new TipoMostro("Melma", new Statistiche(5, 1, 0));
        Castello primoCastello = new Castello(
                "primo",
                "Primo castello",
                List.of(new ConfigurazioneNemici(tipoMelma, 1))
        );
        Castello secondoCastello = new Castello(
                "secondo",
                "Secondo castello",
                List.of(new ConfigurazioneNemici(tipoMelma, 1))
        );
        Partita partita = new Partita(
                eroe,
                List.of(primoCastello, secondoCastello),
                new FabbricaNemiciStandard()
        );
        partita.inizia();
        Battaglia battaglia = partita.battagliaCorrente();

        battaglia.usaAbilitaEroe();
        battaglia.eseguiTurnoNemico();
        assertEquals(15, eroe.bonusAttaccoPercentuale());
        assertEquals(2, eroe.abilita().turniRimanenti());

        battaglia.attacca(0);

        assertEquals(StatoBattaglia.VITTORIA, battaglia.stato());
        assertThrows(IllegalStateException.class, battaglia::eseguiTurnoNemico);

        partita.avanzaAlProssimoCastello();

        assertEquals(2, partita.numeroCastelloCorrente());
        assertEquals(secondoCastello, partita.castelloCorrente());
        assertEquals(0, eroe.bonusAttaccoPercentuale());
        assertEquals(0, eroe.bonusDifesaPercentuale());
        assertEquals(0, eroe.abilita().turniRimanenti());
        assertEquals(2, partita.battagliaCorrente().cureRimanenti());
    }

    @Test
    void avanzaLaRicaricaAllInizioDiOgniNuovoTurnoDellEroe() {
        Eroe eroe = new FabbricaEroiStandard().crea(
                "Arthas",
                new ClasseEroe("Guerriero", new Statistiche(100, 20, 5)),
                new TempraDelCavaliere(new RicaricaATurni(2, 0))
        );
        Nemico nemico = new Mostro(
                "Orco",
                new TipoMostro("Orco", new Statistiche(100, 6, 2))
        );
        Battaglia battaglia = new Battaglia(eroe, List.of(nemico), new AttaccoBase());

        battaglia.usaAbilitaEroe();
        assertEquals(2, eroe.abilita().turniRimanenti());

        battaglia.eseguiTurnoNemico();
        assertEquals(1, eroe.abilita().turniRimanenti());

        RisultatoAzione tentativoInRicarica = battaglia.usaAbilitaEroe();
        assertFalse(tentativoInRicarica.riuscita());
        assertEquals(StatoBattaglia.TURNO_EROE, battaglia.stato());

        battaglia.attacca(0);
        battaglia.eseguiTurnoNemico();
        assertEquals(0, eroe.abilita().turniRimanenti());
    }
}
