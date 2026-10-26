package it.unicam.cs.mpgc.rpg125657.cursedcastles.controller;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello.Castello;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello.ConfigurazioneNemici;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello.FabbricaNemiciStandard;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.RicaricaATurni;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.ClasseEroe;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.FabbricaEroiStandard;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita.TempraDelCavaliere;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro.TipoMostro;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.Statistiche;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.progressione.ProgressiPartita;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.progressione.ProgressiRepository;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.application.ServizioProgressione;
import org.junit.jupiter.api.BeforeEach;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.application.ServizioGioco;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MenuControllerTest {

    private RepositoryInMemoria repository;
    private ServizioGioco gioco;
    private MenuController controller;
    private Castello primoCastello;
    private Castello secondoCastello;

    @BeforeEach
    void inizializza() {
        ClasseEroe classe = new ClasseEroe(
                "eroe-test",
                "Eroe test",
                new Statistiche(100, 20, 5),
                () -> new TempraDelCavaliere(new RicaricaATurni(2, 0))
        );
        TipoMostro tipo = new TipoMostro(
                "bersaglio",
                "Bersaglio",
                new Statistiche(10, 5, 0)
        );
        primoCastello = new Castello(
                "primo",
                "Primo castello",
                List.of(new ConfigurazioneNemici(tipo, 1))
        );
        secondoCastello = new Castello(
                "secondo",
                "Secondo castello",
                List.of(new ConfigurazioneNemici(tipo, 1))
        );
        List<ClasseEroe> classi = List.of(classe);
        List<Castello> castelli = List.of(primoCastello, secondoCastello);
        repository = new RepositoryInMemoria();
        ServizioProgressione servizio = new ServizioProgressione(
                repository,
                new FabbricaEroiStandard(),
                classi,
                castelli
        );
        gioco = new ServizioGioco(servizio, new FabbricaNemiciStandard());
        controller = new MenuController(gioco);
    }

    @Test
    void creaUnaNuovaPartitaESostituisceLoSlot() {
        controller.nuovaPartita("Arthas", "eroe-test");

        ProgressiPartita salvati = repository.carica().orElseThrow();
        assertEquals("Arthas", salvati.nomeEroe());
        assertEquals("eroe-test", salvati.codiceClasse());
        assertTrue(salvati.codiciCastelliCompletati().isEmpty());
        assertEquals(primoCastello, gioco.richiediPartita().castelloCorrente());

        StatoMenu stato = controller.stato();
        assertEquals(1, stato.classiDisponibili().size());
        assertEquals("Arthas", stato.salvataggio().orElseThrow().nomeEroe());
        assertFalse(stato.salvataggio().orElseThrow().campagnaCompletata());
    }

    @Test
    void continuaDalPrimoCastelloNonCompletato() {
        repository.sostituisci(new ProgressiPartita(
                "Arthas",
                "eroe-test",
                java.util.Set.of(primoCastello.codice())
        ));

        controller.continuaPartita();

        assertEquals(secondoCastello, gioco.richiediPartita().castelloCorrente());
    }

    @Test
    void eliminaSalvataggioESessione() {
        controller.nuovaPartita("Arthas", "eroe-test");

        controller.eliminaSalvataggio();

        assertTrue(repository.carica().isEmpty());
        assertTrue(gioco.partita().isEmpty());
        assertThrows(IllegalStateException.class, gioco::richiediPartita);
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
