package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.Statistiche;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.RicaricaATurni;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita.AssorbimentoMagico;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita.TempraDelCavaliere;
import lombok.experimental.UtilityClass;

/** Catalogo iniziale; in futuro può essere sostituito da un repository persistente. */
@UtilityClass
public class ClassiEroe {

    public static final ClasseEroe MAGO = new ClasseEroe(
            "mago",
            "Mago",
            new Statistiche(80, 24, 4),
            () -> new AssorbimentoMagico(new RicaricaATurni(3, 0))
    );
    public static final ClasseEroe CAVALIERE = new ClasseEroe(
            "cavaliere",
            "Cavaliere",
            new Statistiche(120, 17, 9),
            () -> new TempraDelCavaliere(new RicaricaATurni(3, 0))
    );
}
