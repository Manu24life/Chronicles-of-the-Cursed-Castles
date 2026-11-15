package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio;

/**
 * Rappresenta i valori immutabili condivisi da tutte le istanze dello stesso archetipo.
 *
 * @param puntiVitaMassimi limite massimo dei punti vita, strettamente positivo
 * @param attacco valore di attacco, non negativo
 * @param difesa valore base di difesa, non negativo
 */
public record Statistiche(int puntiVitaMassimi, int attacco, int difesa) {

    /**
     * Inizializza i dati immutabili di {@code Statistiche}, verificando i vincoli dei componenti.
     *
     * @param puntiVitaMassimi limite massimo dei punti vita, strettamente positivo
     * @param attacco valore di attacco, non negativo
     * @param difesa valore base di difesa, non negativo
     * @throws IllegalArgumentException se la vita massima non è positiva oppure attacco o difesa sono negativi
     */
    public Statistiche {
        if (puntiVitaMassimi <= 0) {
            throw new IllegalArgumentException("I punti vita massimi devono essere positivi");
        }
        if (attacco < 0 || difesa < 0) {
            throw new IllegalArgumentException("Attacco e difesa non possono essere negativi");
        }
    }
}
