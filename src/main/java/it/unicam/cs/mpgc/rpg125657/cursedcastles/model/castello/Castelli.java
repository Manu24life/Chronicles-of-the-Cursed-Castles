package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro.TipiMostro;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * I tre castelli previsti dalla configurazione iniziale del gioco.
 */
@UtilityClass
public class Castelli {

    /** Configura il primo castello, popolato da tre scheletri. */
    public static final Castello OSSA = new Castello("ossa", "Castello delle Ossa", List.of(
            new ConfigurazioneNemici(TipiMostro.SCHELETRO, 3)
    ));

    /** Configura il secondo castello, popolato da due orchi e un vampiro. */
    public static final Castello FERRO = new Castello("ferro", "Fortezza di Ferro", List.of(
            new ConfigurazioneNemici(TipiMostro.ORCO, 2),
            new ConfigurazioneNemici(TipiMostro.VAMPIRO, 1)
    ));

    /** Configura l’ultimo castello, presidiato dal drago. */
    public static final Castello MALEDIZIONE = new Castello("maledizione", "Rocca della Maledizione", List.of(
            new ConfigurazioneNemici(TipiMostro.DRAGO, 1)
    ));

    private static final List<Castello> TUTTI = List.of(OSSA, FERRO, MALEDIZIONE);

    /**
     * Restituisce i castelli nell’ordine della campagna.
     *
     * @return lista immutabile dei castelli in ordine di avanzamento
     */
    public static List<Castello> tutti() {
        return TUTTI;
    }

    /**
     * Cerca una configurazione nel catalogo mediante il suo codice stabile.
     *
     * @param codice identificativo stabile usato per riconoscere la configurazione
     * @return configurazione corrispondente, oppure {@link java.util.Optional#empty()} se il codice è sconosciuto
     * @throws NullPointerException se almeno uno tra {@code codice} è {@code null}
     */
    public static Optional<Castello> daCodice(String codice) {
        Objects.requireNonNull(codice, "Il codice non può essere null");
        return TUTTI.stream()
                .filter(castello -> castello.codice().equals(codice))
                .findFirst();
    }
}
