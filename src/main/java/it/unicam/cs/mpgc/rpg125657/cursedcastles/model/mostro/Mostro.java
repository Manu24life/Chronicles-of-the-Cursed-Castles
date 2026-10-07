package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.Personaggio;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Objects;

/** Nemico concreto; il tipo ne definisce le statistiche fisse. */
@Getter
@Accessors(fluent = true)
public final class Mostro extends Personaggio implements Nemico {

    private final TipoMostro tipo;

    public Mostro(String nome, TipoMostro tipo) {
        super(nome, Objects.requireNonNull(tipo, "Il tipo non può essere null").statistiche());
        this.tipo = tipo;
    }
}
