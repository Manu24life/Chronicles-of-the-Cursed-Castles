package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.Combattente;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.Ricarica;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.RisultatoAzione;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.Eroe;

import java.util.Objects;

/** Template che delega il conteggio dei turni a una politica di ricarica. */
public abstract class AbilitaConRicarica implements AbilitaEroe {

    private final String nome;
    private final Ricarica ricarica;

    protected AbilitaConRicarica(String nome, Ricarica ricarica) {
        Objects.requireNonNull(nome, "Il nome non può essere null");
        if (nome.isBlank()) {
            throw new IllegalArgumentException("Il nome non può essere vuoto");
        }
        this.nome = nome.strip();
        this.ricarica = Objects.requireNonNull(ricarica, "La ricarica non può essere null");
    }

    @Override
    public final RisultatoAzione usa(Eroe utilizzatore, Combattente bersaglio) {
        Objects.requireNonNull(utilizzatore, "L'utilizzatore non può essere null");
        Objects.requireNonNull(bersaglio, "Il bersaglio non può essere null");

        if (!utilizzatore.isVivo()) {
            return fallimento("Un eroe sconfitto non può usare abilità");
        }
        if (!ricarica.isDisponibile()) {
            return fallimento("Abilità disponibile tra " + ricarica.turniRimanenti() + " turni");
        }

        RisultatoAzione risultato = applica(utilizzatore, bersaglio);
        if (risultato.riuscita()) {
            ricarica.utilizza();
        }
        return risultato;
    }

    protected abstract RisultatoAzione applica(Eroe utilizzatore, Combattente bersaglio);

    @Override
    public final String nome() {
        return nome;
    }

    @Override
    public final boolean isDisponibile() {
        return ricarica.isDisponibile();
    }

    @Override
    public final int turniRimanenti() {
        return ricarica.turniRimanenti();
    }

    @Override
    public final void avanzaTurno() {
        ricarica.avanzaTurno();
    }

    @Override
    public final void reset() {
        ricarica.reset();
    }

    private RisultatoAzione fallimento(String descrizione) {
        return new RisultatoAzione(false, 0, 0, descrizione);
    }
}
