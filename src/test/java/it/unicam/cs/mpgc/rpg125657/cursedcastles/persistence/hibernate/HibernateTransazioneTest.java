package it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.hibernate;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HibernateTransazioneTest {
    @Test
    void esegueRollbackPrimaDiChiudereLaSessioneSuErroreOperazione() {
        verificaRollback(false, false);
    }

    @Test
    void esegueRollbackPrimaDiChiudereLaSessioneSuErroreCommit() {
        verificaRollback(true, false);
    }

    @Test
    void conservaErroreOriginaleSeAncheIlRollbackFallisce() {
        verificaRollback(false, true);
    }

    private void verificaRollback(boolean erroreCommit, boolean erroreRollback) {
        List<String> eventi = new ArrayList<>();
        RuntimeException originale = new IllegalStateException("Errore originale");
        RuntimeException rollback = new IllegalStateException("Errore rollback");
        Transaction transazione = proxy(Transaction.class, (p, metodo, args) -> {
            switch (metodo.getName()) {
                case "commit": throw originale;
                case "isActive": return true;
                case "rollback":
                    eventi.add("rollback");
                    if (erroreRollback) throw rollback;
                    return null;
                default: throw new UnsupportedOperationException(metodo.getName());
            }
        });
        Session sessione = proxy(Session.class, (p, metodo, args) -> {
            switch (metodo.getName()) {
                case "beginTransaction": return transazione;
                case "find":
                    if (!erroreCommit) throw originale;
                    return null;
                case "close": eventi.add("close"); return null;
                default: throw new UnsupportedOperationException(metodo.getName());
            }
        });
        SessionFactory factory = proxy(SessionFactory.class, (p, metodo, args) -> {
            if (metodo.getName().equals("openSession")) return sessione;
            throw new UnsupportedOperationException(metodo.getName());
        });
        EccezionePersistenza errore = assertThrows(EccezionePersistenza.class,
                () -> new HibernateProgressiRepository(factory).carica());
        assertSame(originale, errore.getCause());
        assertEquals(List.of("rollback", "close"), eventi);
        if (erroreRollback) assertArrayEquals(new Throwable[]{rollback}, originale.getSuppressed());
    }

    private static <T> T proxy(Class<T> tipo, InvocationHandler handler) {
        return tipo.cast(Proxy.newProxyInstance(tipo.getClassLoader(), new Class<?>[]{tipo}, handler));
    }
}
