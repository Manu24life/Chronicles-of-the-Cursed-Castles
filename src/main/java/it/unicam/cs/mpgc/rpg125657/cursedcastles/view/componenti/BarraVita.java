package it.unicam.cs.mpgc.rpg125657.cursedcastles.view.componenti;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/** Rappresenta i valori ricevuti dallo snapshot, senza modificare il personaggio. */
public final class BarraVita extends VBox {
    private final Label valore = new Label();
    private final ProgressBar barra = new ProgressBar(1);
    private Timeline animazione;

    public BarraVita() {
        super(6);
        getStyleClass().add("health");
        valore.getStyleClass().add("health-value");
        barra.setMaxWidth(Double.MAX_VALUE);
        getChildren().addAll(valore, barra);
    }

    public void aggiorna(int attuali, int massimi) {
        valore.setText(attuali + " / " + massimi + " PV");
        setAccessibleText("Punti vita: " + attuali + " su " + massimi);
        double frazione = (double) attuali / massimi;
        if (animazione != null) animazione.stop();
        if (getScene() == null) barra.setProgress(frazione);
        else {
            animazione = new Timeline(new KeyFrame(Duration.millis(200),
                    new KeyValue(barra.progressProperty(), frazione)));
            animazione.play();
        }
    }
}
