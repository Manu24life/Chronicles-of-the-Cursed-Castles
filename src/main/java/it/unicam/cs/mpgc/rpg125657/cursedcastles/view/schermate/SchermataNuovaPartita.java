package it.unicam.cs.mpgc.rpg125657.cursedcastles.view.schermate;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.controller.ClasseDisponibile;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.view.CoordinatoreInterfaccia;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Presenta la scelta del nome e della classe per una nuova partita.
 */
public final class SchermataNuovaPartita {
    private final CoordinatoreInterfaccia coordinatore;
    private final ToggleGroup scelta = new ToggleGroup();
    @FXML private TextField nome;
    @FXML private TilePane classi;
    @FXML private Button inizia;
    @FXML private Label validazione;

    /**
     * Inizializza un’istanza di {@code SchermataNuovaPartita} con le dipendenze ricevute.
     *
     * @param coordinatore coordinatore a cui vengono delegati i gesti del giocatore
     */
    public SchermataNuovaPartita(CoordinatoreInterfaccia coordinatore) {
        this.coordinatore = coordinatore;
    }

    /** Collega i controlli FXML ai comandi dopo il caricamento della schermata. */
    @FXML private void initialize() {
        nome.setTextFormatter(new TextFormatter<String>(c -> c.getControlNewText().length() <= 100 ? c : null));
        nome.textProperty().addListener((o, prima, dopo) -> aggiornaValidazione());
        scelta.selectedToggleProperty().addListener((o, prima, dopo) -> aggiornaValidazione());
        nome.setOnAction(e -> { if (!inizia.isDisabled()) crea(); });
    }

    /**
     * Popola la scelta delle classi e presenta le statistiche della classe selezionata.
     *
     * @param disponibili classi selezionabili nella nuova partita
     */
    public void mostra(List<ClasseDisponibile> disponibili) {
        for (ClasseDisponibile classe : disponibili) {
            Label simbolo = new Label(classe.nome().substring(0, 1));
            simbolo.getStyleClass().add("class-initial");
            Label titolo = new Label(classe.nome());
            titolo.getStyleClass().add("class-name");
            Label statistiche = new Label(classe.puntiVitaMassimi() + " PV    /    " + classe.attacco()
                    + " ATT    /    " + classe.difesa() + " DIF");
            statistiche.getStyleClass().add("muted");
            VBox contenuto = new VBox(12, simbolo, titolo, statistiche);
            ToggleButton opzione = new ToggleButton();
            opzione.setId("classe-" + classe.codice());
            opzione.getStyleClass().add("class-choice");
            opzione.setGraphic(contenuto);
            opzione.setToggleGroup(scelta);
            opzione.setOnAction(e -> scelta.selectToggle(opzione));
            opzione.setUserData(classe.codice());
            opzione.setAccessibleText(classe.nome() + ", " + statistiche.getText());
            opzione.setMaxWidth(Double.MAX_VALUE);
            classi.getChildren().add(opzione);
        }
        if (!scelta.getToggles().isEmpty()) scelta.selectToggle(scelta.getToggles().getFirst());
        aggiornaValidazione();
        Platform.runLater(nome::requestFocus);
    }

    private void aggiornaValidazione() {
        boolean vuoto = nome.getText().isBlank();
        inizia.setDisable(vuoto || scelta.getSelectedToggle() == null);
        validazione.setText(vuoto ? "Il tuo nome è necessario per iniziare." : "Il nome verrà inciso nel salvataggio.");
    }

    @FXML private void crea() {
        if (!inizia.isDisabled()) coordinatore.nuovaPartita(nome.getText().strip(),
                (String) scelta.getSelectedToggle().getUserData());
    }

    @FXML private void indietro() { coordinatore.mostraMenu(); }
}
