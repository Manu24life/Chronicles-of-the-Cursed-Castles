package it.unicam.cs.mpgc.rpg125657.cursedcastles.view.grafica;

import com.almasb.fxgl.texture.Texture;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.view.GestoreViste;
import javafx.scene.image.Image;
import javafx.geometry.Rectangle2D;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Immagini condivise, nodi Texture indipendenti; nessun download durante il gioco. */
public final class RisorseBattaglia {
    private static final List<String> NEMICI = List.of("scheletro", "orco", "vampiro", "drago");
    private static final List<String> SCENARI = List.of("ossa", "ferro", "maledizione");
    private static final Map<String, Image> IMMAGINI = new ConcurrentHashMap<>();
    private static final Map<String, Rectangle2D> LIMITI_SPRITE = new ConcurrentHashMap<>();

    private RisorseBattaglia() { }

    /** Chiamato dall'esecutore di avvio, prima di mostrare il menu. */
    public static void precarica() {
        NEMICI.forEach(RisorseBattaglia::limitiSprite);
        SCENARI.forEach(codice -> immagine("scenari/" + codice));
    }

    public static Texture nemico(String tipo) {
        if (!NEMICI.contains(tipo)) throw new IllegalArgumentException("Sprite non disponibile: " + tipo);
        Texture texture = texture("nemici/" + tipo);
        texture.setViewport(limitiSprite(tipo));
        return texture;
    }

    public static Texture scenario(String codice) {
        // Anche i castelli delle campagne configurabili restano rappresentabili.
        return texture("scenari/" + (SCENARI.contains(codice) ? codice : "ossa"));
    }

    private static Texture texture(String chiave) {
        Texture texture = new Texture(immagine(chiave));
        texture.setSmooth(false);
        texture.setMouseTransparent(true);
        return texture;
    }

    /** Esclude i margini trasparenti dal layout senza alterare il PNG originale. */
    private static Rectangle2D limitiSprite(String tipo) {
        return LIMITI_SPRITE.computeIfAbsent(tipo, codice -> {
            Image immagine = immagine("nemici/" + codice);
            int w = (int) immagine.getWidth(), h = (int) immagine.getHeight();
            int minX = w, minY = h, maxX = -1, maxY = -1;
            var lettore = immagine.getPixelReader();
            for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
                if ((lettore.getArgb(x, y) >>> 24) > 8) {
                    minX = Math.min(minX, x); minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x); maxY = Math.max(maxY, y);
                }
            }
            if (maxX < 0) throw new IllegalStateException("Sprite completamente trasparente: " + codice);
            return new Rectangle2D(minX, minY, maxX - minX + 1, maxY - minY + 1);
        });
    }

    private static Image immagine(String chiave) {
        return IMMAGINI.computeIfAbsent(chiave, nome -> {
            double larghezza = nome.startsWith("nemici/") ? 640 : 1920;
            Image immagine = new Image(GestoreViste.risorsa("grafica/" + nome + ".png").toExternalForm(),
                    larghezza, 0, true, false, false);
            if (immagine.isError()) throw new IllegalStateException("Immagine non leggibile: " + nome,
                    immagine.getException());
            return immagine;
        });
    }
}
