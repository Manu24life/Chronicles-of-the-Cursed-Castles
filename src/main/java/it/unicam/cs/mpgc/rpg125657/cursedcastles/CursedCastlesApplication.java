package it.unicam.cs.mpgc.rpg125657.cursedcastles;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public final class CursedCastlesApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                CursedCastlesApplication.class.getResource("main-view.fxml")
        );
        stage.setTitle("Chronicles of the Cursed Castles");
        stage.setScene(new Scene(loader.load(), 640, 420));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
