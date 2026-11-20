package it.unicam.cs.mpgc.rpg125657.cursedcastles.controller;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello.Castello;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.Battaglia;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.StatoBattaglia;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.Eroe;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.partita.Partita;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.partita.StatoPartita;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.application.ServizioGioco;

import java.util.Objects;
import java.util.Optional;

/**
 * Coordina i comandi di combattimento senza duplicare le regole del dominio.
 */
public final class BattagliaController {

    private final ServizioGioco gioco;

    /**
     * Inizializza un’istanza di {@code BattagliaController} con le dipendenze ricevute.
     *
     * @param gioco servizio applicativo condiviso tra i controller
     * @throws NullPointerException se almeno uno tra {@code gioco} è {@code null}
     */
    public BattagliaController(ServizioGioco gioco) {
        this.gioco = Objects.requireNonNull(gioco);
    }

    /**
     * Costruisce uno snapshot immutabile della partita, senza esporre i personaggi mutabili alla vista.
     *
     * @return stato corrente della partita o del menu
     */
    public VistaPartita stato() {
        Partita partita = gioco.richiediPartita();
        Eroe eroe = partita.eroe();
        StatoEroe statoEroe = new StatoEroe(
                eroe.nome(),
                eroe.classe().codice(),
                eroe.classe().nome(),
                eroe.puntiVita(),
                eroe.statisticheBase().puntiVitaMassimi(),
                eroe.attacco(),
                eroe.difesa(),
                eroe.bonusAttaccoPercentuale(),
                eroe.bonusDifesaPercentuale()
        );

        StatoPartita statoPartita = partita.stato();
        Optional<StatoCastello> statoCastello = statoPartita == StatoPartita.COMPLETATA
                ? Optional.empty()
                : Optional.of(creaStatoCastello(partita));
        return new VistaPartita(statoPartita, statoEroe, statoCastello);
    }

    /**
     * Esegue l’attacco dell’eroe contro il nemico selezionato.
     *
     * @param indiceNemico indice del nemico nella lista originale, a partire da zero
     * @return esito del comando insieme allo snapshot aggiornato della partita
     * @throws IllegalStateException se manca una partita attiva o il suo stato non consente il comando
     * @throws IllegalArgumentException se l'indice non identifica un nemico vivo
     * @throws it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza se il salvataggio fallisce dopo che
     *     l'azione ha portato la battaglia alla vittoria
     */
    public EsitoComando attacca(int indiceNemico) {
        return new EsitoComando(gioco.attacca(indiceNemico), stato());
    }

    /**
     * Attiva l'abilità dell'eroe, usando l'indice solo se serve un bersaglio.
     *
     * @param indiceNemico indice del nemico nella lista originale, a partire da zero
     * @return esito del comando insieme allo snapshot aggiornato della partita
     * @throws IllegalStateException se manca una partita attiva o il suo stato non consente il comando
     * @throws IllegalArgumentException se l'abilità richiede un bersaglio e l'indice non identifica un nemico vivo
     * @throws it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza se il salvataggio fallisce dopo che
     *     l'azione ha portato la battaglia alla vittoria
     */
    public EsitoComando usaAbilita(int indiceNemico) {
        return new EsitoComando(gioco.usaAbilita(indiceNemico), stato());
    }

    /**
     * Attiva l'abilità dell'eroe senza selezionare un bersaglio. Restituisce un fallimento se serve un nemico.
     *
     * @return esito del comando insieme allo snapshot aggiornato della partita
     * @throws IllegalStateException se manca una partita attiva o il suo stato non consente il comando
     * @throws it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza se il salvataggio fallisce dopo che
     *     l'azione ha portato la battaglia alla vittoria
     */
    public EsitoComando usaAbilita() {
        return new EsitoComando(gioco.usaAbilita(), stato());
    }

    /**
     * Tenta la cura dell’eroe usando gli utilizzi disponibili nel castello.
     *
     * @return esito del comando insieme allo snapshot aggiornato della partita
     * @throws IllegalStateException se manca una partita attiva o il suo stato non consente il comando
     */
    public EsitoComando cura() {
        return new EsitoComando(gioco.cura(), stato());
    }

    /**
     * Esegue la risposta di un solo nemico e aggiorna lo stato del combattimento.
     *
     * @return esito del comando insieme allo snapshot aggiornato della partita
     * @throws IllegalStateException se manca una partita attiva o il suo stato non consente il comando
     */
    public EsitoComando eseguiTurnoNemico() {
        return new EsitoComando(gioco.eseguiTurnoNemico(), stato());
    }

    /**
     * Registra la vittoria corrente una sola volta. In caso di errore di persistenza, consente di ritentare.
     *
     * @return snapshot aggiornato dopo il salvataggio
     * @throws IllegalStateException se manca una partita o un salvataggio utilizzabile, oppure la battaglia non è
     *     stata vinta
     * @throws it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza se il salvataggio della vittoria
     *     non riesce
     */
    public VistaPartita salvaVittoria() {
        gioco.salvaVittoria();
        return stato();
    }

    /**
     * Salva la vittoria corrente e avanza nella campagna, completandola se si trova all’ultimo castello.
     *
     * @return snapshot del nuovo castello o della campagna completata
     * @throws IllegalStateException se manca una partita attiva o il suo stato non consente il comando
     * @throws it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza se il salvataggio della vittoria
     *     non riesce
     */
    public VistaPartita vaiAlCastelloSuccessivo() {
        gioco.vaiAlCastelloSuccessivo();
        return stato();
    }

    private StatoCastello creaStatoCastello(Partita partita) {
        Battaglia battaglia = partita.battagliaCorrente();
        Castello castello = partita.castelloCorrente();
        Eroe eroe = partita.eroe();
        boolean vittoriaSalvata = battaglia.stato() == StatoBattaglia.VITTORIA
                && gioco.vittoriaSalvata();
        return new StatoCastello(
                castello.codice(),
                castello.nome(),
                partita.numeroCastelloCorrente(),
                partita.numeroCastelli(),
                battaglia.stato(),
                battaglia.statoNemici(),
                battaglia.cureRimanenti(),
                eroe.abilita().nome(),
                eroe.abilita().isDisponibile(),
                eroe.abilita().turniRimanenti(),
                eroe.abilita().richiedeBersaglio(),
                vittoriaSalvata,
                vittoriaSalvata
        );
    }
}
