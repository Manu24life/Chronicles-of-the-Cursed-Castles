package it.unicam.cs.mpgc.rpg125657.cursedcastles.view;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.application.ServizioGioco;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.application.ServizioProgressione;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.controller.*;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello.FabbricaNemiciStandard;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.RisultatoAzione;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.StatoBattaglia;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.partita.StatoPartita;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.hibernate.HibernateProgressiRepository;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.hibernate.HibernateSessionFactory;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.view.schermate.SchermataBattaglia;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.view.schermate.SchermataMenu;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.view.schermate.SchermataNuovaPartita;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.view.grafica.RisorseBattaglia;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.hibernate.SessionFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Traduce gesti e navigazione in comandi esistenti; non contiene regole di combattimento.
 */
public final class CoordinatoreInterfaccia implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(CoordinatoreInterfaccia.class.getName());
    private final GestoreViste viste;
    private final Stage finestra;
    private final DialoghiGioco dialoghi;
    private final EsecutoreComandi esecutore = new EsecutoreComandi();
    private final PauseTransition attesaNemico = new PauseTransition(Duration.millis(650));
    private final ObservableList<String> registro = FXCollections.observableArrayList();
    // La SessionFactory viene creata e chiusa sul medesimo esecutore, anche chiudendo durante l'avvio.
    private SessionFactory factory;
    private MenuController menu;
    private BattagliaController battaglia;
    private StatoMenu statoMenu = new StatoMenu(List.of(), Optional.empty());
    private VistaPartita partita;
    private SchermataBattaglia vistaBattaglia;
    private boolean inBattaglia;
    private boolean occupato;
    private boolean chiuso;
    private boolean letturaFallita;

    /**
     * Inizializza un’istanza di {@code CoordinatoreInterfaccia} con le dipendenze ricevute.
     *
     * @param viste gestore del caricamento e della navigazione delle schermate
     * @param finestra finestra principale dell'applicazione
     */
    public CoordinatoreInterfaccia(GestoreViste viste, Stage finestra) {
        this.viste = viste;
        this.finestra = finestra;
        dialoghi = new DialoghiGioco(finestra);
        attesaNemico.setOnFinished(e -> eseguiBattaglia(battaglia::eseguiTurnoNemico));
    }

    /**
     * Avvia il coordinatore e presenta il menu, usando i controller forniti oppure inizializzando il database
     * locale in modo asincrono.
     *
     */
    public void avvia() {
        operazione("Aprendo il libro delle tracce…", () -> {
            RisorseBattaglia.precarica();
            if (factory != null) factory.close();
            String database = System.getProperty("cursedcastles.database");
            factory = database == null ? HibernateSessionFactory.apriPredefinita()
                    : HibernateSessionFactory.apri(Path.of(database));
            ServizioGioco gioco = new ServizioGioco(new ServizioProgressione(
                    new HibernateProgressiRepository(factory)), new FabbricaNemiciStandard());
            return new ControllerGioco(new MenuController(gioco), new BattagliaController(gioco));
        }, c -> avvia(c.menu(), c.battaglia()), errore -> {
            segnala(errore);
            viste.erroreAvvio("Non è stato possibile aprire il database locale. " + messaggio(errore),
                    this::avvia, this::esci);
        });
    }

    /**
     * Avvia il coordinatore e presenta il menu, usando i controller forniti oppure inizializzando il database
     * locale in modo asincrono.
     *
     * @param menu controller dei comandi del menu
     * @param battaglia controller dei comandi di combattimento
     */
    public void avvia(MenuController menu, BattagliaController battaglia) {
        this.menu = menu;
        this.battaglia = battaglia;
        mostraMenu();
    }

    /**
     * Aggiorna i dati del menu in modo asincrono e ne apre la schermata.
     *
     */
    public void mostraMenu() {
        if (occupato || chiuso) return;
        attesaNemico.stop();
        operazione("Leggendo le ultime tracce…", menu::stato, stato -> {
            statoMenu = stato;
            letturaFallita = false;
            apriMenu();
        }, errore -> {
            letturaFallita = true;
            apriMenu();
            segnala(errore);
            dialoghi.errore(messaggio(errore));
        });
    }

    private void apriMenu() {
        inBattaglia = false;
        viste.mostra("menu-view.fxml", new SchermataMenu(this)).mostra(statoMenu, letturaFallita);
    }

    /**
     * Verifica se lo snapshot corrente rappresenta una partita ancora in corso.
     *
     * @return {@code true} se esiste una partita in corso nello snapshot
     */
    public boolean puoRiprendere() {
        return partita != null && partita.statoPartita() == StatoPartita.IN_CORSO;
    }

    /**
     * Apre la schermata di creazione usando le classi disponibili nel menu.
     *
     */
    public void mostraCreazione() {
        if (occupato || chiuso) return;
        inBattaglia = false;
        viste.mostra("nuova-partita-view.fxml", new SchermataNuovaPartita(this))
                .mostra(statoMenu.classiDisponibili());
    }

    /**
     * Richiede la conferma di sostituzione quando necessaria e avvia la nuova partita in modo asincrono.
     *
     * @param nome nome visualizzato
     * @param classe codice della classe scelta dal giocatore
     */
    public void nuovaPartita(String nome, String classe) {
        if (occupato || chiuso) return;
        if ((statoMenu.salvataggio().isPresent() || letturaFallita || partita != null)
                && !dialoghi.conferma("Incidere un nuovo cammino?",
                "La nuova partita sostituirà l'unico salvataggio e l'eventuale scontro in corso.")) return;
        operazione("Varcando la soglia…", () -> {
            menu.nuovaPartita(nome, classe);
            return battaglia.stato();
        }, stato -> {
            registro.clear();
            aggiungiEvento("Il cammino di " + stato.eroe().nome() + " ha inizio.");
            aggiornaPartita(stato);
        }, this::mostraErrore);
    }

    /**
     * Riprende la partita in memoria, se disponibile; altrimenti ricostruisce la partita dal salvataggio.
     *
     */
    public void continua() {
        if (occupato || chiuso) return;
        if (puoRiprendere()) {
            aggiornaPartita(partita);
            return;
        }
        operazione("Ripercorrendo l'ultima traccia…", () -> {
            menu.continuaPartita();
            return battaglia.stato();
        }, stato -> {
            registro.clear();
            aggiungiEvento("Ripresa dal primo castello incompleto.");
            aggiornaPartita(stato);
        }, this::mostraErrore);
    }

    /**
     * Richiede conferma ed elimina il salvataggio e la sessione corrente in modo asincrono.
     *
     */
    public void elimina() {
        if (occupato || chiuso || !dialoghi.conferma("Cancellare l'ultima traccia?",
                "Il salvataggio e l'eventuale sessione corrente verranno eliminati. Questa azione è definitiva.")) return;
        operazione("Cancellando la traccia…", () -> {
            menu.eliminaSalvataggio();
            return menu.stato();
        }, stato -> {
            partita = null;
            registro.clear();
            statoMenu = stato;
            letturaFallita = false;
            apriMenu();
        }, this::mostraErrore);
    }

    /**
     * Esegue l’attacco dell’eroe contro il nemico selezionato.
     *
     * @param indice indice nella sequenza originale, a partire da zero
     */
    public void attacca(int indice) { eseguiBattaglia(() -> battaglia.attacca(indice)); }
    /**
     * Invia il comando di abilità usando il bersaglio solo quando richiesto dalla classe dell’eroe.
     *
     * @param indice indice nella sequenza originale, a partire da zero
     */
    public void abilita(int indice) {
        boolean bersaglio = partita.castello().orElseThrow().abilitaRichiedeBersaglio();
        eseguiBattaglia(() -> bersaglio ? battaglia.usaAbilita(indice) : battaglia.usaAbilita());
    }
    /**
     * Tenta la cura dell’eroe usando gli utilizzi disponibili nel castello.
     *
     */
    public void cura() { eseguiBattaglia(battaglia::cura); }

    private void eseguiBattaglia(Callable<EsitoComando> comando) {
        if (occupato || chiuso) return;
        operazione("", () -> {
            try {
                EsitoComando esito = comando.call();
                return new Aggiornamento(esito.stato(), esito.risultato(), null);
            } catch (Exception errore) {
                // Un commit fallito può seguire un'azione già eseguita: rileggere, mai ripetere l'attacco.
                return new Aggiornamento(battaglia.stato(), null, errore);
            }
        }, this::ricevi, this::mostraErrore);
    }

    /**
     * Ritenta il salvataggio della vittoria oppure avanza al castello successivo se la vittoria è già salvata.
     *
     */
    public void prosegui() {
        if (occupato || chiuso) return;
        StatoCastello castello = partita.castello().orElseThrow();
        operazione("Incidendo la vittoria…", () -> {
            try {
                VistaPartita stato = castello.vittoriaSalvata()
                        ? battaglia.vaiAlCastelloSuccessivo() : battaglia.salvaVittoria();
                return new Aggiornamento(stato, null, null);
            } catch (Exception errore) {
                return new Aggiornamento(battaglia.stato(), null, errore);
            }
        }, this::ricevi, this::mostraErrore);
    }

    private void ricevi(Aggiornamento aggiornamento) {
        if (aggiornamento.risultato() != null) aggiungiEvento(aggiornamento.risultato().descrizione());
        if (aggiornamento.errore() != null) {
            aggiungiEvento("Operazione non completata: " + messaggio(aggiornamento.errore()));
        }
        aggiornaPartita(aggiornamento.stato());
        if (aggiornamento.errore() != null) mostraErrore(aggiornamento.errore());
    }

    private void aggiornaPartita(VistaPartita stato) {
        String prima = partita == null ? "" : partita.castello().map(StatoCastello::codice).orElse("");
        partita = stato;
        stato.castello().filter(c -> !c.codice().equals(prima))
                .ifPresent(c -> aggiungiEvento("Ingresso: " + c.nome() + "."));
        if (!inBattaglia) {
            vistaBattaglia = viste.mostra("battaglia-view.fxml", new SchermataBattaglia(this, registro));
            inBattaglia = true;
        }
        vistaBattaglia.mostra(stato);
        attesaNemico.stop();
        if (stato.castello().filter(c -> c.statoBattaglia() == StatoBattaglia.TURNO_NEMICI).isPresent()) {
            attesaNemico.playFromStart();
        }
    }

    private void aggiungiEvento(String testo) {
        registro.addFirst(testo);
        if (registro.size() > 160) registro.removeLast();
    }

    private <T> void operazione(String messaggio, Callable<T> comando, Consumer<T> successo,
                               Consumer<Exception> errore) {
        occupato = true;
        // I comandi rapidi bloccano i gesti senza coprire lo scenario con un lampeggio.
        viste.occupato(true, messaggio);
        esecutore.esegui(comando, risultato -> {
            occupato = false;
            viste.occupato(false, "");
            successo.accept(risultato);
        }, eccezione -> {
            occupato = false;
            viste.occupato(false, "");
            errore.accept(eccezione);
        });
    }

    private void mostraErrore(Exception errore) {
        segnala(errore);
        dialoghi.errore(messaggio(errore));
    }

    private static void segnala(Exception errore) { LOG.log(Level.WARNING, "Operazione non completata", errore); }
    private static String messaggio(Exception errore) {
        return errore.getMessage() == null ? "Operazione non riuscita. Puoi riprovare." : errore.getMessage();
    }

    /**
     * Richiede la conferma per abbandonare uno scontro in corso e chiude la finestra.
     *
     */
    public void esci() {
        if (partita != null && partita.statoPartita() == StatoPartita.IN_CORSO
                && !dialoghi.conferma("Lasciare il cammino?",
                "Resteranno i progressi salvati all'ultima vittoria. Lo scontro in corso non verrà conservato.")) return;
        finestra.close();
    }

    /**
     * Arresta l’attesa del nemico e accoda la chiusura della fabbrica Hibernate dopo i comandi in corso.
     *
     */
    @Override
    public void close() {
        if (chiuso) return;
        chiuso = true;
        attesaNemico.stop();
        esecutore.chiudi(() -> { if (factory != null) factory.close(); });
    }

    private record ControllerGioco(MenuController menu, BattagliaController battaglia) { }
    private record Aggiornamento(VistaPartita stato, RisultatoAzione risultato, Exception errore) { }
}
