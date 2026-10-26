package it.unicam.cs.mpgc.rpg125657.cursedcastles.application;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.progressione.ProgressiPartita;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello.Castelli;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello.Castello;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello.ConfigurazioneNemici;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello.FabbricaNemici;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello.FabbricaNemiciStandard;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.RicaricaATurni;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.StatoBattaglia;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.ClasseEroe;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.ClassiEroe;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.Eroe;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.FabbricaEroiStandard;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita.TempraDelCavaliere;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro.TipoMostro;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro.Mostro;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.partita.Partita;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.partita.StatoPartita;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.ModificatoreStatistiche;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.Statistiche;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.hibernate.HibernateProgressiRepository;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.hibernate.HibernateSessionFactory;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServizioProgressioneTest {

    private static final FabbricaNemici NEMICI_DI_TEST = castello -> List.of(
            new Mostro("Bersaglio", new TipoMostro("bersaglio", "Bersaglio", new Statistiche(1, 0, 0)))
    );

    private SessionFactory sessionFactory;
    private HibernateProgressiRepository repository;

    @BeforeEach
    void inizializzaDatabase() {
        sessionFactory = HibernateSessionFactory.apri(
                "jdbc:h2:mem:servizio-" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1",
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
    void salvaClasseNomeEPiuCastelliSenzaDuplicati() {
        ServizioProgressione servizio = new ServizioProgressione(repository);
        Partita partita = servizio.nuovaPartita("Lancillotto", ClassiEroe.CAVALIERE, NEMICI_DI_TEST);

        vinciBattaglia(partita);
        servizio.registraVittoria(partita);
        servizio.registraVittoria(partita);
        partita.avanzaAlProssimoCastello();
        vinciBattaglia(partita);
        servizio.registraVittoria(partita);

        ProgressiPartita caricati = servizio.carica().orElseThrow();
        assertEquals("Lancillotto", caricati.nomeEroe());
        assertEquals("cavaliere", caricati.codiceClasse());
        assertEquals(2, caricati.codiciCastelliCompletati().size());
        assertTrue(caricati.codiciCastelliCompletati().containsAll(List.of("ossa", "ferro")));
    }

    @Test
    void unaNuovaPartitaCancellaLaProgressionePrecedente() {
        ServizioProgressione servizio = new ServizioProgressione(repository);
        Partita partita = servizio.nuovaPartita("Lancillotto", ClassiEroe.CAVALIERE, NEMICI_DI_TEST);
        vinciBattaglia(partita);
        servizio.registraVittoria(partita);

        servizio.nuovaPartita("Merlino", ClassiEroe.MAGO, new FabbricaNemiciStandard());

        ProgressiPartita caricati = servizio.carica().orElseThrow();
        assertEquals("Merlino", caricati.nomeEroe());
        assertEquals("mago", caricati.codiceClasse());
        assertTrue(caricati.codiciCastelliCompletati().isEmpty());
    }

    @Test
    void ricostruisceDalPrimoCastelloIncompletoSenzaStatoTemporaneo() {
        ServizioProgressione servizio = new ServizioProgressione(repository);
        Partita originale = servizio.nuovaPartita("Lancillotto", ClassiEroe.CAVALIERE, NEMICI_DI_TEST);
        Eroe eroeOriginale = originale.eroe();
        eroeOriginale.subisciDanno(30);
        eroeOriginale.applicaBonus(new ModificatoreStatistiche(30, 40));
        eroeOriginale.abilita().usa(eroeOriginale, eroeOriginale);
        assertFalse(eroeOriginale.abilita().isDisponibile());

        vinciBattaglia(originale);
        servizio.registraVittoria(originale);
        ProgressiPartita ricaricati = servizio.carica().orElseThrow();
        Partita ripresa = servizio.ricostruisciPartita(
                ricaricati,
                new FabbricaNemiciStandard()
        ).orElseThrow();
        Eroe eroeRipristinato = ripresa.eroe();

        assertNotSame(eroeOriginale, eroeRipristinato);
        assertEquals(Castelli.FERRO, ripresa.castelloCorrente());
        assertEquals(eroeRipristinato.statisticheBase().puntiVitaMassimi(), eroeRipristinato.puntiVita());
        assertEquals(0, eroeRipristinato.bonusAttaccoPercentuale());
        assertEquals(0, eroeRipristinato.bonusDifesaPercentuale());
        assertEquals(0, eroeRipristinato.abilita().turniRimanenti());
        assertEquals(2, ripresa.battagliaCorrente().cureRimanenti());
    }

    @Test
    void rifiutaIlCompletamentoFuoriOrdine() {
        ServizioProgressione servizio = new ServizioProgressione(repository);
        servizio.nuovaPartita("Merlino", ClassiEroe.MAGO, NEMICI_DI_TEST);
        Partita fuoriOrdine = new Partita(
                new FabbricaEroiStandard().crea("Merlino", ClassiEroe.MAGO),
                Castelli.tutti(),
                NEMICI_DI_TEST
        );
        fuoriOrdine.iniziaDa(1);
        vinciBattaglia(fuoriOrdine);

        assertThrows(
                IllegalStateException.class,
                () -> servizio.registraVittoria(fuoriOrdine)
        );
        assertTrue(servizio.carica().orElseThrow().codiciCastelliCompletati().isEmpty());
    }

    @Test
    void rifiutaLaRegistrazionePrimaDellaVittoriaSenzaModificareIProgressi() {
        ServizioProgressione servizio = new ServizioProgressione(repository);
        Partita partita = servizio.nuovaPartita("Merlino", ClassiEroe.MAGO, NEMICI_DI_TEST);

        assertThrows(IllegalStateException.class, () -> servizio.registraVittoria(partita));
        assertTrue(servizio.carica().orElseThrow().codiciCastelliCompletati().isEmpty());
    }

    @Test
    void registraLaVittoriaPrimaDiCompletareLaPartita() {
        ClasseEroe classe = new ClasseEroe(
                "eroe-test",
                "Eroe test",
                new Statistiche(100, 100, 0),
                () -> new TempraDelCavaliere(new RicaricaATurni(1, 0))
        );
        TipoMostro tipo = new TipoMostro("bersaglio", "Bersaglio", new Statistiche(1, 0, 0));
        Castello castello = new Castello(
                "prova",
                "Castello di prova",
                List.of(new ConfigurazioneNemici(tipo, 1))
        );
        ServizioProgressione servizio = new ServizioProgressione(
                repository,
                new FabbricaEroiStandard(),
                List.of(classe),
                List.of(castello)
        );
        Partita partita = servizio.nuovaPartita("Eroe", classe, new FabbricaNemiciStandard());

        partita.battagliaCorrente().attacca(0);
        assertEquals(StatoBattaglia.VITTORIA, partita.battagliaCorrente().stato());
        servizio.registraVittoria(partita);
        partita.avanzaAlProssimoCastello();

        assertEquals(StatoPartita.COMPLETATA, partita.stato());
        assertTrue(servizio.carica().orElseThrow().isCastelloCompletato("prova"));
        assertTrue(servizio.ricostruisciPartita(
                servizio.carica().orElseThrow(),
                new FabbricaNemiciStandard()
        ).isEmpty());
    }

    private static void vinciBattaglia(Partita partita) {
        partita.battagliaCorrente().attacca(0);
        assertEquals(StatoBattaglia.VITTORIA, partita.battagliaCorrente().stato());
    }
}
