package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.Eroe;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro.Nemico;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Coordina solamente ordine dei turni e conclusione di una battaglia. */
@Accessors(fluent = true)
public final class Battaglia {

    @Getter
    private final Eroe eroe;
    @Getter
    private final List<Nemico> nemici;
    private final AzioneCombattimento attaccoNemici;
    @Getter
    private StatoBattaglia stato = StatoBattaglia.TURNO_EROE;

    public Battaglia(Eroe eroe, List<? extends Nemico> nemici, AzioneCombattimento attaccoNemici) {
        this.eroe = Objects.requireNonNull(eroe, "L'eroe non può essere null");
        Objects.requireNonNull(nemici, "I nemici non possono essere null");
        if (nemici.isEmpty()) {
            throw new IllegalArgumentException("La battaglia richiede almeno un nemico");
        }
        if (nemici.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("La lista non può contenere nemici null");
        }
        this.nemici = List.copyOf(nemici);
        this.attaccoNemici = Objects.requireNonNull(attaccoNemici, "L'attacco non può essere null");
    }

    public RisultatoAzione eseguiAzioneEroe(int indiceNemico, AzioneCombattimento azione) {
        verificaStato(StatoBattaglia.TURNO_EROE);
        Objects.requireNonNull(azione, "L'azione non può essere null");
        Nemico bersaglio = nemici.get(indiceNemico);
        if (!bersaglio.isVivo()) {
            throw new IllegalArgumentException("Il nemico selezionato è già sconfitto");
        }

        RisultatoAzione risultato = azione.esegui(eroe, bersaglio);
        if (risultato.riuscita()) {
            aggiornaStatoDopoAzioneEroe();
        }
        return risultato;
    }

    /** Usa l'abilità posseduta dall'eroe sul nemico indicato. */
    public RisultatoAzione usaAbilitaEroe(int indiceNemico) {
        return eseguiAzioneEroe(
                indiceNemico,
                (esecutore, bersaglio) -> eroe.abilita().usa(eroe, bersaglio)
        );
    }

    public List<RisultatoAzione> eseguiTurnoNemici() {
        verificaStato(StatoBattaglia.TURNO_NEMICI);
        List<RisultatoAzione> risultati = new ArrayList<>();
        for (Nemico nemico : nemici) {
            if (nemico.isVivo() && eroe.isVivo()) {
                risultati.add(attaccoNemici.esegui(nemico, eroe));
            }
        }
        if (eroe.isVivo()) {
            eroe.avanzaTurno();
            stato = StatoBattaglia.TURNO_EROE;
        } else {
            stato = StatoBattaglia.SCONFITTA;
        }
        return List.copyOf(risultati);
    }

    private void aggiornaStatoDopoAzioneEroe() {
        stato = nemici.stream().noneMatch(Nemico::isVivo)
                ? StatoBattaglia.VITTORIA
                : StatoBattaglia.TURNO_NEMICI;
    }

    private void verificaStato(StatoBattaglia atteso) {
        if (stato != atteso) {
            throw new IllegalStateException("Azione non consentita nello stato " + stato);
        }
    }
}
