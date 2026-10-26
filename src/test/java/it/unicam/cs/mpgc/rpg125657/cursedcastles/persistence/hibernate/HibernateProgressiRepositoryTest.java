package it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.hibernate;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.progressione.ProgressiPartita;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HibernateProgressiRepositoryTest {

    private SessionFactory sessionFactory;
    private HibernateProgressiRepository repository;

    @BeforeEach
    void inizializzaDatabase() {
        String nomeDatabase = "progressi-" + UUID.randomUUID();
        sessionFactory = HibernateSessionFactory.apri(
                "jdbc:h2:mem:" + nomeDatabase + ";DB_CLOSE_DELAY=-1",
                "create-drop"
        );
        repository = new HibernateProgressiRepository(sessionFactory);
    }

    @AfterEach
    void chiudeDatabase() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
    void salvaECaricaNomeClasseECastelli() {
        ProgressiPartita progressi = new ProgressiPartita(
                "Merlino",
                "mago",
                Set.of("ossa", "ferro")
        );

        repository.sostituisci(progressi);

        assertEquals(progressi, repository.carica().orElseThrow());
    }

    @Test
    void sostituisceIntegralmenteLoSlotPrecedente() {
        repository.sostituisci(new ProgressiPartita(
                "Lancillotto",
                "cavaliere",
                Set.of("ossa")
        ));

        repository.sostituisci(ProgressiPartita.nuovi("Merlino", "mago"));

        ProgressiPartita caricato = repository.carica().orElseThrow();
        assertEquals("Merlino", caricato.nomeEroe());
        assertEquals("mago", caricato.codiceClasse());
        assertTrue(caricato.codiciCastelliCompletati().isEmpty());
    }

    @Test
    void unErroreDiScritturaNonModificaIlSalvataggioPrecedente() {
        ProgressiPartita precedente = new ProgressiPartita("Merlino", "mago", Set.of("ossa"));
        repository.sostituisci(precedente);

        // Il codice è formalmente valido, ma supera la dimensione della colonna SQL.
        ProgressiPartita troppoGrande = ProgressiPartita.nuovi("Altro", "a".repeat(65));
        assertThrows(EccezionePersistenza.class, () -> repository.sostituisci(troppoGrande));

        assertEquals(precedente, repository.carica().orElseThrow());
    }

    @Test
    void eliminaLoSlotInModoIdempotente() {
        repository.sostituisci(ProgressiPartita.nuovi("Merlino", "mago"));

        repository.elimina();
        repository.elimina();

        assertTrue(repository.carica().isEmpty());
    }
}
