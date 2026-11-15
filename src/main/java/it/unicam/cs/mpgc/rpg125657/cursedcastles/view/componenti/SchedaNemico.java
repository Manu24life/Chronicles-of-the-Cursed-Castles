package it.unicam.cs.mpgc.rpg125657.cursedcastles.view.componenti;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.StatoNemico;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Il bersaglio conserva l'indice dello snapshot, anche dopo la sconfitta di altri nemici.
 */
public final class SchedaNemico extends Button {
    private static final PseudoClass SELEZIONATO = PseudoClass.getPseudoClass("selected");
    private final int indice;
    private final BarraVita vita = new BarraVita();
    private final Label nome = new Label();
    private final Label stato = new Label();
    private final IllustrazioneNemico figura;
    private int puntiVitaPrecedenti = -1;

    /**
     * Inizializza un’istanza di {@code SchedaNemico} con le dipendenze ricevute.
     *
     * @param nemico nemico o snapshot del nemico da rappresentare
     * @param seleziona azione invocata alla selezione della scheda
     */
    public SchedaNemico(StatoNemico nemico, Runnable seleziona) {
        indice = nemico.indice();
        getStyleClass().add("enemy-card");
        setId("nemico-" + indice);
        setMinWidth(120);
        setMaxWidth("drago".equals(nemico.codiceTipo()) ? 520 : 280);
        setMaxHeight(Double.MAX_VALUE);
        nome.getStyleClass().add("enemy-name");
        nome.setWrapText(true);
        stato.getStyleClass().add("eyebrow");
        figura = new IllustrazioneNemico(nemico.codiceTipo());
        VBox.setVgrow(figura, Priority.ALWAYS);
        VBox didascalia = new VBox(6, nome, vita, stato);
        didascalia.getStyleClass().add("enemy-caption");
        didascalia.setAlignment(Pos.CENTER);
        VBox contenuto = new VBox(5, figura, didascalia);
        contenuto.setAlignment(Pos.BOTTOM_CENTER);
        contenuto.setFillWidth(true);
        setGraphic(contenuto);
        setOnAction(e -> seleziona.run());
        aggiorna(nemico, false);
    }

    /**
     * Restituisce l’indice originale del nemico rappresentato.
     *
     * @return indice originale del nemico, a partire da zero
     */
    public int indice() { return indice; }

    /**
     * Aggiorna nome, vita e selezione; riproduce il danno e disabilita la scheda quando il nemico è sconfitto.
     *
     * @param nemico nemico o snapshot del nemico da rappresentare
     * @param selezionato indicatore di selezione del bersaglio
     */
    public void aggiorna(StatoNemico nemico, boolean selezionato) {
        if (puntiVitaPrecedenti > nemico.puntiVita()) figura.riceviDanno(!nemico.vivo());
        puntiVitaPrecedenti = nemico.puntiVita();
        nome.setText(nemico.nome());
        vita.aggiorna(nemico.puntiVita(), nemico.puntiVitaMassimi());
        setDisable(!nemico.vivo());
        pseudoClassStateChanged(SELEZIONATO, selezionato && nemico.vivo());
        stato.setText(!nemico.vivo() ? "SCONFITTO" : selezionato ? "◆ BERSAGLIO" : "SELEZIONA");
        setAccessibleText(nemico.nome() + ", " + nemico.puntiVita() + " punti vita" +
                (selezionato ? ", bersaglio selezionato" : ""));
    }
}
