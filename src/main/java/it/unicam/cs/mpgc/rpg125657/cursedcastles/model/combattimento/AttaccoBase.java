package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento;

import java.util.Objects;

/**
 * Rappresenta la regola standard del danno, indipendente da eroi, mostri e interfaccia grafica.
 */
public final class AttaccoBase implements AzioneCombattimento {

    /**
     * {@inheritDoc}
     * <p>
     * Calcola il danno come massimo tra uno e la differenza fra attacco e difesa, limitandolo alla vita residua del
     * bersaglio. Restituisce un fallimento se uno dei combattenti è sconfitto.
     * @throws NullPointerException se almeno uno tra {@code esecutore}, {@code bersaglio} è {@code null}
     */
    @Override
    public RisultatoAzione esegui(Combattente esecutore, Combattente bersaglio) {
        Objects.requireNonNull(esecutore, "L'esecutore non può essere null");
        Objects.requireNonNull(bersaglio, "Il bersaglio non può essere null");
        if (!esecutore.isVivo()) {
            return RisultatoAzione.fallimento(
                    EsitoAzione.AZIONE_NON_CONSENTITA,
                    "Un combattente sconfitto non può attaccare"
            );
        }
        if (!bersaglio.isVivo()) {
            return RisultatoAzione.fallimento(
                    EsitoAzione.BERSAGLIO_SCONFITTO,
                    "Il bersaglio è già sconfitto"
            );
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
