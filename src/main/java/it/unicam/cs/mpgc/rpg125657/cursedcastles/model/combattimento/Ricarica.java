package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento;

/**
 * Astrazione di una politica di ricarica.
 * Implementazioni diverse possono basarsi sui turni o su altre condizioni.
 */
public interface Ricarica {

    boolean isDisponibile();

    void utilizza();

    void avanzaTurno();

    int turniRimanenti();
}
