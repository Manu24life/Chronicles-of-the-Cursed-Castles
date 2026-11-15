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
 * Rappresenta la radice dell'aggregato di gioco: governa la progressione ordinata nei castelli
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

    /**
     * Configura una campagna con attacchi standard e una nuova cura standard per ciascun castello.
     *
     * @param eroe eroe che affronta la campagna
     * @param castelli sequenza ordinata dei castelli della campagna
     * @param fabbricaNemici fabbrica che crea nuovi avversari per ogni castello
     */
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

    /**
     * Configura una campagna non ancora iniziata; conserva l’ordine dei castelli e le strategie ricevute.
     *
     * @param eroe eroe che affronta la campagna
     * @param castelli sequenza ordinata dei castelli della campagna
     * @param fabbricaNemici fabbrica che crea nuovi avversari per ogni castello
     * @param attaccoEroe strategia di attacco dell'eroe
     * @param attaccoNemici strategia di attacco dei nemici
     * @param fabbricaRegolaCura fabbrica di una nuova regola di cura per ciascun castello
     * @throws NullPointerException se almeno uno tra {@code eroe}, {@code castelli}, {@code fabbricaNemici},
     *     {@code attaccoEroe}, {@code attaccoNemici}, {@code fabbricaRegolaCura} è {@code null}
     * @throws IllegalArgumentException se la sequenza dei castelli è vuota o contiene null
     */
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

    /**
     * Avvia la partita dal primo castello della campagna.
     *
     * @throws IllegalStateException se la partita è già iniziata o l’eroe è sconfitto
     */
    public void inizia() {
        iniziaDa(0);
    }

    /**
     * Avvia la partita dal castello indicato, ricostituendo vita, bonus e ricarica dell’eroe.
     *
     * @param indice indice nella sequenza originale, a partire da zero
     * @throws IllegalArgumentException se l’indice iniziale non identifica un castello della campagna
     * @throws IllegalStateException se la partita è già iniziata o l’eroe è sconfitto
     */
    public void iniziaDa(int indice) {
        if (stato != StatoPartita.NON_INIZIATA) {
            throw new IllegalStateException("La partita è già iniziata");
        }
        if (indice < 0 || indice >= castelli.size()) {
            throw new IllegalArgumentException("Indice del castello iniziale non valido: " + indice);
        }
        avviaCastello(indice);
    }

    /**
     * Avanza solo dopo una vittoria; completa la campagna se non rimangono castelli, altrimenti prepara una nuova
     * battaglia.
     *
     * @throws IllegalStateException se la partita non è in corso o la battaglia corrente non è stata vinta
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

    /**
     * Restituisce la fase della partita, rilevando anche la sconfitta nella battaglia corrente.
     *
     * @return stato corrente della partita o del menu
     */
    public StatoPartita stato() {
        if (stato == StatoPartita.IN_CORSO
                && battagliaCorrente.stato() == StatoBattaglia.SCONFITTA) {
            return StatoPartita.SCONFITTA;
        }
        return stato;
    }

    /**
     * Restituisce il castello attualmente affrontato.
     *
     * @return castello corrente
     * @throws IllegalStateException se non esiste un castello corrente
     */
    public Castello castelloCorrente() {
        verificaCastelloCorrenteDisponibile();
        return castelli.get(indiceCastelloCorrente);
    }

    /**
     * Restituisce la posizione del castello corrente nella campagna, a partire da uno.
     *
     * @return numero del castello a partire da uno
     * @throws IllegalStateException se non esiste un castello corrente
     */
    public int numeroCastelloCorrente() {
        verificaCastelloCorrenteDisponibile();
        return indiceCastelloCorrente + 1;
    }

    /**
     * Restituisce il numero totale di castelli della campagna.
     *
     * @return numero totale di castelli
     */
    public int numeroCastelli() {
        return castelli.size();
    }

    /**
     * Restituisce il combattimento del castello corrente.
     *
     * @return battaglia corrente
     * @throws IllegalStateException se non esiste un castello corrente
     */
    public Battaglia battagliaCorrente() {
        verificaCastelloCorrenteDisponibile();
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

    private void verificaCastelloCorrenteDisponibile() {
        if (indiceCastelloCorrente < 0 || battagliaCorrente == null) {
            throw new IllegalStateException("Non esiste un castello corrente");
        }
    }
}
