package it.unicam.cs.mpgc.rpg125657.cursedcastles.view;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.application.ServizioGioco;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.application.ServizioProgressione;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.controller.*;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello.*;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.RicaricaATurni;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.*;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita.TempraDelCavaliere;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro.TipoMostro;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.Statistiche;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.progressione.*;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.view.schermate.SchermataBattaglia;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.view.schermate.SchermataMenu;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.view.schermate.SchermataNuovaPartita;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.view.grafica.RisorseBattaglia;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.*;

/** Verifica il vero FXML e il percorso UI → controller → dominio, senza database personali. */
@Timeout(20)
class InterfacciaJavaFxTest {
    private GestoreViste viste;
    private CoordinatoreInterfaccia coordinatore;
    private Stage finestra;

    @BeforeAll static void avviaToolkit() throws Exception {
        CountDownLatch pronto = new CountDownLatch(1);
        Platform.startup(() -> { Platform.setImplicitExit(false); pronto.countDown(); });
        assertTrue(pronto.await(10, TimeUnit.SECONDS));
        RisorseBattaglia.precarica();
    }

    @BeforeEach void creaFinestra() throws Exception {
        fx(() -> {
            viste = new GestoreViste();
            finestra = new Stage();
            Scene scena = new Scene(viste.root(), 1280, 800);
            scena.getStylesheets().add(GestoreViste.risorsa("cursed-castles.css").toExternalForm());
            finestra.setScene(scena);
            viste.root().resize(1280, 800);
            coordinatore = new CoordinatoreInterfaccia(viste, finestra);
            return null;
        });
    }

    @AfterEach void chiudiFinestra() throws Exception {
        fx(() -> {
            coordinatore.close();
            finestra.hide();
            List.copyOf(Window.getWindows()).forEach(Window::hide);
            return null;
        });
    }

    @AfterAll static void fermaToolkit() { Platform.exit(); }

    @Test void caricaLeSchermateConICataloghiRealiEGeneraAnteprime() throws Exception {
        fx(() -> {
            StatoMenu menu = new StatoMenu(ConfigurazioneEroe.tutte().stream().map(c -> new ClasseDisponibile(
                    c.codice(), c.nome(), c.statistiche().puntiVitaMassimi(), c.statistiche().attacco(),
                    c.statistiche().difesa())).toList(), Optional.empty());
            viste.mostra("menu-view.fxml", new SchermataMenu(coordinatore)).mostra(menu, false);
            return null;
        });
        cattura("menu");
        fx(() -> {
            viste.root().resize(960, 640);
            viste.root().applyCss();
            viste.root().layout();
            var bounds = nodo("nuova").localToScene(nodo("nuova").getBoundsInLocal());
            assertTrue(bounds.getMaxY() <= 641);
            return null;
        });
        cattura("menu-compatta");
        fx(() -> {
            viste.mostra("nuova-partita-view.fxml", new SchermataNuovaPartita(coordinatore))
                    .mostra(ConfigurazioneEroe.tutte().stream().map(c -> new ClasseDisponibile(c.codice(), c.nome(),
                            c.statistiche().puntiVitaMassimi(), c.statistiche().attacco(), c.statistiche().difesa())).toList());
            assertTrue(pulsante("inizia").isDisabled());
            ((TextField) nodo("nome")).setText("Elda");
            assertFalse(pulsante("inizia").isDisabled());
            ((TextField) nodo("nome")).setText(" ");
            assertTrue(pulsante("inizia").isDisabled());
            return null;
        });
        cattura("creazione");
        for (ClasseEroe classe : ConfigurazioneEroe.tutte()) {
            ServizioGioco gioco = new ServizioGioco(new ServizioProgressione(new RepositoryTest()), new FabbricaNemiciStandard());
            gioco.nuovaPartita("Elda", classe.codice());
            BattagliaController controller = new BattagliaController(gioco);
            fx(() -> {
                viste.mostra("battaglia-view.fxml", new SchermataBattaglia(coordinatore,
                        FXCollections.observableArrayList("Ingresso: Castello delle Ossa."))).mostra(controller.stato());
                assertTrue(pulsante("cura").isDisabled());
                assertFalse(pulsante("abilita").isDisabled());
                return null;
            });
            cattura("battaglia-" + classe.codice());
            fx(() -> {
                viste.root().resize(960, 640);
                viste.root().applyCss();
                viste.root().layout();
                Node azione = nodo("attacca");
                var bounds = azione.localToScene(azione.getBoundsInLocal());
                assertTrue(bounds.getMaxX() <= 961 && bounds.getMaxY() <= 641, "Comandi fuori dalla finestra minima");
                return null;
            });
            cattura("battaglia-compatta-" + classe.codice());
        }
    }

