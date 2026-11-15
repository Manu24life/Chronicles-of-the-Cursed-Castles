package it.unicam.cs.mpgc.rpg125657.cursedcastles.view.componenti;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

import java.util.Random;

/**
 * Incisione originale, deterministica: viene ridisegnata soltanto al cambio di scena o dimensione.
 */
public final class ScenarioCastello extends Region {
    private final Canvas tela = new Canvas();
    private String codice = "menu";

    /**
     * Inizializza un’istanza di {@code ScenarioCastello} con le dipendenze ricevute.
     */
    public ScenarioCastello() {
        getChildren().add(tela);
        setMouseTransparent(true);
        setMinSize(0, 0);
    }

    /**
     * Seleziona il castello da disegnare e richiede l’aggiornamento dello scenario.
     *
     * @param codice identificativo stabile usato per riconoscere la configurazione
     */
    public void mostra(String codice) {
        this.codice = codice;
        disegna();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void layoutChildren() {
        tela.setWidth(getWidth());
        tela.setHeight(getHeight());
        disegna();
    }

    private void disegna() {
        if (tela.getWidth() <= 0 || tela.getHeight() <= 0) return;
        GraphicsContext g = tela.getGraphicsContext2D();
        g.setTransform(1, 0, 0, 1, 0, 0);
        g.scale(tela.getWidth() / 1280, tela.getHeight() / 600);
        g.setFill(Color.web("#171714"));
        g.fillRect(0, 0, 1280, 600);
        Random segni = new Random(9813L + codice.hashCode());
        g.setStroke(Color.web("#b9ab8c", .045));
        g.setLineWidth(.7);
        for (int i = 0; i < 1700; i++) {
            double x = segni.nextDouble() * 1280, y = segni.nextDouble() * 600;
            g.strokeLine(x, y, x + segni.nextDouble() * 38, y - segni.nextDouble() * 12);
        }
        if ("menu".equals(codice)) paesaggio(g);
        else interno(g);
        g.setFill(new RadialGradient(0, 0, .5, .4, .72, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT), new Stop(1, Color.web("#080908", .9))));
        g.fillRect(0, 0, 1280, 600);
        g.setStroke(Color.web("#c6b58f", .012));
        for (int y = 0; y < 600; y += 4) g.strokeLine(0, y, 1280, y);
    }

    private void paesaggio(GraphicsContext g) {
        g.setFill(Color.web("#b3aa8f", .12));
        g.fillOval(798, 42, 176, 176);
        g.setStroke(Color.web("#c9b998", .1));
        for (int i = 0; i < 14; i++) g.strokeLine(0, 130 + i * 19, 1280, 100 + i * 22);
        g.setFill(Color.web("#0b0c0b"));
        g.fillPolygon(new double[]{0, 120, 230, 400, 550, 720, 880, 1050, 1280, 1280, 0},
                new double[]{490, 435, 475, 395, 445, 395, 410, 350, 410, 600, 600}, 11);
        tour(g, 730, 225, 84, 240);
        tour(g, 915, 175, 94, 290);
        tour(g, 1060, 280, 68, 190);
        g.setFill(Color.web("#101210"));
        g.fillRect(790, 335, 310, 160);
        g.setStroke(Color.web("#9a8d72", .24));
        for (int y = 345; y < 500; y += 14) {
            g.strokeLine(790, y, 1100, y);
            for (int x = 790 + y % 28; x < 1100; x += 32) g.strokeLine(x, y, x, y + 14);
        }
        g.setFill(Color.web("#060807"));
        g.fillRoundRect(919, 388, 65, 130, 65, 90);
        g.setStroke(Color.web("#b6a583", .14));
        g.strokeRoundRect(912, 378, 79, 150, 79, 90);
        // Alberi spogli e croci lungo il sentiero, senza asset esterni.
        for (int i = 0; i < 6; i++) {
            double x = 80 + i * 197, y = 530 + (i % 2) * 22;
            g.setStroke(Color.web("#444335", .55));
            g.setLineWidth(2);
            g.strokeLine(x, y, x - 8, y - 92);
            g.strokeLine(x - 4, y - 48, x - 31, y - 76);
            g.strokeLine(x - 5, y - 63, x + 19, y - 88);
        }
    }

    private void tour(GraphicsContext g, double x, double y, double w, double h) {
        g.setFill(Color.web("#0e100e"));
        g.fillRect(x, y, w, h);
        g.fillPolygon(new double[]{x - 11, x + w / 2, x + w + 11},
                new double[]{y, y - 96, y}, 3);
        g.setStroke(Color.web("#a99a7b", .3));
        g.setLineWidth(1);
        g.strokeLine(x + w / 2, y - 96, x + w + 11, y);
        for (double yy = y + 12; yy < y + h; yy += 13) g.strokeLine(x + 4, yy, x + w - 4, yy - 2);
        g.setFill(Color.web("#b8a37d", .22));
        for (int i = 0; i < 3; i++) g.fillRoundRect(x + w / 2 - 5, y + 26 + i * 52, 10, 24, 10, 10);
    }

    private void interno(GraphicsContext g) {
        g.setFill(Color.web("#aaa080", .035));
        g.fillPolygon(new double[]{470, 810, 1180, 100}, new double[]{0, 0, 600, 600}, 4);
        g.setStroke(Color.web("#8c8168", .2));
        g.setLineWidth(1);
        for (int y = 44; y < 410; y += 37) {
            g.strokeLine(0, y, 1280, y);
            for (int x = (y / 37 % 2) * 65; x < 1280; x += 130) g.strokeLine(x, y, x, y + 37);
        }
        for (int i = 0; i < 5; i++) {
            double x = i * 320 - 80;
            g.setFill(Color.web("#0a0c0b", .8));
            g.fillRoundRect(x, 28, 206, 440, 206, 270);
            g.setStroke(Color.web("#ab9b7b", .18));
            g.setLineWidth(3);
            g.strokeRoundRect(x - 10, 16, 226, 455, 226, 290);
            g.setLineWidth(1);
            g.strokeRoundRect(x - 21, 4, 248, 480, 248, 310);
        }
        if ("ferro".equals(codice)) {
            g.setStroke(Color.web("#9b9280", .28));
            for (int x = 485; x <= 805; x += 24) g.strokeLine(x, 10, x, 390);
            for (int y = 50; y <= 390; y += 55) g.strokeLine(485, y, 805, y);
        } else if ("maledizione".equals(codice)) {
            g.setStroke(Color.web("#78413b", .35));
            g.setLineWidth(2);
            g.strokeOval(453, 80, 374, 300);
            g.strokeOval(468, 93, 344, 274);
            g.strokePolygon(new double[]{640, 800, 480}, new double[]{105, 335, 335}, 3);
        }
        g.setFill(Color.web("#10110f"));
        g.fillRect(0, 437, 1280, 163);
        g.setStroke(Color.web("#9a8b70", .16));
        g.setLineWidth(1);
        for (int x = -900; x < 2400; x += 170) g.strokeLine(640, 360, x, 600);
        for (int y = 451; y < 600; y += (y - 410) / 2) g.strokeLine(0, y, 1280, y);
        if ("ossa".equals(codice)) {
            g.setStroke(Color.web("#b4a78b", .25));
            for (int i = 0; i < 18; i++) {
                double x = 30 + i * 73, y = 475 + i % 3 * 17;
                g.strokeLine(x, y, x + 34, y + 13);
                g.strokeOval(x - 3, y - 2, 7, 7);
                g.strokeOval(x + 30, y + 10, 7, 7);
            }
        }
    }
}
