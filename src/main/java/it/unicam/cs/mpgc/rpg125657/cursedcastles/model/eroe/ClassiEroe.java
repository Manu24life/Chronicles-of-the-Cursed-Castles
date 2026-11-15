package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.Statistiche;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.RicaricaATurni;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita.AssorbimentoMagico;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita.TempraDelCavaliere;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Rappresenta il catalogo iniziale; in futuro può essere sostituito da un repository persistente.
 */
@UtilityClass
public class ClassiEroe {

    /** Configura il mago e genera una nuova abilità di assorbimento magico per ogni eroe. */
    public static final ClasseEroe MAGO = new ClasseEroe(
            "mago",
            "Mago",
            new Statistiche(80, 24, 4),
            () -> new AssorbimentoMagico(new RicaricaATurni(3, 0))
    );
    /** Configura il cavaliere e genera una nuova abilità di tempra per ogni eroe. */
    public static final ClasseEroe CAVALIERE = new ClasseEroe(
            "cavaliere",
            "Cavaliere",
            new Statistiche(120, 17, 9),
            () -> new TempraDelCavaliere(new RicaricaATurni(3, 0))
    );

    private static final List<ClasseEroe> TUTTE = List.of(MAGO, CAVALIERE);

    /**
     * Restituisce il catalogo immutabile delle classi dell’eroe.
     *
     * @return catalogo immutabile delle classi
     */
    public static List<ClasseEroe> tutte() {
        return TUTTE;
    }

    /**
     * Cerca una configurazione nel catalogo mediante il suo codice stabile.
     *
     * @param codice identificativo stabile usato per riconoscere la configurazione
     * @return configurazione corrispondente, oppure {@link java.util.Optional#empty()} se il codice è sconosciuto
     * @throws NullPointerException se almeno uno tra {@code codice} è {@code null}
     */
    public static Optional<ClasseEroe> daCodice(String codice) {
        Objects.requireNonNull(codice, "Il codice non può essere null");
        return TUTTE.stream()
                .filter(classe -> classe.codice().equals(codice))
                .findFirst();
    }
}
