package it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence;

/** Errore infrastrutturale esposto senza propagare dettagli specifici di Hibernate. */
public final class EccezionePersistenza extends RuntimeException {

    public EccezionePersistenza(String messaggio, Throwable causa) {
        super(messaggio, causa);
    }
}
