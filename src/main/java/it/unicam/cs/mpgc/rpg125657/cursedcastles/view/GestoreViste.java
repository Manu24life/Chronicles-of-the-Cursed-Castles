package it.unicam.cs.mpgc.rpg125657.cursedcastles.view;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.CursedCastlesApplication;
import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/** Racchiude caricamento FXML, navigazione e indicatore delle operazioni asincrone. */
public final class GestoreViste {
    private final StackPane root;
    private final StackPane contenuto;
    private final VBox attesa;
    private final Label messaggioAttesa = new Label();

    public GestoreViste() throws IOException {
        root = FXMLLoader.load(risorsa("main-view.fxml"));
        contenuto = (StackPane) root.lookup("#contenuto");
        ProgressIndicator indicatore = new ProgressIndicator();
        indicatore.setMaxSize(30, 30);
        attesa = new VBox(16, indicatore, messaggioAttesa);
        attesa.setAlignment(Pos.CENTER);
        attesa.getStyleClass().add("loading-veil");
        root.getChildren().add(attesa);
        occupato(false, "");
    }

    public static URL risorsa(String nome) {
        return Objects.requireNonNull(CursedCastlesApplication.class.getResource(nome),
                "Risorsa grafica mancante: " + nome);
    }

    public Parent root() { return root; }

    public static void applicaTema(Parent radice) {
        radice.getStylesheets().add(risorsa("cursed-castles.css").toExternalForm());
        String famiglia = Font.getFamilies().contains("Georgia") ? "Georgia" : "Serif";
        radice.setStyle("-fx-font-family: '" + famiglia + "';");
    }

    public <T> T mostra(String fxml, T controller) {
        FXMLLoader loader = new FXMLLoader(risorsa(fxml));
        loader.setControllerFactory(tipo -> {
            if (!tipo.isInstance(controller)) throw new IllegalArgumentException("Controller FXML inatteso: " + tipo);
            return controller;
        });
        try {
            Parent vista = loader.load();
            contenuto.getChildren().setAll(vista);
            FadeTransition dissolvenza = new FadeTransition(Duration.millis(180), vista);
            dissolvenza.setFromValue(0);
            dissolvenza.setToValue(1);
            dissolvenza.play();
            return controller;
        } catch (IOException errore) {
            throw new IllegalStateException("Impossibile aprire la schermata " + fxml, errore);
        }
    }

    public void occupato(boolean valore, String messaggio) {
        messaggioAttesa.setText(messaggio);
        attesa.setVisible(valore && !messaggio.isBlank());
        attesa.setManaged(attesa.isVisible());
        contenuto.setDisable(valore);
    }

    public void erroreAvvio(String messaggio, Runnable riprova, Runnable esci) {
        Label titolo = new Label("LA SOGLIA RESTA CHIUSA");
        titolo.getStyleClass().add("section-title");
        Label descrizione = new Label(messaggio);
        descrizione.setWrapText(true);
        descrizione.setMaxWidth(500);
        Button tentativo = new Button("Riprova");
        tentativo.setOnAction(e -> riprova.run());
        Button uscita = new Button("Esci");
        uscita.setOnAction(e -> esci.run());
        VBox pannello = new VBox(24, titolo, descrizione, tentativo, uscita);
        pannello.setAlignment(Pos.CENTER);
        pannello.getStyleClass().add("screen");
        contenuto.getChildren().setAll(pannello);
    }
}
