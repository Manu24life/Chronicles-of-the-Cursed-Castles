package it.unicam.cs.mpgc.rpg125657.cursedcastles.controller;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.application.ServizioGioco;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.progressione.ProgressiPartita;
import java.util.Objects;

/**
 * Traduce i comandi del menu nei casi d'uso applicativi.
 */
public final class MenuController {
    private final ServizioGioco gioco;

    /**
     * Inizializza un’istanza di {@code MenuController} con le dipendenze ricevute.
     *
     * @param gioco servizio applicativo condiviso tra i controller
     * @throws NullPointerException se almeno uno tra {@code gioco} è {@code null}
     */
    public MenuController(ServizioGioco gioco) {
        this.gioco = Objects.requireNonNull(gioco);
    }

    /**
     * Costruisce lo stato del menu con classi disponibili e riepilogo del salvataggio.
     *
     * @return stato corrente della partita o del menu
     */
    public StatoMenu stato() {
        return new StatoMenu(gioco.classiDisponibili().stream().map(ClasseDisponibile::da).toList(),
                gioco.carica().map(this::creaRiepilogo));
    }

    /**
     * Delega la creazione della partita al servizio applicativo e sostituisce lo slot persistente.
     *
     * @param nomeEroe nome dell'eroe, ripulito dagli spazi iniziali e finali
     * @param codiceClasse identificativo della classe dell'eroe
     * @throws IllegalArgumentException se il nome non è valido o il codice della classe è sconosciuto
     * @throws it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza se l’accesso al
     *     salvataggio non riesce
     */
    public void nuovaPartita(String nomeEroe, String codiceClasse) {
        gioco.nuovaPartita(nomeEroe, codiceClasse);
    }

    /**
     * Carica il salvataggio e avvia una nuova battaglia dal primo castello incompleto.
     *
     * @throws IllegalStateException se il salvataggio manca, è incompatibile o descrive una campagna completata
     * @throws it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza se l’accesso al
     *     salvataggio non riesce
     */
    public void continuaPartita() { gioco.continuaPartita(); }
    /**
     * Elimina lo slot persistente di salvataggio.
     *
     * @throws it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza se l’accesso al
     *     salvataggio non riesce
     */
    public void eliminaSalvataggio() { gioco.eliminaSalvataggio(); }

    private RiepilogoSalvataggio creaRiepilogo(ProgressiPartita progressi) {
        return new RiepilogoSalvataggio(progressi.nomeEroe(), progressi.codiceClasse(),
                progressi.codiciCastelliCompletati(), gioco.campagnaCompletata(progressi));
    }
}
