package it.unicam.cs.mpgc.rpg125657.cursedcastles.application;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello.FabbricaNemici;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.Battaglia;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.RisultatoAzione;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.StatoBattaglia;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.ClasseEroe;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.partita.Partita;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.progressione.ProgressiPartita;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/** Unico coordinatore della sessione runtime e dei suoi checkpoint persistenti. */
public final class ServizioGioco {
    private final ServizioProgressione progressione;
    private final FabbricaNemici fabbricaNemici;
    private final SessioneGioco sessione = new SessioneGioco();
    private boolean vittoriaSalvata;

    public ServizioGioco(ServizioProgressione progressione, FabbricaNemici fabbricaNemici) {
        this.progressione = Objects.requireNonNull(progressione);
        this.fabbricaNemici = Objects.requireNonNull(fabbricaNemici);
    }

    public List<ClasseEroe> classiDisponibili() { return progressione.classiDisponibili(); }
    public Optional<ProgressiPartita> carica() { return progressione.carica(); }
    public boolean campagnaCompletata(ProgressiPartita progressi) {
        return progressione.campagnaCompletata(progressi);
    }
    public Optional<Partita> partita() { return sessione.partita(); }
    public Partita richiediPartita() { return sessione.richiediPartita(); }
    public boolean vittoriaSalvata() { return vittoriaSalvata; }

    public void nuovaPartita(String nome, String codiceClasse) {
        Objects.requireNonNull(codiceClasse, "Il codice della classe non può essere null");
        ClasseEroe classe = classiDisponibili().stream()
                .filter(c -> c.codice().equals(codiceClasse))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Classe sconosciuta: " + codiceClasse));
        Partita candidata = progressione.nuovaPartita(nome, classe, fabbricaNemici);
        sessione.imposta(candidata);
        vittoriaSalvata = false;
    }

    public void continuaPartita() {
        ProgressiPartita progressi = carica().orElseThrow(
                () -> new IllegalStateException("Non esiste un salvataggio da caricare"));
        Partita candidata = progressione.ricostruisciPartita(progressi, fabbricaNemici)
                .orElseThrow(() -> new IllegalStateException("La campagna salvata è già completata"));
        sessione.imposta(candidata);
        vittoriaSalvata = false;
    }

    public void eliminaSalvataggio() {
        progressione.eliminaSalvataggio();
        sessione.termina();
        vittoriaSalvata = false;
    }

    public RisultatoAzione attacca(int indice) { return esegui(b -> b.attacca(indice)); }
    public RisultatoAzione usaAbilita(int indice) { return esegui(b -> b.usaAbilitaEroe(indice)); }
    public RisultatoAzione usaAbilita() { return esegui(Battaglia::usaAbilitaEroe); }
    public RisultatoAzione cura() { return esegui(Battaglia::curaEroe); }
    public RisultatoAzione eseguiTurnoNemico() { return esegui(Battaglia::eseguiTurnoNemico); }

    private RisultatoAzione esegui(Function<Battaglia, RisultatoAzione> comando) {
        Battaglia battaglia = richiediPartita().battagliaCorrente();
        RisultatoAzione risultato = comando.apply(battaglia);
        if (battaglia.stato() == StatoBattaglia.VITTORIA) salvaVittoria();
        return risultato;
    }

    /** Un fallimento di persistenza lascia la vittoria disponibile per un nuovo tentativo. */
    public void salvaVittoria() {
        Partita partita = richiediPartita();
        if (partita.battagliaCorrente().stato() != StatoBattaglia.VITTORIA) {
            throw new IllegalStateException("La battaglia corrente non è stata vinta");
        }
        if (!vittoriaSalvata) {
            progressione.registraVittoria(partita);
            vittoriaSalvata = true;
        }
    }

    public void vaiAlCastelloSuccessivo() {
        salvaVittoria();
        richiediPartita().avanzaAlProssimoCastello();
        vittoriaSalvata = false;
    }
}
