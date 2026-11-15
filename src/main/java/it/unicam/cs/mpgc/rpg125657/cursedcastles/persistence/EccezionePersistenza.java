package it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence;

/**
 * Rappresenta un errore infrastrutturale esposto senza propagare dettagli specifici di Hibernate.
 */
public final class EccezionePersistenza extends RuntimeException {

    /**
     * Inizializza un’istanza di {@code EccezionePersistenza} con le dipendenze ricevute.
     *
     * @param messaggio testo del messaggio
     * @param causa errore che ha originato l'eccezione
     */
    public EccezionePersistenza(String messaggio, Throwable causa) {
        super(messaggio, causa);
    }
}
