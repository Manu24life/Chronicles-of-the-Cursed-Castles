package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.progressione;

/** Il salvataggio non è compatibile con la campagna configurata. */
public final class SalvataggioNonValidoException extends IllegalStateException {
    public SalvataggioNonValidoException(String messaggio) {
        super(messaggio);
    }

    public SalvataggioNonValidoException(String messaggio, Throwable causa) {
        super(messaggio, causa);
    }
}
