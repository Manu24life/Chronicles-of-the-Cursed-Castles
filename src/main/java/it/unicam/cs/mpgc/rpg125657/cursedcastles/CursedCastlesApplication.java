package it.unicam.cs.mpgc.rpg125657.cursedcastles;

import javafx.application.Application;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.view.CoordinatoreInterfaccia;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.view.GestoreViste;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Avvia l’applicazione JavaFX e collega scena, tema e coordinatore dell’interfaccia.
 */
public final class CursedCastlesApplication extends Application {
    private CoordinatoreInterfaccia coordinatore;

    /**
     * Costruisce la scena principale, collega la gestione della chiusura e avvia il coordinatore.
     *
     * @param stage finestra principale fornita da JavaFX
     * @throws IOException se il caricamento della struttura FXML principale non riesce
     */
    @Override
    public void start(Stage stage) throws IOException {
        GestoreViste viste = new GestoreViste();
        coordinatore = new CoordinatoreInterfaccia(viste, stage);
        Scene scena = new Scene(viste.root(), 1280, 800);
        GestoreViste.applicaTema(viste.root());
        stage.setTitle("Chronicles of the Cursed Castles");
        stage.setMinWidth(960);
        stage.setMinHeight(640);
        stage.setScene(scena);
        stage.setOnCloseRequest(evento -> {
            evento.consume();
            coordinatore.esci();
        });
        stage.show();
        coordinatore.avvia();
    }

    /**
     * Rilascia le risorse del coordinatore alla terminazione dell’applicazione.
     *
     */
    @Override
    public void stop() {
        if (coordinatore != null) coordinatore.close();
    }

    /**
     * Avvia il runtime JavaFX passando gli argomenti della riga di comando.
     *
     * @param args argomenti della riga di comando passati a JavaFX
     */
    public static void main(String[] args) {
        launch(args);
    }
}
