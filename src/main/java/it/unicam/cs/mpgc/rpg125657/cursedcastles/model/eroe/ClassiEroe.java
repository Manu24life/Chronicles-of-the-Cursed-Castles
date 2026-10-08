package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.Statistiche;
import lombok.experimental.UtilityClass;

/** Catalogo iniziale; in futuro può essere sostituito da un repository persistente. */
@UtilityClass
public class ClassiEroe {

    public static final ClasseEroe MAGO = new ClasseEroe("Mago", new Statistiche(80, 24, 4));
    public static final ClasseEroe CAVALIERE = new ClasseEroe("Cavaliere", new Statistiche(120, 17, 9));
}
