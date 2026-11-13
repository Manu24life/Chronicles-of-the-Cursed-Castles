package it.unicam.cs.mpgc.rpg125657.cursedcastles.view.schermate;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.controller.StatoMenu;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.view.CoordinatoreInterfaccia;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public final class SchermataMenu {
    private final CoordinatoreInterfaccia coordinatore;
    @FXML private Button continua;
    @FXML private Button nuova;
    @FXML private Button elimina;
    @FXML private Button rileggi;
    @FXML private Label nomeSalvato;
    @FXML private Label dettagliSalvataggio;
    @FXML private Label errore;
    @FXML private Label titolo;
    @FXML private StackPane radiceMenu;

    public SchermataMenu(CoordinatoreInterfaccia coordinatore) {
        this.coordinatore = coordinatore;
    }

    @FXML private void initialize() {
        radiceMenu.heightProperty().addListener((o, prima, dopo) ->
                titolo.setStyle("-fx-font-size: " + (dopo.doubleValue() < 730 ? 44 : 64) + "px;"));
    }

    public void mostra(StatoMenu menu, boolean letturaFallita) {
        boolean ripresa = coordinatore.puoRiprendere();
        boolean disponibile = menu.salvataggio().filter(s -> !s.campagnaCompletata()).isPresent();
        continua.setText(ripresa ? "Riprendi lo scontro" : "Continua dal checkpoint");
        continua.setDisable(!ripresa && !disponibile);
        nuova.setDisable(menu.classiDisponibili().isEmpty());
        elimina.setDisable(menu.salvataggio().isEmpty() && !letturaFallita);
        rileggi.setVisible(letturaFallita);
        rileggi.setManaged(letturaFallita);
        errore.setVisible(letturaFallita);
        errore.setManaged(letturaFallita);
        nomeSalvato.setText(menu.salvataggio().map(s -> s.nomeEroe()).orElse("Nessun cammino inciso"));
        dettagliSalvataggio.setText(menu.salvataggio().map(s -> {
            String classe = menu.classiDisponibili().stream()
                    .filter(c -> c.codice().equals(s.codiceClasse())).map(c -> c.nome())
                    .findFirst().orElse(s.codiceClasse());
            return classe + "  /  " + s.codiciCastelliCompletati().size() + " castelli completati\n"
                    + (s.campagnaCompletata() ? "Campagna completata." : "Ripresa dal primo castello incompleto.");
        }).orElse(letturaFallita ? "Il salvataggio non è leggibile." : "La nuova partita occuperà l'unico slot disponibile."));
    }

    @FXML private void nuovaPartita() { coordinatore.mostraCreazione(); }
    @FXML private void continuaPartita() { coordinatore.continua(); }
    @FXML private void eliminaSalvataggio() { coordinatore.elimina(); }
    @FXML private void rileggiSalvataggio() { coordinatore.mostraMenu(); }
    @FXML private void esci() { coordinatore.esci(); }
}
