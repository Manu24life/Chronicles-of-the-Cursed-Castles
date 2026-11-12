package it.unicam.cs.mpgc.rpg125657.cursedcastles;

import javafx.application.Application;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.view.CoordinatoreInterfaccia;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.view.GestoreViste;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public final class CursedCastlesApplication extends Application {
    private CoordinatoreInterfaccia coordinatore;

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

    @Override
    public void stop() {
        if (coordinatore != null) coordinatore.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
