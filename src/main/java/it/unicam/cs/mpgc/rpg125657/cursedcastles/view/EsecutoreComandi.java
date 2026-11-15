package it.unicam.cs.mpgc.rpg125657.cursedcastles.view;

import javafx.application.Platform;

import java.util.Objects;
import java.util.ServiceConfigurationError;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Serializza tutti gli accessi alla sessione; la scena riceve soltanto risultati immutabili.
 */
public final class EsecutoreComandi implements AutoCloseable {
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r ->
            new Thread(r, "cursed-castles-sessione"));
    private final AtomicBoolean chiuso = new AtomicBoolean();
    private final Consumer<Runnable> aggiornaInterfaccia;

    /**
     * Crea l’esecutore seriale dei comandi e la strategia di consegna degli aggiornamenti alla vista.
     */
    public EsecutoreComandi() {
        this(Platform::runLater);
    }

    EsecutoreComandi(Consumer<Runnable> aggiornaInterfaccia) {
        this.aggiornaInterfaccia = Objects.requireNonNull(aggiornaInterfaccia);
    }

    /**
     * Accoda un comando sul thread della sessione e consegna risultato o errore alla vista; ignora la richiesta se
     * l’esecutore è chiuso.
     *
     * @param <T> tipo del risultato del comando o del controller della vista
     * @param comando operazione da eseguire sul thread della sessione
     * @param successo azione che riceve il risultato sul thread di aggiornamento della vista
     * @param errore azione che riceve l'eccezione sul thread di aggiornamento della vista
     */
    public <T> void esegui(Callable<T> comando, Consumer<T> successo, Consumer<Exception> errore) {
        if (chiuso.get()) return;
        executor.execute(() -> {
            try {
                T risultato = comando.call();
                consegna(() -> successo.accept(risultato));
            } catch (Exception eccezione) {
                consegna(() -> errore.accept(eccezione));
            } catch (LinkageError | ServiceConfigurationError configurazione) {
                consegna(() -> errore.accept(new IllegalStateException(
                        "Impossibile inizializzare un componente. Verifica la configurazione del runtime.",
                        configurazione)));
            }
        });
    }

    private void consegna(Runnable aggiornamento) {
        if (!chiuso.get()) {
            aggiornaInterfaccia.accept(() -> {
                if (!chiuso.get()) aggiornamento.run();
            });
        }
    }

    /**
     * Impedisce nuove consegne alla vista e accoda il rilascio delle risorse dopo i comandi già inviati.
     *
     * @param chiusuraRisorse azione di rilascio delle risorse accodata dopo i comandi già inviati
     */
    public void chiudi(Runnable chiusuraRisorse) {
        if (chiuso.compareAndSet(false, true)) {
            executor.execute(chiusuraRisorse);
            executor.shutdown();
        }
    }

    /**
     * Chiude l’esecutore lasciando terminare i comandi già accodati, senza consegnare altri aggiornamenti alla
     * vista.
     *
     */
    @Override
    public void close() {
        chiudi(() -> { });
    }
}
