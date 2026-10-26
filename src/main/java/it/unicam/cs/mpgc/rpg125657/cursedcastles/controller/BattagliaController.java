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

/** Coordina i comandi di combattimento senza duplicare le regole del dominio. */
public final class BattagliaController {

    private final ServizioGioco gioco;

    public BattagliaController(ServizioGioco gioco) {
        this.gioco = Objects.requireNonNull(gioco);
    }

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

    public EsitoComando attacca(int indiceNemico) {
        return new EsitoComando(gioco.attacca(indiceNemico), stato());
    }

    public EsitoComando usaAbilita(int indiceNemico) {
        return new EsitoComando(gioco.usaAbilita(indiceNemico), stato());
    }

    public EsitoComando usaAbilita() {
        return new EsitoComando(gioco.usaAbilita(), stato());
    }

    public EsitoComando cura() {
        return new EsitoComando(gioco.cura(), stato());
    }

    public EsitoComando eseguiTurnoNemico() {
        return new EsitoComando(gioco.eseguiTurnoNemico(), stato());
    }

    public VistaPartita salvaVittoria() {
        gioco.salvaVittoria();
        return stato();
    }

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
