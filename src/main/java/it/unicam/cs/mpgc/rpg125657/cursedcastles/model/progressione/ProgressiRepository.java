package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.progressione;

import java.util.Optional;

/**
 * Rappresenta la porta di persistenza dell'unico slot, indipendente dalla tecnologia usata.
 */
public interface ProgressiRepository {

    /**
     * Legge lo slot di salvataggio, se presente.
     *
     * @return salvataggio presente, oppure {@link java.util.Optional#empty()} se lo slot è vuoto
     * @throws it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza se l’accesso al
     *     salvataggio non riesce
     */
    Optional<ProgressiPartita> carica();

    /**
     * Crea o sostituisce integralmente lo slot di salvataggio in una singola transazione.
     *
     * @param progressi dati persistenti della progressione
     * @throws it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza se l’accesso al
     *     salvataggio non riesce
     * @throws NullPointerException se i progressi sono null
     */
    void sostituisci(ProgressiPartita progressi);

    /**
     * Elimina lo slot di salvataggio se presente.
     * @throws it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza se l’accesso al
     *     salvataggio non riesce
     */
    void elimina();
}
