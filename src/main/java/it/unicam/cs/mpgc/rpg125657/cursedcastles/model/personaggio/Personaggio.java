package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.Combattente;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Objects;

/** Gestisce esclusivamente identità e stato vitale di un combattente. */
@Getter
@Accessors(fluent = true)
public abstract class Personaggio implements Combattente {

    private final String nome;
    private final Statistiche statisticheBase;
    private int puntiVita;

    protected Personaggio(String nome, Statistiche statistiche) {
        Objects.requireNonNull(nome, "Il nome non può essere null");
        if (nome.isBlank()) {
            throw new IllegalArgumentException("Il nome non può essere vuoto");
        }
        this.nome = nome.strip();
        this.statisticheBase = Objects.requireNonNull(statistiche, "Le statistiche non possono essere null");
        this.puntiVita = statistiche.puntiVitaMassimi();
    }

    @Override
    public final void subisciDanno(int danno) {
        if (danno < 0) {
            throw new IllegalArgumentException("Il danno non può essere negativo");
        }
        puntiVita = Math.max(0, puntiVita - danno);
    }

    @Override
    public final void recuperaPuntiVita(int puntiVita) {
        if (puntiVita < 0) {
            throw new IllegalArgumentException("La cura non può essere negativa");
        }
        this.puntiVita = Math.min(statisticheBase.puntiVitaMassimi(), this.puntiVita + puntiVita);
    }
}
