package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.progressione;

/**
 * Segnala un salvataggio incompatibile con la campagna configurata.
 */
public final class SalvataggioNonValidoException extends IllegalStateException {
    /**
     * Inizializza un’istanza di {@code SalvataggioNonValidoException} con le dipendenze ricevute.
     *
     * @param messaggio descrizione dell’incompatibilità del salvataggio
     */
    public SalvataggioNonValidoException(String messaggio) {
        super(messaggio);
    }

    /**
     * Inizializza un’istanza di {@code SalvataggioNonValidoException} con le dipendenze ricevute.
     *
     * @param messaggio descrizione dell’incompatibilità del salvataggio
     * @param causa errore che ha originato l'eccezione
     */
    public SalvataggioNonValidoException(String messaggio, Throwable causa) {
        super(messaggio, causa);
    }
}
