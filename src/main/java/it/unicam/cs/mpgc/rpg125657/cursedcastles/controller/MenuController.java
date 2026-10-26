package it.unicam.cs.mpgc.rpg125657.cursedcastles.controller;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.application.ServizioGioco;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.progressione.ProgressiPartita;
import java.util.Objects;

/** Traduce i comandi del menu nei casi d'uso applicativi. */
public final class MenuController {
    private final ServizioGioco gioco;

    public MenuController(ServizioGioco gioco) {
        this.gioco = Objects.requireNonNull(gioco);
    }

    public StatoMenu stato() {
        return new StatoMenu(gioco.classiDisponibili().stream().map(ClasseDisponibile::da).toList(),
                gioco.carica().map(this::creaRiepilogo));
    }

    public void nuovaPartita(String nomeEroe, String codiceClasse) {
        gioco.nuovaPartita(nomeEroe, codiceClasse);
    }

    public void continuaPartita() { gioco.continuaPartita(); }
    public void eliminaSalvataggio() { gioco.eliminaSalvataggio(); }

    private RiepilogoSalvataggio creaRiepilogo(ProgressiPartita progressi) {
        return new RiepilogoSalvataggio(progressi.nomeEroe(), progressi.codiceClasse(),
                progressi.codiciCastelliCompletati(), gioco.campagnaCompletata(progressi));
    }
}
