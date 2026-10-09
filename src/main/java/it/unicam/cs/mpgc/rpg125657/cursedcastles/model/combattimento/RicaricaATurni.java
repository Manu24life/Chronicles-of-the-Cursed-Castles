package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento;

import lombok.Getter;
import lombok.experimental.Accessors;

/** Politica di ricarica che diventa disponibile dopo un numero definito di turni. */
public final class RicaricaATurni implements Ricarica {

    private final int durata;
    private final int attesaIniziale;
    @Getter
    @Accessors(fluent = true)
    private int turniRimanenti;

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

    @Override
    public boolean isDisponibile() {
        return turniRimanenti == 0;
    }

    @Override
    public void utilizza() {
        if (!isDisponibile()) {
            throw new IllegalStateException("Azione ancora in ricarica");
        }
        turniRimanenti = durata;
    }

    @Override
    public void avanzaTurno() {
        if (turniRimanenti > 0) {
            turniRimanenti--;
        }
    }

    @Override
    public void reset() {
        turniRimanenti = attesaIniziale;
    }

}
