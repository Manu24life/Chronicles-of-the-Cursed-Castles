package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.Combattente;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.EsitoAzione;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.Ricarica;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.RisultatoAzione;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.Eroe;

import java.util.Objects;

/**
 * Fornisce un template che delega il conteggio dei turni a una politica di ricarica.
 */
public abstract class AbilitaConRicarica implements AbilitaEroe {

    private final String nome;
    private final Ricarica ricarica;

    /**
     * Inizializza un’istanza di {@code AbilitaConRicarica} con le dipendenze ricevute.
     *
     * @param nome nome non vuoto dell’abilità
     * @param ricarica politica che regola la disponibilità dell'abilità
     * @throws NullPointerException se almeno uno tra {@code nome}, {@code ricarica} è {@code null}
     * @throws IllegalArgumentException se il nome dell’abilità è vuoto
     */
    protected AbilitaConRicarica(String nome, Ricarica ricarica) {
        Objects.requireNonNull(nome, "Il nome non può essere null");
        if (nome.isBlank()) {
            throw new IllegalArgumentException("Il nome non può essere vuoto");
        }
        this.nome = nome.strip();
        this.ricarica = Objects.requireNonNull(ricarica, "La ricarica non può essere null");
    }

    /**
     * {@inheritDoc}
     * <p>
     * Verifica vitalità e disponibilità prima di applicare l’effetto concreto. Avvia la ricarica solo se l’effetto
     * riesce; restituisce un fallimento se l’eroe è sconfitto o l’abilità è in ricarica.
     * @throws NullPointerException se almeno uno tra {@code utilizzatore}, {@code bersaglio} è {@code null}
     */
    @Override
    public final RisultatoAzione usa(Eroe utilizzatore, Combattente bersaglio) {
        Objects.requireNonNull(utilizzatore, "L'utilizzatore non può essere null");
        Objects.requireNonNull(bersaglio, "Il bersaglio non può essere null");

        if (!utilizzatore.isVivo()) {
            return fallimento(
                    EsitoAzione.AZIONE_NON_CONSENTITA,
                    "Un eroe sconfitto non può usare abilità"
            );
        }
        if (!ricarica.isDisponibile()) {
            return fallimento(
                    EsitoAzione.ABILITA_IN_RICARICA,
                    "Abilità disponibile tra " + ricarica.turniRimanenti() + " turni"
            );
        }

        RisultatoAzione risultato = applica(utilizzatore, bersaglio);
        if (risultato.riuscita()) {
            ricarica.utilizza();
        }
        return risultato;
    }

    /**
     * Applica l’effetto specifico dopo la verifica della vitalità dell’eroe e della disponibilità della ricarica.
     *
     * @param utilizzatore eroe che attiva l'abilità
     * @param bersaglio combattente destinatario dell'effetto
     * @return esito dell’azione, con successo o motivo del fallimento e variazioni effettive di vita
     */
    protected abstract RisultatoAzione applica(Eroe utilizzatore, Combattente bersaglio);

    /**
     * {@inheritDoc}
     */
    @Override
    public final String nome() {
        return nome;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isDisponibile() {
        return ricarica.isDisponibile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int turniRimanenti() {
        return ricarica.turniRimanenti();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void avanzaTurno() {
        ricarica.avanzaTurno();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void reset() {
        ricarica.reset();
    }

    private RisultatoAzione fallimento(EsitoAzione codice, String descrizione) {
        return RisultatoAzione.fallimento(codice, descrizione);
    }
}
