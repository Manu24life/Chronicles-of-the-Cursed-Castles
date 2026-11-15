package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.Statistiche;
import lombok.experimental.UtilityClass;

/**
 * Rappresenta i tipi di nemico disponibili nella prima configurazione del gioco.
 */
@UtilityClass
public class TipiMostro {

    /** Definisce le statistiche base dello scheletro. */
    public static final TipoMostro SCHELETRO = new TipoMostro(
            "scheletro", "Scheletro", new Statistiche(35, 14, 2)
    );
    /** Definisce le statistiche base dell’orco. */
    public static final TipoMostro ORCO = new TipoMostro(
            "orco", "Orco", new Statistiche(65, 20, 5)
    );
    /** Definisce le statistiche base del vampiro. */
    public static final TipoMostro VAMPIRO = new TipoMostro(
            "vampiro", "Vampiro", new Statistiche(80, 26, 7)
    );
    /** Definisce le statistiche base del drago. */
    public static final TipoMostro DRAGO = new TipoMostro(
            "drago", "Drago", new Statistiche(320, 30, 12)
    );
}
