package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.partita;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello.Castello;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello.FabbricaNemici;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.AttaccoBase;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.AzioneCombattimento;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.Battaglia;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.CuraPercentualeLimitata;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.FabbricaRegolaCura;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.StatoBattaglia;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.Eroe;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Objects;

/**
 * Radice dell'aggregato di gioco: governa la progressione ordinata nei castelli
 * e il ciclo di vita dello stato che deve essere resettato tra un castello e il successivo.
 */
@Accessors(fluent = true)
public final class Partita {

    @Getter
    private final Eroe eroe;
    private final List<Castello> castelli;
    private final FabbricaNemici fabbricaNemici;
    private final AzioneCombattimento attaccoEroe;
    private final AzioneCombattimento attaccoNemici;
    private final FabbricaRegolaCura fabbricaRegolaCura;
    private int indiceCastelloCorrente = -1;
    private Battaglia battagliaCorrente;
    private StatoPartita stato = StatoPartita.NON_INIZIATA;

    public Partita(Eroe eroe, List<Castello> castelli, FabbricaNemici fabbricaNemici) {
        this(
                eroe,
                castelli,
                fabbricaNemici,
                new AttaccoBase(),
                new AttaccoBase(),
                CuraPercentualeLimitata::new
        );
    }

    public Partita(
            Eroe eroe,
            List<Castello> castelli,
            FabbricaNemici fabbricaNemici,
            AzioneCombattimento attaccoEroe,
            AzioneCombattimento attaccoNemici,
            FabbricaRegolaCura fabbricaRegolaCura
    ) {
        this.eroe = Objects.requireNonNull(eroe, "L'eroe non può essere null");
        Objects.requireNonNull(castelli, "I castelli non possono essere null");
        if (castelli.isEmpty()) {
            throw new IllegalArgumentException("Una partita richiede almeno un castello");
        }
        if (castelli.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("La lista non può contenere castelli null");
        }
        this.castelli = List.copyOf(castelli);
        this.fabbricaNemici = Objects.requireNonNull(fabbricaNemici, "La fabbrica non può essere null");
        this.attaccoEroe = Objects.requireNonNull(attaccoEroe, "L'attacco dell'eroe non può essere null");
        this.attaccoNemici = Objects.requireNonNull(attaccoNemici, "L'attacco dei nemici non può essere null");
        this.fabbricaRegolaCura = Objects.requireNonNull(
                fabbricaRegolaCura,
                "La fabbrica della cura non può essere null"
        );
    }

    public void inizia() {
        if (stato != StatoPartita.NON_INIZIATA) {
            throw new IllegalStateException("La partita è già iniziata");
        }
        avviaCastello(0);
    }

    /**
     * Passa al castello successivo solo dopo la vittoria. Se non restano
     * castelli, la partita viene marcata come completata.
     */
    public void avanzaAlProssimoCastello() {
        verificaPartitaInCorso();
        if (battagliaCorrente.stato() != StatoBattaglia.VITTORIA) {
            throw new IllegalStateException("Il castello corrente non è stato completato");
        }

        int prossimoIndice = indiceCastelloCorrente + 1;
        if (prossimoIndice >= castelli.size()) {
            stato = StatoPartita.COMPLETATA;
            battagliaCorrente = null;
            return;
        }
        avviaCastello(prossimoIndice);
    }

    public StatoPartita stato() {
        if (stato == StatoPartita.IN_CORSO
                && battagliaCorrente.stato() == StatoBattaglia.SCONFITTA) {
            return StatoPartita.SCONFITTA;
        }
        return stato;
    }

    public Castello castelloCorrente() {
        verificaPartitaInCorso();
        return castelli.get(indiceCastelloCorrente);
    }

    public int numeroCastelloCorrente() {
        verificaPartitaInCorso();
        return indiceCastelloCorrente + 1;
    }

    public int numeroCastelli() {
        return castelli.size();
    }

    public Battaglia battagliaCorrente() {
        verificaPartitaInCorso();
        return battagliaCorrente;
    }

    private void avviaCastello(int indice) {
        if (!eroe.isVivo()) {
            stato = StatoPartita.SCONFITTA;
            throw new IllegalStateException("Un eroe sconfitto non può entrare in un nuovo castello");
        }
        eroe.preparaNuovoCastello();
        indiceCastelloCorrente = indice;
        Castello castello = castelli.get(indice);
        battagliaCorrente = new Battaglia(
                eroe,
                fabbricaNemici.creaNemici(castello),
                attaccoEroe,
                attaccoNemici,
                Objects.requireNonNull(
                        fabbricaRegolaCura.crea(),
                        "La fabbrica non può restituire una regola di cura null"
                )
        );
        stato = StatoPartita.IN_CORSO;
    }

    private void verificaPartitaInCorso() {
        StatoPartita statoCorrente = stato();
        if (statoCorrente != StatoPartita.IN_CORSO) {
            throw new IllegalStateException("Operazione non consentita nello stato " + statoCorrente);
        }
    }
}