    @Test void esegueAutomaticamenteUnaSolaRispostaNemicaDopoLaPausa() throws Exception {
        RepositoryTest repository = avviaCampagna(10, 50);
        creaDallaSchermata();
        fx(() -> { pulsante("attacca").fire(); return null; });
        attendi(() -> etichetta("turno").contains("si prepara"));
        fx(() -> {
            assertTrue(pulsante("attacca").isDisabled());
            assertTrue(pulsante("alMenu").isDisabled());
            assertEquals("Punti vita: 100 su 100", nodo("vitaEroe").getAccessibleText());
            return null;
        });
        attendi(() -> "Punti vita: 80 su 100".equals(nodo("vitaEroe").getAccessibleText()));
        fx(() -> {
            assertFalse(pulsante("attacca").isDisabled());
            assertTrue(etichetta("turno").contains("Il tuo turno"));
            assertTrue(repository.progressi.codiciCastelliCompletati().isEmpty());
            return null;
        });
    }

    @Test void salvaLaVittoriaAvanzaEConcludeLaCampagna() throws Exception {
        RepositoryTest repository = avviaCampagna(100, 1);
        creaDallaSchermata();
        fx(() -> { pulsante("attacca").fire(); return null; });
        attendi(() -> etichetta("titoloEsito").equals("Castello superato"));
        assertTrue(repository.progressi.isCastelloCompletato("castello-1"));
        fx(() -> { viste.root().resize(960, 640); return null; });
        cattura("vittoria-compatta");
        fx(() -> {
            var bounds = nodo("avanti").localToScene(nodo("avanti").getBoundsInLocal());
            assertTrue(bounds.getMaxY() <= 641 && bounds.getMinY() >= 0);
            return null;
        });
        fx(() -> { pulsante("avanti").fire(); return null; });
        attendi(() -> etichetta("castello").equals("Castello 2"));
        fx(() -> { pulsante("attacca").fire(); return null; });
        attendi(() -> etichetta("titoloEsito").equals("Castello superato"));
        fx(() -> { pulsante("avanti").fire(); return null; });
        attendi(() -> etichetta("progresso").equals("CAMPAGNA COMPLETATA"));
        assertEquals(2, repository.progressi.codiciCastelliCompletati().size());
        fx(() -> { pulsante("tornaAlMenu").fire(); return null; });
        attendi(() -> nodo("continua") != null);
        fx(() -> { assertTrue(pulsante("continua").isDisabled()); return null; });
    }

    @Test void ritentaSoloIlSalvataggioDopoUnCommitFallito() throws Exception {
        RepositoryTest repository = avviaCampagna(100, 1);
        creaDallaSchermata();
        repository.fallisce = true;
        fx(() -> { pulsante("attacca").fire(); return null; });
        attendi(() -> nodo("avanti") != null && pulsante("avanti").getText().equals("Riprova salvataggio"));
        fx(() -> {
            for (Window w : List.copyOf(Window.getWindows())) {
                if (w.getScene().getRoot() instanceof DialogPane dialogo) {
                    ((Button) dialogo.lookupButton(ButtonType.OK)).fire();
                }
            }
            return null;
        });
        repository.fallisce = false;
        fx(() -> { pulsante("avanti").fire(); return null; });
        attendi(() -> pulsante("avanti").getText().equals("Al prossimo castello  ›"));
        assertTrue(repository.progressi.isCastelloCompletato("castello-1"));
        fx(() -> { assertEquals("Castello 1", etichetta("castello")); return null; });
    }

