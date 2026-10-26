package it.unicam.cs.mpgc.rpg125657.cursedcastles.application;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.controller.BattagliaController;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello.Castelli;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello.FabbricaNemici;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello.FabbricaNemiciStandard;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro.Mostro;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro.TipoMostro;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.Statistiche;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.ClassiEroe;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.FabbricaEroi;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.FabbricaEroiStandard;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.partita.Partita;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.progressione.ProgressiPartita;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.progressione.ProgressiRepository;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.progressione.SalvataggioNonValidoException;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ServizioGiocoTest {
    private final RepositoryTest repository = new RepositoryTest();
    private final FabbricaNemici nemici = new FabbricaNemiciStandard();

    private ServizioGioco gioco(FabbricaEroi eroi, FabbricaNemici fabbricaNemici) {
        return new ServizioGioco(new ServizioProgressione(repository, eroi,
                ClassiEroe.tutte(), Castelli.tutti()), fabbricaNemici);
    }

    @Test
    void conservaSalvataggioESessioneSeLaCreazioneNemiciFallisce() {
        boolean[] fallisce = {false};
        ServizioGioco gioco = gioco(new FabbricaEroiStandard(), castello -> {
            if (fallisce[0]) throw new IllegalStateException("Creazione nemici fallita");
            return nemici.creaNemici(castello);
        });
        gioco.nuovaPartita("Prima", "mago");
        Partita precedente = gioco.richiediPartita();
        ProgressiPartita salvati = repository.progressi;
        fallisce[0] = true;

        assertThrows(IllegalStateException.class, () -> gioco.nuovaPartita("Seconda", "cavaliere"));
        assertSame(precedente, gioco.richiediPartita());
        assertEquals(salvati, repository.progressi);
    }

    @Test
    void conservaSalvataggioSeLaCreazioneEroeFallisce() {
        repository.progressi = ProgressiPartita.nuovi("Prima", "mago");
        ProgressiPartita salvati = repository.progressi;
        ServizioGioco gioco = gioco((nome, classe, abilita) -> {
            throw new IllegalStateException("Creazione eroe fallita");
        }, nemici);
        assertThrows(IllegalStateException.class, () -> gioco.nuovaPartita("Seconda", "cavaliere"));
        assertEquals(salvati, repository.progressi);
        assertTrue(gioco.partita().isEmpty());
    }

    @Test
    void conservaSessioneSeIlSalvataggioDellaNuovaPartitaFallisce() {
        ServizioGioco gioco = gioco(new FabbricaEroiStandard(), nemici);
        gioco.nuovaPartita("Prima", "mago");
        Partita precedente = gioco.richiediPartita();
        ProgressiPartita salvati = repository.progressi;
        repository.fallisce = true;
        assertThrows(EccezionePersistenza.class, () -> gioco.nuovaPartita("Seconda", "cavaliere"));
        assertSame(precedente, gioco.richiediPartita());
        assertEquals(salvati, repository.progressi);
    }

    @Test
    void rifiutaSalvataggiFuoriOrdineOConRiferimentiSconosciuti() {
        ServizioGioco gioco = gioco(new FabbricaEroiStandard(), nemici);
        gioco.nuovaPartita("Prima", "mago");
        Partita precedente = gioco.richiediPartita();
        for (ProgressiPartita invalidi : List.of(
                new ProgressiPartita("Prima", "mago", Set.of("ferro")),
                new ProgressiPartita("Prima", "mago", Set.of("ossa", "maledizione")),
                new ProgressiPartita("Prima", "mago", Set.of("sconosciuto")),
                ProgressiPartita.nuovi("Prima", "sconosciuta"))) {
            repository.progressi = invalidi;
            assertThrows(SalvataggioNonValidoException.class, gioco::carica);
            assertThrows(SalvataggioNonValidoException.class, gioco::continuaPartita);
            assertSame(precedente, gioco.richiediPartita());
        }
    }

    @Test
    void accettaTuttiIPrefissiValidiERiconosceLaCampagnaCompletata() {
        ServizioGioco gioco = gioco(new FabbricaEroiStandard(), nemici);
        for (int i = 0; i <= Castelli.tutti().size(); i++) {
            Set<String> completati = Castelli.tutti().subList(0, i).stream()
                    .map(c -> c.codice()).collect(java.util.stream.Collectors.toSet());
            repository.progressi = new ProgressiPartita("Eroe", "mago", completati);
            assertEquals(repository.progressi, gioco.carica().orElseThrow());
            assertEquals(i == Castelli.tutti().size(), gioco.campagnaCompletata(repository.progressi));
        }
        assertThrows(IllegalStateException.class, gioco::continuaPartita);
        assertTrue(gioco.partita().isEmpty());
    }

    @Test
    void riprovaLaVittoriaSenzaPerdereLoStatoRicreandoIlController() {
        ServizioGioco gioco = gioco(new FabbricaEroiStandard(), castello -> List.of(
                new Mostro("Bersaglio", new TipoMostro("bersaglio", "Bersaglio", new Statistiche(1, 0, 0)))));
        gioco.nuovaPartita("Eroe", "mago");
        repository.fallisce = true;
        BattagliaController controller = new BattagliaController(gioco);
        assertThrows(EccezionePersistenza.class, () -> controller.attacca(0));
        assertFalse(gioco.vittoriaSalvata());
        assertThrows(EccezionePersistenza.class, controller::vaiAlCastelloSuccessivo);
        assertEquals(Castelli.OSSA, gioco.richiediPartita().castelloCorrente());

        repository.fallisce = false;
        BattagliaController ricreato = new BattagliaController(gioco);
        ricreato.salvaVittoria();
        int scritture = repository.scritture;
        assertTrue(new BattagliaController(gioco).stato().castello().orElseThrow().vittoriaSalvata());
        ricreato.salvaVittoria();
        assertEquals(scritture, repository.scritture);
        ricreato.vaiAlCastelloSuccessivo();
        assertEquals(Castelli.FERRO, gioco.richiediPartita().castelloCorrente());
        assertFalse(gioco.vittoriaSalvata());
    }

    @Test
    void unErroreDiEliminazioneNonTerminaLaSessione() {
        ServizioGioco gioco = gioco(new FabbricaEroiStandard(), nemici);
        gioco.nuovaPartita("Eroe", "mago");
        Partita precedente = gioco.richiediPartita();
        repository.fallisce = true;
        assertThrows(EccezionePersistenza.class, gioco::eliminaSalvataggio);
        assertSame(precedente, gioco.richiediPartita());
    }

    private static final class RepositoryTest implements ProgressiRepository {
        ProgressiPartita progressi;
        boolean fallisce;
        int scritture;
        public Optional<ProgressiPartita> carica() { return Optional.ofNullable(progressi); }
        public void sostituisci(ProgressiPartita nuovi) {
            verifica();
            progressi = nuovi;
            scritture++;
        }
        public void elimina() { verifica(); progressi = null; }
        private void verifica() {
            if (fallisce) throw new EccezionePersistenza("Errore simulato", new RuntimeException());
        }
    }
}
