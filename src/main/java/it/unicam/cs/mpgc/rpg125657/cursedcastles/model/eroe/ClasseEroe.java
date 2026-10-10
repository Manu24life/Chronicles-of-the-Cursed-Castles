package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.Statistiche;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita.AbilitaEroe;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita.FabbricaAbilitaEroe;

import java.util.Objects;

/** Archetipo immutabile: due eroi della stessa classe condividono questi valori. */
public record ClasseEroe(
        String codice,
        String nome,
        Statistiche statistiche,
        FabbricaAbilitaEroe fabbricaAbilita
) {

    /** Costruttore mantenuto per archetipi creati dinamicamente con abilità esplicita. */
    public ClasseEroe(String nome, Statistiche statistiche) {
        this(codiceDaNome(nome), nome, statistiche, null);
    }

    public ClasseEroe(
            String nome,
            Statistiche statistiche,
            FabbricaAbilitaEroe fabbricaAbilita
    ) {
        this(codiceDaNome(nome), nome, statistiche, fabbricaAbilita);
    }

    public ClasseEroe {
        Objects.requireNonNull(codice, "Il codice della classe non può essere null");
        Objects.requireNonNull(nome, "Il nome della classe non può essere null");
        Objects.requireNonNull(statistiche, "Le statistiche non possono essere null");
        if (codice.isBlank() || !codice.matches("[a-z0-9]+(?:-[a-z0-9]+)*")) {
            throw new IllegalArgumentException("Il codice deve usare lettere minuscole, numeri e trattini");
        }
        if (nome.isBlank()) {
            throw new IllegalArgumentException("Il nome della classe non può essere vuoto");
        }
        nome = nome.strip();
    }

    public AbilitaEroe creaAbilita() {
        if (fabbricaAbilita == null) {
            throw new IllegalStateException("La classe non definisce un'abilità predefinita");
        }
        return Objects.requireNonNull(
                fabbricaAbilita.crea(),
                "La fabbrica non può restituire un'abilità null"
        );
    }

    public boolean haAbilitaPredefinita() {
        return fabbricaAbilita != null;
    }

    public boolean isAbilitaCompatibile(AbilitaEroe abilita) {
        Objects.requireNonNull(abilita, "L'abilità non può essere null");
        return !haAbilitaPredefinita() || creaAbilita().getClass().equals(abilita.getClass());
    }

    private static String codiceDaNome(String nome) {
        Objects.requireNonNull(nome, "Il nome della classe non può essere null");
        return nome.strip().toLowerCase(java.util.Locale.ROOT).replaceAll("[^a-z0-9]+", "-");
    }
}
