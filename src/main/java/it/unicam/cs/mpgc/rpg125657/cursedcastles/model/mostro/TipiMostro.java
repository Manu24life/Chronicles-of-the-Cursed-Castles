package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.Statistiche;
import lombok.experimental.UtilityClass;

/** Tipi di nemico disponibili nella prima configurazione del gioco. */
@UtilityClass
public class TipiMostro {

    public static final TipoMostro SCHELETRO = new TipoMostro(
            "scheletro", "Scheletro", new Statistiche(35, 10, 2)
    );
    public static final TipoMostro ORCO = new TipoMostro(
            "orco", "Orco", new Statistiche(65, 15, 5)
    );
    public static final TipoMostro VAMPIRO = new TipoMostro(
            "vampiro", "Vampiro", new Statistiche(80, 18, 7)
    );
    public static final TipoMostro DRAGO = new TipoMostro(
            "drago", "Drago", new Statistiche(320, 25, 12)
    );
}
