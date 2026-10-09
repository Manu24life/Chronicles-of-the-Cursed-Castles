package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro.Nemico;

import java.util.Objects;

/** Vista immutabile dello stato di un nemico esposta a Controller e View. */
public record StatoNemico(
        int indice,
        String nome,
        String codiceTipo,
        String tipo,
        int puntiVita,
        int puntiVitaMassimi,
        boolean vivo
) {

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
