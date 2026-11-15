package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento.Combattente;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Objects;

/**
 * Gestisce esclusivamente identità e stato vitale di un combattente.
 */
@Getter
@Accessors(fluent = true)
public abstract class Personaggio implements Combattente {

    private final String nome;
    private final Statistiche statisticheBase;
    private int puntiVita;

    /**
     * Inizializza il combattente con il nome normalizzato e tutti i punti vita della configurazione.
     *
     * @param nome nome non vuoto del combattente
     * @param statistiche statistiche base immutabili del personaggio
     * @throws NullPointerException se almeno uno tra {@code nome}, {@code statistiche} è {@code null}
     * @throws IllegalArgumentException se il nome è vuoto
     */
    protected Personaggio(String nome, Statistiche statistiche) {
        Objects.requireNonNull(nome, "Il nome non può essere null");
        if (nome.isBlank()) {
            throw new IllegalArgumentException("Il nome non può essere vuoto");
        }
        this.nome = nome.strip();
        this.statisticheBase = Objects.requireNonNull(statistiche, "Le statistiche non possono essere null");
        this.puntiVita = statistiche.puntiVitaMassimi();
    }

    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException se il danno è negativo
     */
    @Override
    public final void subisciDanno(int danno) {
        if (danno < 0) {
            throw new IllegalArgumentException("Il danno non può essere negativo");
        }
        puntiVita = Math.max(0, puntiVita - danno);
    }

    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException se la quantità da recuperare è negativa
     * @throws IllegalStateException se la cura tenta di rianimare un personaggio sconfitto
     */
    @Override
    public final void recuperaPuntiVita(int puntiVita) {
        if (puntiVita < 0) {
            throw new IllegalArgumentException("La cura non può essere negativa");
        }
        if (!isVivo() && puntiVita > 0) {
            throw new IllegalStateException("La cura non può rianimare un personaggio sconfitto");
        }
        this.puntiVita = Math.min(statisticheBase.puntiVitaMassimi(), this.puntiVita + puntiVita);
    }
}
