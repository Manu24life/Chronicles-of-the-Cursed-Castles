package it.unicam.cs.mpgc.rpg125657.cursedcastles.controller;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello.Castello;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello.ConfigurazioneNemici;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello.FabbricaNemiciStandard;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.RicaricaATurni;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.StatoBattaglia;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.ClasseEroe;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.FabbricaEroiStandard;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita.TempraDelCavaliere;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro.TipoMostro;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.partita.StatoPartita;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.Statistiche;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.progressione.ProgressiPartita;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.progressione.ProgressiRepository;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.application.ServizioProgressione;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.application.ServizioGioco;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BattagliaControllerTest {

    @Test
    void mantieneSeparatoIlTurnoDelNemico() {
        ContestoTest contesto = creaContesto(
                new Statistiche(100, 10, 0),
                new Statistiche(50, 20, 0),
                1
        );
        contesto.menu().nuovaPartita("Arthas", "eroe-test");

        EsitoComando attacco = contesto.battaglia().attacca(0);

        assertEquals(StatoBattaglia.TURNO_NEMICI,
                attacco.stato().castello().orElseThrow().statoBattaglia());
        assertEquals(100, attacco.stato().eroe().puntiVita());
        assertTrue(contesto.repository().carica().orElseThrow()
                .codiciCastelliCompletati().isEmpty());

        EsitoComando risposta = contesto.battaglia().eseguiTurnoNemico();

        assertEquals(StatoBattaglia.TURNO_EROE,
                risposta.stato().castello().orElseThrow().statoBattaglia());
        assertEquals(80, risposta.stato().eroe().puntiVita());
    }

    @Test
    void salvaAutomaticamenteLaVittoriaEAvanzaSoloSuComando() {
        ContestoTest contesto = creaContesto(
                new Statistiche(100, 100, 0),
                new Statistiche(1, 0, 0),
                2
        );
        contesto.menu().nuovaPartita("Arthas", "eroe-test");

        EsitoComando vittoria = contesto.battaglia().attacca(0);

        StatoCastello primo = vittoria.stato().castello().orElseThrow();
        assertEquals(StatoBattaglia.VITTORIA, primo.statoBattaglia());
        assertTrue(primo.vittoriaSalvata());
        assertTrue(primo.puoAvanzare());
        assertEquals(1, primo.numero());
        assertTrue(contesto.repository().carica().orElseThrow()
                .isCastelloCompletato("castello-1"));

        VistaPartita secondo = contesto.battaglia().vaiAlCastelloSuccessivo();

        assertEquals(StatoPartita.IN_CORSO, secondo.statoPartita());
        assertEquals(2, secondo.castello().orElseThrow().numero());
        assertFalse(secondo.castello().orElseThrow().vittoriaSalvata());

        contesto.battaglia().attacca(0);
        VistaPartita completata = contesto.battaglia().vaiAlCastelloSuccessivo();

        assertEquals(StatoPartita.COMPLETATA, completata.statoPartita());
        assertTrue(completata.castello().isEmpty());
        assertEquals(2, contesto.repository().carica().orElseThrow()
                .codiciCastelliCompletati().size());
    }

    @Test
    void esponeLoStatoFinaleAncheDopoLaSconfitta() {
        ContestoTest contesto = creaContesto(
                new Statistiche(10, 1, 0),
                new Statistiche(50, 20, 0),
                1
        );
        contesto.menu().nuovaPartita("Arthas", "eroe-test");
        contesto.battaglia().attacca(0);

        VistaPartita sconfitta = contesto.battaglia().eseguiTurnoNemico().stato();

        assertEquals(StatoPartita.SCONFITTA, sconfitta.statoPartita());
        assertEquals(StatoBattaglia.SCONFITTA,
                sconfitta.castello().orElseThrow().statoBattaglia());
        assertEquals(0, sconfitta.eroe().puntiVita());
    }

    private ContestoTest creaContesto(
            Statistiche statisticheEroe,
            Statistiche statisticheNemico,
            int numeroCastelli
    ) {
        ClasseEroe classe = new ClasseEroe(
                "eroe-test",
                "Eroe test",
                statisticheEroe,
                () -> new TempraDelCavaliere(new RicaricaATurni(2, 0))
        );
        TipoMostro tipo = new TipoMostro("bersaglio", "Bersaglio", statisticheNemico);
        List<Castello> castelli = java.util.stream.IntStream.rangeClosed(1, numeroCastelli)
                .mapToObj(indice -> new Castello(
                        "castello-" + indice,
                        "Castello " + indice,
                        List.of(new ConfigurazioneNemici(tipo, 1))
                ))
                .toList();
        RepositoryInMemoria repository = new RepositoryInMemoria();
        ServizioProgressione servizio = new ServizioProgressione(
                repository,
                new FabbricaEroiStandard(),
                List.of(classe),
                castelli
        );
        ServizioGioco gioco = new ServizioGioco(servizio, new FabbricaNemiciStandard());
        MenuController menu = new MenuController(gioco);
        BattagliaController battaglia = new BattagliaController(gioco);
        return new ContestoTest(repository, menu, battaglia);
    }

    private record ContestoTest(
            RepositoryInMemoria repository,
            MenuController menu,
            BattagliaController battaglia
    ) {
    }

    private static final class RepositoryInMemoria implements ProgressiRepository {

        private ProgressiPartita progressi;

        @Override
        public Optional<ProgressiPartita> carica() {
            return Optional.ofNullable(progressi);
        }

        @Override
        public void sostituisci(ProgressiPartita progressi) {
            this.progressi = progressi;
        }

        @Override
        public void elimina() {
            progressi = null;
        }
    }
}