    @Test void usaLAbilitaDelMagoSulBersaglioSceltoEGestisceLeCure() throws Exception {
        avviaCatalogoReale();
        creaDallaSchermata("mago");
        fx(() -> {
            pulsante("nemico-2").fire();
            pulsante("abilita").fire();
            return null;
        });
        attendi(() -> "Punti vita: 70 su 80".equals(nodo("vitaEroe").getAccessibleText()));
        fx(() -> {
            assertTrue(nodo("nemico-2").getAccessibleText().contains("27 punti vita, bersaglio"));
            assertTrue(nodo("nemico-0").getAccessibleText().contains("35 punti vita"));
            assertTrue(pulsante("abilita").isDisabled());
            pulsante("cura").fire();
            return null;
        });
        attendi(() -> "Punti vita: 70 su 80".equals(nodo("vitaEroe").getAccessibleText())
                && pulsante("cura").getText().contains("1 rimaste"));
    }

    @Test void usaLAbilitaPersonaleDelCavaliere() throws Exception {
        avviaCatalogoReale();
        creaDallaSchermata("cavaliere");
        fx(() -> { pulsante("abilita").fire(); return null; });
        attendi(() -> etichetta("turno").contains("Il tuo turno") && etichetta("attacco").equals("20"));
        fx(() -> {
            assertEquals("13", etichetta("difesa"));
            assertTrue(etichetta("bonus").contains("+15% ATT / +40% DIF"));
            assertTrue(pulsante("abilita").isDisabled());
            return null;
        });
    }

    @Test void mostraLaSconfittaERiprendeDalCheckpoint() throws Exception {
        avviaCampagna(1, 999);
        creaDallaSchermata();
        for (int i = 0; i < 5; i++) {
            fx(() -> { pulsante("attacca").fire(); return null; });
            final int attesi = 80 - i * 20;
            attendi(() -> ("Punti vita: " + attesi + " su 100").equals(nodo("vitaEroe").getAccessibleText()));
        }
        fx(() -> {
            assertEquals("Il silenzio ritorna", etichetta("titoloEsito"));
            assertTrue(pulsante("attacca").isDisabled());
            pulsante("tornaAlMenu").fire();
            return null;
        });
        attendi(() -> nodo("continua") != null);
        fx(() -> { pulsante("continua").fire(); return null; });
        attendi(() -> nodo("vitaEroe") != null && "Punti vita: 100 su 100".equals(nodo("vitaEroe").getAccessibleText()));
    }

    @Test void rendeGliAltriCastelliConLeIncisioniDeiNemici() throws Exception {
        RepositoryTest repository = new RepositoryTest();
        ServizioGioco gioco = new ServizioGioco(new ServizioProgressione(repository), new FabbricaNemiciStandard());
        List<String> completati = new java.util.ArrayList<>();
        for (Castello castello : Castelli.tutti()) {
            repository.progressi = new ProgressiPartita("Elda", "cavaliere", java.util.Set.copyOf(completati));
            gioco.continuaPartita();
            VistaPartita stato = new BattagliaController(gioco).stato();
            fx(() -> {
                viste.mostra("battaglia-view.fxml", new SchermataBattaglia(coordinatore,
                        FXCollections.observableArrayList("Ingresso: " + castello.nome() + "."))).mostra(stato);
                return null;
            });
            cattura("castello-" + castello.codice());
            completati.add(castello.codice());
        }
    }

