package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.Statistiche;
import lombok.experimental.UtilityClass;

/** Tipi di nemico disponibili nella prima configurazione del gioco. */
@UtilityClass
public class TipiMostro {

    public static final TipoMostro SCHELETRO = new TipoMostro("Scheletro", new Statistiche(35, 10, 2));
    public static final TipoMostro ORCO = new TipoMostro("Orco", new Statistiche(65, 15, 5));
    public static final TipoMostro VAMPIRO = new TipoMostro("Vampiro", new Statistiche(80, 18, 7));
    public static final TipoMostro DRAGO = new TipoMostro("Drago", new Statistiche(320, 25, 12));
}
