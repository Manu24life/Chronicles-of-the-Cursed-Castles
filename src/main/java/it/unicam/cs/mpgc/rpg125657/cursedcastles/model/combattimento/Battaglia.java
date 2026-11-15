package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.Eroe;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro.Nemico;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Objects;

/**
 * Rappresenta l’aggregato di combattimento di un castello.
 * Dopo ogni azione riuscita dell'eroe risponde un solo nemico ancora vivo.
 */
@Accessors(fluent = true)
public final class Battaglia {

    @Getter
    private final Eroe eroe;
    private final List<Nemico> nemici;
    private final AzioneCombattimento attaccoEroe;
    private final AzioneCombattimento attaccoNemici;
    private final RegolaCura regolaCura;
    private int indiceNemicoCheRisponde = -1;
    @Getter
    private StatoBattaglia stato = StatoBattaglia.TURNO_EROE;

    /**
     * Inizializza la battaglia con una strategia di attacco condivisa e la cura standard.
     *
     * @param eroe eroe che affronta la campagna
     * @param nemici avversari della battaglia, conservati nello stesso ordine
     * @param attacco strategia di attacco condivisa tra eroe e nemici
     */
    public Battaglia(Eroe eroe, List<? extends Nemico> nemici, AzioneCombattimento attacco) {
        this(eroe, nemici, attacco, attacco, new CuraPercentualeLimitata());
    }

    /**
     * Inizializza una battaglia con almeno un nemico vivo e strategie di attacco e cura indipendenti.
     *
     * @param eroe eroe che affronta la campagna
     * @param nemici avversari della battaglia, conservati nello stesso ordine
     * @param attaccoEroe strategia di attacco dell'eroe
     * @param attaccoNemici strategia di attacco dei nemici
     * @param regolaCura regola di cura con stato indipendente per la battaglia
     * @throws NullPointerException se almeno uno tra {@code eroe}, {@code nemici}, {@code attaccoEroe},
     *     {@code attaccoNemici}, {@code regolaCura} è {@code null}
     * @throws IllegalArgumentException se l’eroe è sconfitto oppure la lista dei nemici è vuota, contiene null o
     *     non contiene nemici vivi
     */
    public Battaglia(
            Eroe eroe,
            List<? extends Nemico> nemici,
            AzioneCombattimento attaccoEroe,
            AzioneCombattimento attaccoNemici,
            RegolaCura regolaCura
    ) {
        this.eroe = Objects.requireNonNull(eroe, "L'eroe non può essere null");
        if (!eroe.isVivo()) {
            throw new IllegalArgumentException("Un eroe sconfitto non può iniziare una battaglia");
        }
        Objects.requireNonNull(nemici, "I nemici non possono essere null");
        if (nemici.isEmpty()) {
            throw new IllegalArgumentException("La battaglia richiede almeno un nemico");
        }
        if (nemici.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("La lista non può contenere nemici null");
        }
        if (nemici.stream().noneMatch(Nemico::isVivo)) {
            throw new IllegalArgumentException("La battaglia richiede almeno un nemico vivo");
        }
        this.nemici = List.copyOf(nemici);
        this.attaccoEroe = Objects.requireNonNull(attaccoEroe, "L'attacco dell'eroe non può essere null");
        this.attaccoNemici = Objects.requireNonNull(attaccoNemici, "L'attacco non può essere null");
        this.regolaCura = Objects.requireNonNull(regolaCura, "La regola di cura non può essere null");
    }

    /**
     * Esegue l’attacco dell’eroe contro il nemico selezionato.
     *
     * @param indiceNemico indice del nemico vivo nella lista originale, a partire da zero
     * @return esito dell’azione, con successo o motivo del fallimento e variazioni effettive di vita
     * @throws IllegalStateException se non è il turno dell’eroe
     * @throws IllegalArgumentException se l’indice non identifica un nemico vivo
     */
    public RisultatoAzione attacca(int indiceNemico) {
        return eseguiAzioneConBersaglio(indiceNemico, attaccoEroe);
    }

    /** @deprecated Il Controller dovrebbe usare i metodi espliciti dell'aggregato. */
    @Deprecated
    RisultatoAzione eseguiAzioneEroe(int indiceNemico, AzioneCombattimento azione) {
        return eseguiAzioneConBersaglio(indiceNemico, azione);
    }

    private RisultatoAzione eseguiAzioneConBersaglio(
            int indiceNemico,
            AzioneCombattimento azione
    ) {
        verificaStato(StatoBattaglia.TURNO_EROE);
        Objects.requireNonNull(azione, "L'azione non può essere null");
        Nemico bersaglio = nemicoVivo(indiceNemico);

        RisultatoAzione risultato = azione.esegui(eroe, bersaglio);
        if (risultato.riuscita()) {
            int indiceRisposta = bersaglio.isVivo() ? indiceNemico : primoNemicoVivo();
            preparaRispostaDelNemico(indiceRisposta);
        }
        return risultato;
    }

    /**
     * Attiva l’abilità dell’eroe; prepara la risposta nemica solo quando l’azione riesce.
     * Ignora l’indice quando l’abilità non richiede un bersaglio.
     *
     * @param indiceNemico indice del nemico vivo nella lista originale, a partire da zero
     * @return esito dell’azione, con successo o motivo del fallimento e variazioni effettive di vita
     * @throws IllegalStateException se non è il turno dell’eroe
     * @throws IllegalArgumentException se l’abilità richiede un bersaglio e l’indice non identifica un nemico vivo
     */
    public RisultatoAzione usaAbilitaEroe(int indiceNemico) {
        if (!eroe.abilita().richiedeBersaglio()) {
            return usaAbilitaEroe();
        }
        return eseguiAzioneConBersaglio(
                indiceNemico,
                (esecutore, bersaglio) -> eroe.abilita().usa(eroe, bersaglio)
        );
    }

