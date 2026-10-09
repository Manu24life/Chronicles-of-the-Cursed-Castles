package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento;

import java.util.Objects;

/** Regola standard del danno, indipendente da eroi, mostri e interfaccia grafica. */
public final class AttaccoBase implements AzioneCombattimento {

    @Override
    public RisultatoAzione esegui(Combattente esecutore, Combattente bersaglio) {
        Objects.requireNonNull(esecutore, "L'esecutore non può essere null");
        Objects.requireNonNull(bersaglio, "Il bersaglio non può essere null");
        if (!esecutore.isVivo() || !bersaglio.isVivo()) {
            return new RisultatoAzione(false, 0, 0, "L'azione non può essere eseguita");
        }

        int dannoCalcolato = Math.max(1, esecutore.attacco() - bersaglio.difesa());
        int dannoEffettivo = Math.min(dannoCalcolato, bersaglio.puntiVita());
        bersaglio.subisciDanno(dannoEffettivo);
        return new RisultatoAzione(
                true,
                dannoEffettivo,
                0,
                esecutore.nome() + " infligge " + dannoEffettivo + " danni a " + bersaglio.nome()
        );
    }
}
