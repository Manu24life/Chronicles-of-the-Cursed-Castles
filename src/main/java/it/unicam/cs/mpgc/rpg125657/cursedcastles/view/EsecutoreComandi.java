package it.unicam.cs.mpgc.rpg125657.cursedcastles.view;

import javafx.application.Platform;

import java.util.Objects;
import java.util.ServiceConfigurationError;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/** Serializza tutti gli accessi alla sessione; la scena riceve soltanto risultati immutabili. */
public final class EsecutoreComandi implements AutoCloseable {
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r ->
            new Thread(r, "cursed-castles-sessione"));
    private final AtomicBoolean chiuso = new AtomicBoolean();
    private final Consumer<Runnable> aggiornaInterfaccia;

    public EsecutoreComandi() {
        this(Platform::runLater);
    }

    EsecutoreComandi(Consumer<Runnable> aggiornaInterfaccia) {
        this.aggiornaInterfaccia = Objects.requireNonNull(aggiornaInterfaccia);
    }

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

    /** La chiusura delle risorse viene accodata dopo gli eventuali commit in corso. */
    public void chiudi(Runnable chiusuraRisorse) {
        if (chiuso.compareAndSet(false, true)) {
            executor.execute(chiusuraRisorse);
            executor.shutdown();
        }
    }

    @Override
    public void close() {
        chiudi(() -> { });
    }
}