    /**
     * Attiva l’abilità dell’eroe; prepara la risposta nemica solo quando l’azione riesce. Restituisce un fallimento
     * se l’abilità richiede un nemico.
     *
     * @return esito dell’azione, con successo o motivo del fallimento e variazioni effettive di vita
     * @throws IllegalStateException se non è il turno dell’eroe
     */
    public RisultatoAzione usaAbilitaEroe() {
        verificaStato(StatoBattaglia.TURNO_EROE);
        if (eroe.abilita().richiedeBersaglio()) {
            return RisultatoAzione.fallimento(
                    EsitoAzione.AZIONE_NON_CONSENTITA,
                    "L'abilità richiede la scelta di un nemico"
            );
        }
        RisultatoAzione risultato = eroe.abilita().usa(eroe, eroe);
        if (risultato.riuscita()) {
            preparaRispostaDelNemico(primoNemicoVivo());
        }
        return risultato;
    }

    /**
     * Tenta la cura dell’eroe e prepara la risposta nemica solo in caso di successo.
     *
     * @return esito dell’azione, con successo o motivo del fallimento e variazioni effettive di vita
     * @throws IllegalStateException se non è il turno dell’eroe
     */
    public RisultatoAzione curaEroe() {
        verificaStato(StatoBattaglia.TURNO_EROE);
        RisultatoAzione risultato = regolaCura.usa(eroe);
        if (risultato.riuscita()) {
            preparaRispostaDelNemico(primoNemicoVivo());
        }
        return risultato;
    }

    /**
     * Restituisce le cure ancora utilizzabili nel castello.
     *
     * @return numero di cure residue
     */
    public int cureRimanenti() {
        return regolaCura.utilizziRimanenti();
    }

    /**
     * Esegue la risposta di un solo nemico e aggiorna lo stato del combattimento.
     *
     * @return esito dell’azione, con successo o motivo del fallimento e variazioni effettive di vita
     * @throws IllegalStateException se non è il turno dei nemici o non esiste un nemico vivo
     */
    public RisultatoAzione eseguiTurnoNemico() {
        verificaStato(StatoBattaglia.TURNO_NEMICI);
        RisultatoAzione risultato = attaccoNemici.esegui(nemicoCheRisponde(), eroe);
        concludeTurnoNemico();
        return risultato;
    }

    /** @deprecated Ora ogni turno contiene la risposta di un solo nemico. */
    @Deprecated
    List<RisultatoAzione> eseguiTurnoNemici() {
        return List.of(eseguiTurnoNemico());
    }

    /**
     * Crea snapshot immutabili dei nemici mantenendo gli indici originali, inclusi quelli dei nemici sconfitti.
     *
     * @return lista immutabile degli snapshot dei nemici
     */
    public List<StatoNemico> statoNemici() {
        return java.util.stream.IntStream.range(0, nemici.size())
                .mapToObj(indice -> StatoNemico.da(indice, nemici.get(indice)))
                .toList();
    }

    private void concludeTurnoNemico() {
        indiceNemicoCheRisponde = -1;
        if (eroe.isVivo()) {
            eroe.avanzaTurno();
            stato = StatoBattaglia.TURNO_EROE;
        } else {
            stato = StatoBattaglia.SCONFITTA;
        }
    }

    private void preparaRispostaDelNemico(int indiceRisposta) {
        if (nemici.stream().noneMatch(Nemico::isVivo)) {
            indiceNemicoCheRisponde = -1;
            stato = StatoBattaglia.VITTORIA;
            return;
        }
        indiceNemicoCheRisponde = indiceRisposta;
        stato = StatoBattaglia.TURNO_NEMICI;
    }

    private Nemico nemicoVivo(int indiceNemico) {
        if (indiceNemico < 0 || indiceNemico >= nemici.size()) {
            throw new IllegalArgumentException("Indice del nemico non valido: " + indiceNemico);
        }
        Nemico nemico = nemici.get(indiceNemico);
        if (!nemico.isVivo()) {
            throw new IllegalArgumentException("Il nemico selezionato è già sconfitto");
        }
        return nemico;
    }

    private int primoNemicoVivo() {
        for (int indice = 0; indice < nemici.size(); indice++) {
            if (nemici.get(indice).isVivo()) {
                return indice;
            }
        }
        return -1;
    }

    private Nemico nemicoCheRisponde() {
        if (indiceNemicoCheRisponde < 0
                || indiceNemicoCheRisponde >= nemici.size()
                || !nemici.get(indiceNemicoCheRisponde).isVivo()) {
            indiceNemicoCheRisponde = primoNemicoVivo();
        }
        if (indiceNemicoCheRisponde < 0) {
            throw new IllegalStateException("Non esiste un nemico vivo che possa rispondere");
        }
        return nemici.get(indiceNemicoCheRisponde);
    }

    private void verificaStato(StatoBattaglia atteso) {
        if (stato != atteso) {
            throw new IllegalStateException("Azione non consentita nello stato " + stato);
        }
    }
}
