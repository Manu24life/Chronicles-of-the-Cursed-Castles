package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento;

/** Comportamenti comuni a qualsiasi partecipante a un combattimento. */
public interface Combattimento {



    RisultatoAzione attacca(Combattimento bersaglio);

    void subisciDanno(int danno);

    void recuperaPuntiVita(int puntiVita);

    boolean isVivo();

}
