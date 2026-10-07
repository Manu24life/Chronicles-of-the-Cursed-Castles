package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio;

/** Valori immutabili condivisi da tutte le istanze dello stesso archetipo. */
public record Statistiche(int puntiVitaMassimi, int attacco, int difesa) {

    public Statistiche {
        if (puntiVitaMassimi <= 0) {
            throw new IllegalArgumentException("I punti vita massimi devono essere positivi");
        }
        if (attacco < 0 || difesa < 0) {
            throw new IllegalArgumentException("Attacco e difesa non possono essere negativi");
        }
    }
}