    @Test void condivideLeImmaginiMaNonINodiFxglEVerificaLaTrasparenza() throws Exception {
        fx(() -> {
            for (String tipo : List.of("scheletro", "orco", "vampiro", "drago")) {
                var primo = RisorseBattaglia.nemico(tipo);
                var secondo = RisorseBattaglia.nemico(tipo);
                assertNotSame(primo, secondo);
                assertSame(primo.getImage(), secondo.getImage());
                primo.setOpacity(.4);
                assertEquals(1, secondo.getOpacity());
                var immagine = primo.getImage();
                assertFalse(immagine.isError());
                assertEquals(0, immagine.getPixelReader().getColor(0, 0).getOpacity());
                assertEquals(0, immagine.getPixelReader().getColor((int) immagine.getWidth() - 1, 0).getOpacity());
            }
            assertNotSame(RisorseBattaglia.scenario("ossa").getImage(),
                    RisorseBattaglia.scenario("ferro").getImage());
            return null;
        });
    }

    @Test void animaIlDannoConFxglESiFermaAlDistaccoDallaScena() throws Exception {
        var figura = fx(() -> {
            var sprite = new it.unicam.cs.mpgc.rpg125657.cursedcastles.view.componenti.IllustrazioneNemico("scheletro");
            var pannello = new javafx.scene.layout.StackPane(sprite);
            finestra.setScene(new Scene(pannello, 400, 400));
            finestra.show();
            sprite.riceviDanno(false);
            return sprite;
        });
        var texture = fx(() -> (com.almasb.fxgl.texture.Texture) figura.getChildrenUnmodifiable().getFirst());
        attendi(() -> texture.getTranslateX() > 0);
        fx(() -> {
            ((javafx.scene.layout.StackPane) finestra.getScene().getRoot()).getChildren().clear();
            assertEquals(0, texture.getTranslateX());
            assertNull(figura.getScene());
            return null;
        });
    }

    @Test void recuperaUnoSlotNonValidoSoloDopoLaConfermaDiCancellazione() throws Exception {
        RepositoryTest repository = new RepositoryTest();
        repository.progressi = ProgressiPartita.nuovi("Elda", "classe-sconosciuta");
        ServizioGioco gioco = new ServizioGioco(new ServizioProgressione(repository), new FabbricaNemiciStandard());
        fx(() -> { coordinatore.avvia(new MenuController(gioco), new BattagliaController(gioco)); return null; });
        attendi(() -> nodo("rileggi") != null && nodo("rileggi").isVisible());
        fx(() -> {
            rispondiAlDialogo("OK");
            assertTrue(pulsante("nuova").isDisabled());
            assertFalse(pulsante("elimina").isDisabled());
            return null;
        });
        FutureTask<Void> annullamento = apriConfermaEliminazione();
        fx(() -> { rispondiAlDialogo("Annulla"); return null; });
        annullamento.get(5, TimeUnit.SECONDS);
        assertNotNull(repository.progressi);
        FutureTask<Void> conferma = apriConfermaEliminazione();
        fx(() -> { rispondiAlDialogo("Conferma"); return null; });
        conferma.get(5, TimeUnit.SECONDS);
        attendi(() -> nodo("nuova") != null && !pulsante("nuova").isDisabled());
        assertNull(repository.progressi);
    }

    private FutureTask<Void> apriConfermaEliminazione() throws Exception {
        FutureTask<Void> azione = new FutureTask<>(() -> { pulsante("elimina").fire(); return null; });
        Platform.runLater(azione);
        attendi(() -> Window.getWindows().stream().anyMatch(w ->
                w.getScene().getRoot() instanceof DialogPane d
                        && d.getButtonTypes().stream().anyMatch(t -> t.getText().equals("Conferma"))));
        return azione;
    }

    private static void rispondiAlDialogo(String testo) {
        for (Window finestra : List.copyOf(Window.getWindows())) {
            if (finestra.getScene().getRoot() instanceof DialogPane dialogo) {
                dialogo.getButtonTypes().stream().filter(tipo -> tipo.getText().equals(testo)).findFirst()
                        .ifPresent(tipo -> ((Button) dialogo.lookupButton(tipo)).fire());
            }
        }
    }

    private void avviaCatalogoReale() throws Exception {
        ServizioGioco gioco = new ServizioGioco(new ServizioProgressione(new RepositoryTest()), new FabbricaNemiciStandard());
        fx(() -> { coordinatore.avvia(new MenuController(gioco), new BattagliaController(gioco)); return null; });
        attendi(() -> nodo("nuova") != null);
    }

