package it.unicam.cs.mpgc.rpg125657.cursedcastles.view.schermate;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.controller.StatoCastello;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.controller.VistaPartita;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.StatoBattaglia;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.StatoNemico;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.partita.StatoPartita;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.view.CoordinatoreInterfaccia;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.view.componenti.BarraVita;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.view.componenti.ScenarioBattaglia;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.view.componenti.SchedaNemico;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public final class SchermataBattaglia {
    private final CoordinatoreInterfaccia coordinatore;
    private final ObservableList<String> registro;
    private VistaPartita partita;
    private int selezionato = -1;
    private String codiceCastello = "";
    @FXML private BorderPane radiceBattaglia;
    @FXML private ScenarioBattaglia scenario;
    @FXML private Label castello;
    @FXML private Label progresso;
    @FXML private Label turno;
    @FXML private Label nomeEroe;
    @FXML private Label classeEroe;
    @FXML private Label attacco;
    @FXML private Label difesa;
    @FXML private Label bonus;
    @FXML private Label bersaglio;
    @FXML private BarraVita vitaEroe;
    @FXML private HBox nemici;
    @FXML private Button attacca;
    @FXML private Button abilita;
    @FXML private Button cura;
    @FXML private Button alMenu;
    @FXML private Button avanti;
    @FXML private Button tornaAlMenu;
    @FXML private VBox esito;
    @FXML private Label titoloEsito;
    @FXML private Label dettaglioEsito;
    @FXML private ListView<String> eventi;

    public SchermataBattaglia(CoordinatoreInterfaccia coordinatore, ObservableList<String> registro) {
        this.coordinatore = coordinatore;
        this.registro = registro;
    }

    @FXML private void initialize() {
        eventi.setItems(registro);
        eventi.setCellFactory(lista -> new ListCell<>() {
            private final Label testo = new Label();
            { testo.setWrapText(true); testo.maxWidthProperty().bind(lista.widthProperty().subtract(34)); }
            @Override protected void updateItem(String voce, boolean vuota) {
                super.updateItem(voce, vuota);
                testo.setText(voce);
                setGraphic(vuota ? null : testo);
            }
        });
        radiceBattaglia.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            switch (e.getCode()) {
                case A -> { if (!attacca.isDisabled()) attacca.fire(); }
                case S -> { if (!abilita.isDisabled()) abilita.fire(); }
                case C -> { if (!cura.isDisabled()) cura.fire(); }
                case DIGIT1, NUMPAD1 -> scegliPosizione(0);
                case DIGIT2, NUMPAD2 -> scegliPosizione(1);
                case DIGIT3, NUMPAD3 -> scegliPosizione(2);
                default -> { return; }
            }
            e.consume();
        });
    }

    public void mostra(VistaPartita partita) {
        this.partita = partita;
        nomeEroe.setText(partita.eroe().nome());
        classeEroe.setText(partita.eroe().nomeClasse().toUpperCase(java.util.Locale.ITALIAN));
        vitaEroe.aggiorna(partita.eroe().puntiVita(), partita.eroe().puntiVitaMassimi());
        attacco.setText(String.valueOf(partita.eroe().attacco()));
        difesa.setText(String.valueOf(partita.eroe().difesa()));
        bonus.setText("Bonus: +" + partita.eroe().bonusAttaccoPercentuale() + "% ATT / +"
                + partita.eroe().bonusDifesaPercentuale() + "% DIF");
        if (partita.castello().isEmpty()) {
            nemici.getChildren().clear();
            castello.setText("Il cammino è compiuto");
            progresso.setText("CAMPAGNA COMPLETATA");
            turno.setText("Ogni castello è stato superato.");
            bersaglio.setText("Nessun bersaglio");
            disabilitaAzioni();
            mostraEsito("Oltre la maledizione", "La campagna è completata e la progressione è salvata.", null);
            alMenu.setDisable(false);
            return;
        }
        StatoCastello stato = partita.castello().orElseThrow();
        castello.setText(stato.nome());
        progresso.setText("CASTELLO " + stato.numero() + " / " + stato.totale());
        if (!codiceCastello.equals(stato.codice())) {
            codiceCastello = stato.codice();
            scenario.mostra(codiceCastello);
            nemici.getChildren().clear();
            selezionato = -1;
            for (StatoNemico nemico : stato.nemici()) {
                SchedaNemico scheda = new SchedaNemico(nemico, () -> seleziona(nemico.indice()));
                HBox.setHgrow(scheda, Priority.ALWAYS);
                nemici.getChildren().add(scheda);
            }
        }
        if (stato.nemici().stream().noneMatch(n -> n.indice() == selezionato && n.vivo())) {
            selezionato = stato.nemici().stream().filter(StatoNemico::vivo)
                    .mapToInt(StatoNemico::indice).findFirst().orElse(-1);
        }
        for (var nodo : nemici.getChildren()) {
            SchedaNemico scheda = (SchedaNemico) nodo;
            stato.nemici().stream().filter(n -> n.indice() == scheda.indice()).findFirst()
                    .ifPresent(n -> scheda.aggiorna(n, n.indice() == selezionato));
        }
        boolean turnoEroe = stato.statoBattaglia() == StatoBattaglia.TURNO_EROE;
        attacca.setDisable(!turnoEroe || selezionato < 0);
        abilita.setText(stato.nomeAbilita() + (stato.abilitaDisponibile() ? "  [S]"
                : " · " + stato.turniRicaricaRimanenti() + " turni"));
        abilita.setDisable(!turnoEroe || !stato.abilitaDisponibile()
                || (stato.abilitaRichiedeBersaglio() && selezionato < 0));
        cura.setText("Cura · " + stato.cureRimanenti() + " rimaste  [C]");
        cura.setDisable(!turnoEroe || stato.cureRimanenti() == 0
                || partita.eroe().puntiVita() == partita.eroe().puntiVitaMassimi());
        cura.setTooltip(new Tooltip("La cura consuma un'azione. Gli utilizzi sono limitati al castello corrente."));
        bersaglio.setText(stato.nemici().stream().filter(n -> n.indice() == selezionato)
                .map(n -> "Bersaglio: " + n.nome()).findFirst().orElse("Nessun bersaglio vivo"));
        alMenu.setDisable(stato.statoBattaglia() == StatoBattaglia.TURNO_NEMICI);
        turno.setText(switch (stato.statoBattaglia()) {
            case TURNO_EROE -> "Il tuo turno · scegli come agire";
            case TURNO_NEMICI -> "Il nemico si prepara a rispondere…";
            case VITTORIA -> "Non restano avversari in questo castello.";
            case SCONFITTA -> "Il tuo cammino si interrompe qui.";
        });
        esito.setVisible(!turnoEroe && stato.statoBattaglia() != StatoBattaglia.TURNO_NEMICI);
        esito.setManaged(esito.isVisible());
        if (stato.statoBattaglia() == StatoBattaglia.VITTORIA) {
            mostraEsito("Castello superato", stato.vittoriaSalvata()
                            ? "La vittoria è incisa nel salvataggio."
                            : "Vittoria ottenuta, salvataggio non riuscito. Riprova prima di proseguire.",
                    !stato.vittoriaSalvata() ? "Riprova salvataggio" : stato.numero() == stato.totale()
                            ? "Concludi il cammino  ›" : "Al prossimo castello  ›");
        } else if (partita.statoPartita() == StatoPartita.SCONFITTA) {
            mostraEsito("Il silenzio ritorna", "I castelli già completati restano salvati. Puoi riprovare dal menu.", null);
        }
    }

    private void mostraEsito(String titolo, String descrizione, String azione) {
        esito.setVisible(true);
        esito.setManaged(true);
        titoloEsito.setText(titolo);
        dettaglioEsito.setText(descrizione);
        avanti.setText(azione);
        avanti.setVisible(azione != null);
        avanti.setManaged(azione != null);
        tornaAlMenu.setVisible(azione == null);
        tornaAlMenu.setManaged(azione == null);
    }

    private void disabilitaAzioni() {
        attacca.setDisable(true);
        abilita.setDisable(true);
        cura.setDisable(true);
    }

    private void seleziona(int indice) {
        if (partita.castello().filter(c -> c.statoBattaglia() == StatoBattaglia.TURNO_EROE).isEmpty()) return;
        selezionato = indice;
        mostra(partita);
    }

    private void scegliPosizione(int posizione) {
        if (posizione < nemici.getChildren().size()) {
            SchedaNemico scheda = (SchedaNemico) nemici.getChildren().get(posizione);
            if (!scheda.isDisabled()) scheda.fire();
        }
    }

    @FXML private void attacca() { coordinatore.attacca(selezionato); }
    @FXML private void usaAbilita() { coordinatore.abilita(selezionato); }
    @FXML private void cura() { coordinatore.cura(); }
    @FXML private void alMenu() { coordinatore.mostraMenu(); }
    @FXML private void prosegui() { coordinatore.prosegui(); }
}
