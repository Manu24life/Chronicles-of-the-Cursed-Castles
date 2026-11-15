package it.unicam.cs.mpgc.rpg125657.cursedcastles.view;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class EsecutoreComandiTest {
    @Test
    void serializzaComandiEChiudeLeRisorseDopoLeScritture() throws Exception {
        List<String> ordine = new ArrayList<>();
        CountDownLatch finito = new CountDownLatch(1);
        EsecutoreComandi esecutore = new EsecutoreComandi(Runnable::run);
        esecutore.esegui(() -> { ordine.add("scrittura"); return 1; }, valore -> { }, e -> fail(e));
        esecutore.esegui(() -> { ordine.add("lettura"); return 2; }, valore -> { }, e -> fail(e));
        esecutore.chiudi(() -> { ordine.add("chiusura"); finito.countDown(); });
        assertTrue(finito.await(5, TimeUnit.SECONDS));
        assertEquals(List.of("scrittura", "lettura", "chiusura"), ordine);
    }

    @Test
    void nonConsegnaRisultatiAUnaFinestraGiaChiusa() throws Exception {
        ConcurrentLinkedQueue<Runnable> aggiornamenti = new ConcurrentLinkedQueue<>();
        CountDownLatch accodato = new CountDownLatch(1);
        EsecutoreComandi esecutore = new EsecutoreComandi(r -> { aggiornamenti.add(r); accodato.countDown(); });
        boolean[] consegnato = {false};
        esecutore.esegui(() -> 42, valore -> consegnato[0] = true, e -> fail(e));
        assertTrue(accodato.await(5, TimeUnit.SECONDS));
        esecutore.close();
        aggiornamenti.forEach(Runnable::run);
        assertFalse(consegnato[0]);
    }

    @Test
    void consegnaIlFallimentoSenzaBloccareIlComandoSuccessivo() throws Exception {
        AtomicReference<Exception> fallimento = new AtomicReference<>();
        CountDownLatch completato = new CountDownLatch(1);
        try (EsecutoreComandi esecutore = new EsecutoreComandi(Runnable::run)) {
            esecutore.esegui(() -> { throw new IllegalStateException("Disco non disponibile"); },
                    valore -> fail("Il comando deve fallire"), fallimento::set);
            esecutore.esegui(() -> 1, valore -> completato.countDown(), e -> fail(e));
            assertTrue(completato.await(5, TimeUnit.SECONDS));
            assertInstanceOf(IllegalStateException.class, fallimento.get());
        }
    }

    @Test
    void segnalaAncheGliErroriDiConfigurazioneDeiServiziModulari() throws Exception {
        AtomicReference<Exception> fallimento = new AtomicReference<>();
        CountDownLatch consegnato = new CountDownLatch(1);
        try (EsecutoreComandi esecutore = new EsecutoreComandi(Runnable::run)) {
            esecutore.esegui(() -> { throw new java.util.ServiceConfigurationError("SPI non dichiarato"); },
                    valore -> fail("L'inizializzazione deve fallire"), e -> {
                        fallimento.set(e);
                        consegnato.countDown();
                    });
            assertTrue(consegnato.await(5, TimeUnit.SECONDS));
            assertInstanceOf(IllegalStateException.class, fallimento.get());
            assertInstanceOf(java.util.ServiceConfigurationError.class, fallimento.get().getCause());
        }
    }
}