    private RepositoryTest avviaCampagna(int attacco, int vitaNemico) throws Exception {
        RepositoryTest repository = new RepositoryTest();
        ClasseEroe classe = new ClasseEroe("eroe-test", "Eroe test", new Statistiche(100, attacco, 0),
                () -> new TempraDelCavaliere(new RicaricaATurni(3, 0)));
        TipoMostro tipo = new TipoMostro("scheletro", "Scheletro", new Statistiche(vitaNemico, 20, 0));
        List<Castello> castelli = java.util.stream.IntStream.rangeClosed(1, 2).mapToObj(i ->
                new Castello("castello-" + i, "Castello " + i, List.of(new ConfigurazioneNemici(tipo, 1)))).toList();
        ServizioGioco gioco = new ServizioGioco(new ServizioProgressione(repository, new FabbricaEroiStandard(),
                List.of(classe), castelli), new FabbricaNemiciStandard());
        fx(() -> { coordinatore.avvia(new MenuController(gioco), new BattagliaController(gioco)); return null; });
        attendi(() -> nodo("nuova") != null);
        return repository;
    }

    private void creaDallaSchermata() throws Exception { creaDallaSchermata(null); }

    private void creaDallaSchermata(String classe) throws Exception {
        fx(() -> { pulsante("nuova").fire(); return null; });
        fx(() -> {
            if (classe != null) ((ToggleButton) nodo("classe-" + classe)).fire();
            ((TextField) nodo("nome")).setText("Elda");
            pulsante("inizia").fire();
            return null;
        });
        attendi(() -> nodo("attacca") != null);
    }

    private Node nodo(String id) {
        // Il contenuto di ScrollPane entra nell'albero grafico quando viene creata la skin.
        viste.root().applyCss();
        viste.root().layout();
        return viste.root().lookup("#" + id);
    }
    private Button pulsante(String id) { return (Button) nodo(id); }
    private String etichetta(String id) { return ((Label) nodo(id)).getText(); }

    private void attendi(BooleanSupplier condizione) throws Exception {
        long limite = System.nanoTime() + TimeUnit.SECONDS.toNanos(8);
        while (System.nanoTime() < limite) {
            if (fx(condizione::getAsBoolean)) return;
            Thread.sleep(15);
        }
        fail("L'interfaccia non ha raggiunto lo stato atteso");
    }

    private void cattura(String nome) throws Exception {
        // Attende il completamento della sola transizione grafica, senza rallentare la produzione.
        Thread.sleep(230);
        WritableImage immagine = fx(() -> {
            if (!nome.contains("compatta")) viste.root().resize(1280, 800);
            viste.root().applyCss();
            viste.root().layout();
            return viste.root().snapshot(null, null);
        });
        BufferedImage png = new BufferedImage((int) immagine.getWidth(), (int) immagine.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < png.getHeight(); y++) for (int x = 0; x < png.getWidth(); x++) {
            png.setRGB(x, y, immagine.getPixelReader().getArgb(x, y));
        }
        Path cartella = Path.of("build", "reports", "ui");
        Files.createDirectories(cartella);
        ImageIO.write(png, "png", cartella.resolve(nome + ".png").toFile());
    }

    private static <T> T fx(Callable<T> azione) throws Exception {
        FutureTask<T> task = new FutureTask<>(azione);
        Platform.runLater(task);
        return task.get(10, TimeUnit.SECONDS);
    }

    private static final class RepositoryTest implements ProgressiRepository {
        private volatile ProgressiPartita progressi;
        private volatile boolean fallisce;
        @Override public Optional<ProgressiPartita> carica() { return Optional.ofNullable(progressi); }
        @Override public void sostituisci(ProgressiPartita valore) {
            if (fallisce) throw new EccezionePersistenza("Salvataggio di prova non riuscito", new IllegalStateException("Commit"));
            progressi = valore;
        }
        @Override public void elimina() { progressi = null; }
    }
}
