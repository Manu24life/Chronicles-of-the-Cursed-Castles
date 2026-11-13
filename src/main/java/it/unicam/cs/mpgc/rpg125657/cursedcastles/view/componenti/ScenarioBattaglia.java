package it.unicam.cs.mpgc.rpg125657.cursedcastles.view.componenti;

import com.almasb.fxgl.texture.Texture;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.view.grafica.RisorseBattaglia;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

/** Panorama FXGL con ritaglio proporzionale; conserva il pavimento nella finestra minima. */
public final class ScenarioBattaglia extends Region {
    private Texture panorama;
    private final Region vignetta = new Region();
    private String codice = "";

    public ScenarioBattaglia() {
        setMinSize(0, 0);
        setMouseTransparent(true);
        vignetta.getStyleClass().add("battle-vignette");
        getChildren().add(vignetta);
        Rectangle ritaglio = new Rectangle();
        ritaglio.widthProperty().bind(widthProperty());
        ritaglio.heightProperty().bind(heightProperty());
        setClip(ritaglio);
    }

    public void mostra(String codice) {
        if (this.codice.equals(codice)) return;
        Texture nuovo = RisorseBattaglia.scenario(codice);
        this.codice = codice;
        panorama = nuovo;
        getChildren().setAll(panorama, vignetta);
        requestLayout();
    }

    @Override protected void layoutChildren() {
        double w = getWidth(), h = getHeight();
        if (panorama != null && w > 0 && h > 0) {
            double iw = panorama.getImage().getWidth(), ih = panorama.getImage().getHeight();
            double scala = Math.max(w / iw, h / ih);
            double vw = w / scala, vh = h / scala;
            panorama.setViewport(new Rectangle2D((iw - vw) / 2, (ih - vh) * .68, vw, vh));
            panorama.setFitWidth(w);
            panorama.setFitHeight(h);
        }
        vignetta.resizeRelocate(0, 0, w, h);
    }
}
