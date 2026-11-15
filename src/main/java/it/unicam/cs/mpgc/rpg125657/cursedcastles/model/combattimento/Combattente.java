package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.Statistiche;

/**
 * Rappresenta il contratto minimo richiesto dalle regole di combattimento.
 */
public interface Combattente {

    /**
     * Restituisce il nome del combattente.
     *
     * @return nome del combattente
     */
    String nome();

    /**
     * Restituisce le statistiche base immutabili del combattente.
     *
     * @return statistiche base del combattente
     */
    Statistiche statisticheBase();

    /**
     * Restituisce l’attacco corrente, comprensivo dei bonus applicabili.
     *
     * @return attacco corrente
     */
    default int attacco() {
        return statisticheBase().attacco();
    }

    /**
     * Restituisce la difesa corrente, comprensiva dei bonus applicabili.
     *
     * @return difesa corrente
     */
    default int difesa() {
        return statisticheBase().difesa();
    }

    /**
     * Restituisce i punti vita residui del combattente.
     *
     * @return punti vita residui
     */
    int puntiVita();

    /**
     * Sottrae il danno dai punti vita senza scendere sotto zero.
     *
     * @param danno punti vita da sottrarre, non negativi
     * @throws IllegalArgumentException se il danno è negativo
     */
    void subisciDanno(int danno);

    /**
     * Recupera punti vita fino al massimo consentito, senza rianimare un combattente sconfitto.
     *
     * @param puntiVita quantità non negativa di punti vita da recuperare
     * @throws IllegalArgumentException se la quantità da recuperare è negativa
     * @throws IllegalStateException se una cura positiva tenta di rianimare un combattente sconfitto
     */
    void recuperaPuntiVita(int puntiVita);

    /**
     * Verifica se il combattente possiede ancora punti vita.
     *
     * @return {@code true} se i punti vita sono maggiori di zero
     */
    default boolean isVivo() {
        return puntiVita() > 0;
    }
}
