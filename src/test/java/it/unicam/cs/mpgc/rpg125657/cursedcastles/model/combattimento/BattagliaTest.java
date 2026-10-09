package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.ClasseEroe;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.Eroe;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.FabbricaEroiStandard;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita.TempraDelCavaliere;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro.Mostro;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro.Nemico;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro.TipoMostro;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.Statistiche;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BattagliaTest {

    @Test
    void alternaIlTurnoTraEroeENemici() {
        Eroe eroe = new FabbricaEroiStandard().crea(
                "Arthas",
                new ClasseEroe("Guerriero", new Statistiche(100, 20, 5)),
                new TempraDelCavaliere(new RicaricaATurni(3, 0))
        );
        Nemico mostro = new Mostro("Orco", new TipoMostro("Orco", new Statistiche(50, 12, 2)));
        Battaglia battaglia = new Battaglia(eroe, List.of(mostro), new AttaccoBase());

        battaglia.eseguiAzioneEroe(0, new AttaccoBase());
        assertEquals(StatoBattaglia.TURNO_NEMICI, battaglia.stato());

        battaglia.eseguiTurnoNemici();
        assertEquals(StatoBattaglia.TURNO_EROE, battaglia.stato());
        assertEquals(93, eroe.puntiVita());
    }

    @Test
    void terminaConLaVittoriaQuandoTuttiINemiciSonoSconfitti() {
        Eroe eroe = new FabbricaEroiStandard().crea(
                "Arthas",
                new ClasseEroe("Guerriero", new Statistiche(100, 20, 5)),
                new TempraDelCavaliere(new RicaricaATurni(3, 0))
        );
        Nemico debole = new Mostro("Melma", new TipoMostro("Melma", new Statistiche(5, 1, 0)));
        Battaglia battaglia = new Battaglia(eroe, List.of(debole), new AttaccoBase());

        battaglia.eseguiAzioneEroe(0, new AttaccoBase());

        assertEquals(StatoBattaglia.VITTORIA, battaglia.stato());
        assertThrows(IllegalStateException.class, battaglia::eseguiTurnoNemici);
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

        battaglia.usaAbilitaEroe(0);
        assertEquals(2, eroe.abilita().turniRimanenti());

        battaglia.eseguiTurnoNemici();
        assertEquals(1, eroe.abilita().turniRimanenti());

        RisultatoAzione tentativoInRicarica = battaglia.usaAbilitaEroe(0);
        assertFalse(tentativoInRicarica.riuscita());
        assertEquals(StatoBattaglia.TURNO_EROE, battaglia.stato());

        battaglia.eseguiAzioneEroe(0, new AttaccoBase());
        battaglia.eseguiTurnoNemici();
        assertEquals(0, eroe.abilita().turniRimanenti());
    }
}
