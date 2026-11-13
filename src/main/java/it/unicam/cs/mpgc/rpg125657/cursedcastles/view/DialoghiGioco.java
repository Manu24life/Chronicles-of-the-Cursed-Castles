package it.unicam.cs.mpgc.rpg125657.cursedcastles.view;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;
import javafx.geometry.Insets;

/** Dialoghi con lo stesso tema della finestra, senza dipendenze dal dominio. */
public final class DialoghiGioco {
    private final Window proprietario;

    public DialoghiGioco(Window proprietario) {
        this.proprietario = proprietario;
    }

    public boolean conferma(String titolo, String messaggio) {
        Alert dialogo = crea(Alert.AlertType.CONFIRMATION, titolo, messaggio);
        ButtonType conferma = new ButtonType("Conferma");
        ButtonType annulla = new ButtonType("Annulla", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogo.getButtonTypes().setAll(conferma, annulla);
        return dialogo.showAndWait().filter(conferma::equals).isPresent();
    }

    public void errore(String messaggio) {
        crea(Alert.AlertType.ERROR, "Qualcosa ha interrotto il cammino", messaggio).showAndWait();
    }

    private Alert crea(Alert.AlertType tipo, String titolo, String messaggio) {
        Alert dialogo = new Alert(tipo);
        dialogo.initOwner(proprietario);
        dialogo.setTitle("Chronicles of the Cursed Castles");
        dialogo.setHeaderText(titolo);
        dialogo.setContentText(messaggio);
        dialogo.setGraphic(null);
        GestoreViste.applicaTema(dialogo.getDialogPane());
        dialogo.getDialogPane().getStyleClass().add("dark-dialog");
        dialogo.getDialogPane().setPadding(new Insets(18));
        dialogo.getDialogPane().setPrefWidth(480);
        return dialogo;
    }
}
