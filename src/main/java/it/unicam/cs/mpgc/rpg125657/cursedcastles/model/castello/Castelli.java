package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro.TipiMostro;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** I tre castelli previsti dalla configurazione iniziale del gioco. */
@UtilityClass
public class Castelli {

    public static final Castello OSSA = new Castello("ossa", "Castello delle Ossa", List.of(
            new ConfigurazioneNemici(TipiMostro.SCHELETRO, 3)
    ));

    public static final Castello FERRO = new Castello("ferro", "Fortezza di Ferro", List.of(
            new ConfigurazioneNemici(TipiMostro.ORCO, 2),
            new ConfigurazioneNemici(TipiMostro.VAMPIRO, 1)
    ));

    public static final Castello MALEDIZIONE = new Castello("maledizione", "Rocca della Maledizione", List.of(
            new ConfigurazioneNemici(TipiMostro.DRAGO, 1)
    ));

    private static final List<Castello> TUTTI = List.of(OSSA, FERRO, MALEDIZIONE);

    public static List<Castello> tutti() {
        return TUTTI;
    }

    public static Optional<Castello> daCodice(String codice) {
        Objects.requireNonNull(codice, "Il codice non può essere null");
        return TUTTI.stream()
                .filter(castello -> castello.codice().equals(codice))
                .findFirst();
    }
}
