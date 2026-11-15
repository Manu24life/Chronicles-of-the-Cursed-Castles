package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Rappresenta una politica di ricarica che diventa disponibile dopo un numero definito di turni.
 */
public final class RicaricaATurni implements Ricarica {

    private final int durata;
    private final int attesaIniziale;
    @Getter
    @Accessors(fluent = true)
    private int turniRimanenti;

    /**
     * Configura la durata della ricarica e l’attesa iniziale prima del primo utilizzo.
     *
     * @param durata numero di turni di attesa dopo ogni utilizzo
     * @param attesaIniziale numero di turni di attesa iniziali, anche zero
     * @throws IllegalArgumentException se la durata non è positiva o l’attesa iniziale è negativa
     */
    public RicaricaATurni(int durata, int attesaIniziale) {
        if (durata < 1) {
            throw new IllegalArgumentException("La durata deve essere positiva");
        }
        if (attesaIniziale < 0) {
            throw new IllegalArgumentException("L'attesa iniziale non può essere negativa");
        }
        this.durata = durata;
        this.attesaIniziale = attesaIniziale;
        this.turniRimanenti = attesaIniziale;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDisponibile() {
        return turniRimanenti == 0;
    }

    /**
     * {@inheritDoc}
     * @throws IllegalStateException se la ricarica non è ancora disponibile
     */
    @Override
    public void utilizza() {
        if (!isDisponibile()) {
            throw new IllegalStateException("Azione ancora in ricarica");
        }
        turniRimanenti = durata;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void avanzaTurno() {
        if (turniRimanenti > 0) {
            turniRimanenti--;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        turniRimanenti = attesaIniziale;
    }

}
