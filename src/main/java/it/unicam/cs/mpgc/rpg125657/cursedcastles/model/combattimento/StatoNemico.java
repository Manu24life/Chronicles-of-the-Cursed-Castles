package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro.Nemico;

import java.util.Objects;

/**
 * Rappresenta una vista immutabile dello stato di un nemico esposta a Controller e View.
 *
 * @param indice indice nella sequenza originale, a partire da zero
 * @param nome nome visualizzato
 * @param codiceTipo identificativo stabile del tipo di mostro
 * @param tipo nome visualizzato del tipo di mostro
 * @param puntiVita punti vita attuali
 * @param puntiVitaMassimi limite massimo dei punti vita, strettamente positivo
 * @param vivo indicatore coerente con la presenza di punti vita positivi
 */
public record StatoNemico(
        int indice,
        String nome,
        String codiceTipo,
        String tipo,
        int puntiVita,
        int puntiVitaMassimi,
        boolean vivo
) {

    /**
     * Inizializza i dati immutabili di {@code StatoNemico}, verificando i vincoli dei componenti.
     *
     * @param indice indice nella sequenza originale, a partire da zero
     * @param nome nome visualizzato
     * @param codiceTipo identificativo stabile del tipo di mostro
     * @param tipo nome visualizzato del tipo di mostro
     * @param puntiVita punti vita attuali
     * @param puntiVitaMassimi limite massimo dei punti vita, strettamente positivo
     * @param vivo indicatore coerente con la presenza di punti vita positivi
     * @throws NullPointerException se almeno uno tra {@code nome}, {@code codiceTipo}, {@code tipo} è {@code null}
     * @throws IllegalArgumentException se indice o punti vita non sono validi, oppure l’indicatore di vitalità è
     *     incoerente
     */
    public StatoNemico {
        if (indice < 0 || puntiVita < 0 || puntiVitaMassimi <= 0 || puntiVita > puntiVitaMassimi) {
            throw new IllegalArgumentException("Stato del nemico non valido");
        }
        Objects.requireNonNull(nome, "Il nome non può essere null");
        Objects.requireNonNull(codiceTipo, "Il codice del tipo non può essere null");
        Objects.requireNonNull(tipo, "Il tipo non può essere null");
        if (vivo != (puntiVita > 0)) {
            throw new IllegalArgumentException("Lo stato vivo deve essere coerente con i punti vita");
        }
    }

    /**
     * Copia i dati correnti del nemico in uno snapshot immutabile con il suo indice originale.
     *
     * @param indice indice nella sequenza originale, a partire da zero
     * @param nemico nemico o snapshot del nemico da rappresentare
     * @return snapshot immutabile dei dati ricevuti
     * @throws NullPointerException se almeno uno tra {@code nemico} è {@code null}
     */
    public static StatoNemico da(int indice, Nemico nemico) {
        Objects.requireNonNull(nemico, "Il nemico non può essere null");
        return new StatoNemico(
                indice,
                nemico.nome(),
                nemico.tipo().codice(),
                nemico.tipo().nome(),
                nemico.puntiVita(),
                nemico.statisticheBase().puntiVitaMassimi(),
                nemico.isVivo()
        );
    }
}
