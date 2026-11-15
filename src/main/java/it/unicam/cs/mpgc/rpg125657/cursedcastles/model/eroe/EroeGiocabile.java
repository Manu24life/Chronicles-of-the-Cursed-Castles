package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.Personaggio;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita.AbilitaEroe;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.BonusStatistiche;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.ModificatoreStatistiche;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Objects;

/**
 * Rappresenta l’implementazione standard di un eroe controllato dal giocatore.
 */
@Accessors(fluent = true)
public final class EroeGiocabile extends Personaggio implements Eroe {

    @Getter
    private final ClasseEroe classe;
    @Getter
    private final AbilitaEroe abilita;
    private final BonusStatistiche bonusStatistiche = new BonusStatistiche();

    /**
     * Crea l’eroe con vita piena e bonus inizialmente nulli, verificando l’abilità della classe.
     *
     * @param nome nome non vuoto dell’eroe
     * @param classe archetipo con le statistiche base
     * @param abilita abilità compatibile con l’archetipo
     * @throws NullPointerException se un argomento è null
     * @throws IllegalArgumentException se il nome è vuoto o l’abilità non è compatibile
     */
    EroeGiocabile(String nome, ClasseEroe classe, AbilitaEroe abilita) {
        super(nome, Objects.requireNonNull(classe, "La classe non può essere null").statistiche());
        this.classe = classe;
        this.abilita = Objects.requireNonNull(abilita, "L'abilità non può essere null");
        if (!classe.isAbilitaCompatibile(abilita)) {
            throw new IllegalArgumentException("L'abilità non è compatibile con la classe " + classe.nome());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int attacco() {
        return bonusStatistiche.calcolaAttacco(statisticheBase().attacco());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int difesa() {
        return bonusStatistiche.calcolaDifesa(statisticheBase().difesa());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applicaBonus(ModificatoreStatistiche modificatore) {
        bonusStatistiche.applica(modificatore);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int bonusAttaccoPercentuale() {
        return bonusStatistiche.attaccoPercentuale();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int bonusDifesaPercentuale() {
        return bonusStatistiche.difesaPercentuale();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void azzeraBonus() {
        bonusStatistiche.reset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void avanzaTurno() {
        abilita.avanzaTurno();
    }
}
