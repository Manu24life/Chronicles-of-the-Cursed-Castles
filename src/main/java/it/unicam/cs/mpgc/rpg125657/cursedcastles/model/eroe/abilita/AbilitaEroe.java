package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.Combattente;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.RisultatoAzione;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.Eroe;

/** Strategia per un'abilità posseduta da un eroe. */
public interface AbilitaEroe {

    String nome();

    RisultatoAzione usa(Eroe utilizzatore, Combattente bersaglio);

    boolean isDisponibile();

    int turniRimanenti();

    void avanzaTurno();

    /** Ripristina lo stato dell'abilità all'inizio di un nuovo castello. */
    void reset();

    /** Indica se l'abilità richiede la scelta di un nemico. */
    default boolean richiedeBersaglio() {
        return true;
    }
}
