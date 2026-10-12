package it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.hibernate;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.progressione.ProgressiPartita;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.progressione.ProgressiRepository;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/** Implementazione Hibernate del repository dello slot principale. */
public final class HibernateProgressiRepository implements ProgressiRepository {

    private final SessionFactory sessionFactory;

    public HibernateProgressiRepository(SessionFactory sessionFactory) {
        this.sessionFactory = Objects.requireNonNull(
                sessionFactory,
                "La SessionFactory non può essere null"
        );
    }

    @Override
    public Optional<ProgressiPartita> carica() {
        return inTransazione("Impossibile caricare il salvataggio", session -> {
            SalvataggioEntity entita = session.find(
                    SalvataggioEntity.class,
                    SalvataggioEntity.ID_SLOT_PRINCIPALE
            );
            return Optional.ofNullable(entita).map(SalvataggioEntity::aProgressi);
        });
    }

    @Override
    public void sostituisci(ProgressiPartita progressi) {
        Objects.requireNonNull(progressi, "I progressi non possono essere null");
        inTransazione("Impossibile salvare la progressione", session -> {
            SalvataggioEntity entita = session.find(
                    SalvataggioEntity.class,
                    SalvataggioEntity.ID_SLOT_PRINCIPALE
            );
            if (entita == null) {
                session.persist(new SalvataggioEntity(progressi));
            } else {
                entita.aggiornaDa(progressi);
            }
            return null;
        });
    }

    @Override
    public void elimina() {
        inTransazione("Impossibile eliminare il salvataggio", session -> {
            SalvataggioEntity entita = session.find(
                    SalvataggioEntity.class,
                    SalvataggioEntity.ID_SLOT_PRINCIPALE
            );
            if (entita != null) {
                session.remove(entita);
            }
            return null;
        });
    }

    private <T> T inTransazione(String messaggioErrore, Function<Session, T> operazione) {
        Transaction transazione = null;
        try (Session sessione = sessionFactory.openSession()) {
            transazione = sessione.beginTransaction();
            T risultato = operazione.apply(sessione);
            transazione.commit();
            return risultato;
        } catch (RuntimeException errore) {
            eseguiRollback(transazione, errore);
            if (errore instanceof EccezionePersistenza eccezionePersistenza) {
                throw eccezionePersistenza;
            }
            throw new EccezionePersistenza(messaggioErrore, errore);
        }
    }

    private void eseguiRollback(Transaction transazione, RuntimeException erroreOriginale) {
        if (transazione == null || !transazione.isActive()) {
            return;
        }
        try {
            transazione.rollback();
        } catch (RuntimeException erroreRollback) {
            erroreOriginale.addSuppressed(erroreRollback);
        }
    }
}
