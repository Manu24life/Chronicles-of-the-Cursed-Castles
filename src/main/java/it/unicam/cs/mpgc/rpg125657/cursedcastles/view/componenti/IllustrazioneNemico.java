package it.unicam.cs.mpgc.rpg125657.cursedcastles.view.componenti;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.AnimationBuilder;
import com.almasb.fxgl.texture.Texture;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.view.grafica.RisorseBattaglia;
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Sprite 2D con texture e feedback FXGL, indipendente dalle regole di combattimento.
 */
public final class IllustrazioneNemico extends Region {
    private final Texture sprite;
    private Animation<?> animazione;
    private long ultimoFrame;
    private final AnimationTimer aggiornamenti = new AnimationTimer() {
        @Override public void handle(long adesso) {
            if (!visibileInFinestra()) {
                terminaAnimazione();
                return;
            }
            double secondi = ultimoFrame == 0 ? 0 : Math.min(.05, (adesso - ultimoFrame) / 1_000_000_000.0);
            ultimoFrame = adesso;
            animazione.onUpdate(secondi);
            if (!animazione.isAnimating()) terminaAnimazione();
        }
    };

    /**
     * Inizializza un’istanza di {@code IllustrazioneNemico} con le dipendenze ricevute.
     *
     * @param tipo codice dello sprite del nemico
     */
    public IllustrazioneNemico(String tipo) {
        sprite = RisorseBattaglia.nemico(tipo);
        sprite.setPreserveRatio(true);
        sprite.setEffect(new DropShadow(12, 0, 4, Color.color(0, 0, 0, .8)));
        setMinSize(60, 70);
        setPrefSize("drago".equals(tipo) ? 440 : 230, 270);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setMouseTransparent(true);
        getChildren().add(sprite);
        sceneProperty().addListener((o, prima, dopo) -> {
            if (dopo == null) terminaAnimazione();
        });
    }

    @Override protected void layoutChildren() {
        double spazioW = Math.max(1, getWidth() - 10);
        double spazioH = Math.max(1, getHeight() - 10);
        sprite.setFitWidth(spazioW);
        sprite.setFitHeight(spazioH);
        double iw = sprite.getViewport().getWidth(), ih = sprite.getViewport().getHeight();
        double scala = Math.min(spazioW / iw, spazioH / ih);
        sprite.relocate((getWidth() - iw * scala) / 2, getHeight() - ih * scala - 5);
    }

    /**
     * Riproduce il feedback visivo del danno e, se necessario, della sconfitta.
     *
     * @param sconfitto indicatore di sconfitta del nemico
     */
    public void riceviDanno(boolean sconfitto) {
        terminaAnimazione();
        if (!visibileInFinestra()) {
            sprite.setOpacity(sconfitto ? .35 : 1);
            return;
        }
        animazione = sconfitto
                ? new AnimationBuilder().duration(Duration.millis(380))
                    .fade(sprite).from(1).to(.35).build()
                : new AnimationBuilder().duration(Duration.millis(55)).repeat(4).autoReverse(true)
                    .interpolator(Interpolator.EASE_OUT)
                    .animate(sprite.translateXProperty()).from(0.0).to(6.0).build();
        ultimoFrame = 0;
        animazione.start();
        aggiornamenti.start();
    }

    private boolean visibileInFinestra() {
        return getScene() != null && getScene().getWindow() != null && getScene().getWindow().isShowing();
    }

    private void terminaAnimazione() {
        aggiornamenti.stop();
        if (animazione != null) animazione.stop();
        sprite.setTranslateX(0);
        ultimoFrame = 0;
    }
}
