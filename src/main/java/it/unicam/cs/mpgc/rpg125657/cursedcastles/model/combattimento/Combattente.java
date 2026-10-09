package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.Statistiche;

/** Contratto minimo richiesto dalle regole di combattimento. */
public interface Combattente {

    String nome();

    Statistiche statisticheBase();

    /** Attacco corrente, comprensivo di eventuali bonus o penalità. */
    default int attacco() {
        return statisticheBase().attacco();
    }

    /** Difesa corrente, comprensiva di eventuali bonus o penalità. */
    default int difesa() {
        return statisticheBase().difesa();
    }

    int puntiVita();

    void subisciDanno(int danno);

    void recuperaPuntiVita(int puntiVita);

    default boolean isVivo() {
        return puntiVita() > 0;
    }
}
