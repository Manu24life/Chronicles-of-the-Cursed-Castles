package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.Personaggio;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita.AbilitaEroe;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.BonusStatistiche;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio.ModificatoreStatistiche;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Objects;

/** Implementazione standard di un eroe controllato dal giocatore. */
@Accessors(fluent = true)
public final class EroeGiocabile extends Personaggio implements Eroe {

    @Getter
    private final ClasseEroe classe;
    @Getter
    private final AbilitaEroe abilita;
    private final BonusStatistiche bonusStatistiche = new BonusStatistiche();

    public EroeGiocabile(String nome, ClasseEroe classe, AbilitaEroe abilita) {
        super(nome, Objects.requireNonNull(classe, "La classe non può essere null").statistiche());
        this.classe = classe;
        this.abilita = Objects.requireNonNull(abilita, "L'abilità non può essere null");
    }

    @Override
    public int attacco() {
        return bonusStatistiche.calcolaAttacco(statisticheBase().attacco());
    }

    @Override
    public int difesa() {
        return bonusStatistiche.calcolaDifesa(statisticheBase().difesa());
    }

    @Override
    public void applicaBonus(ModificatoreStatistiche modificatore) {
        bonusStatistiche.applica(modificatore);
    }

    @Override
    public int bonusAttaccoPercentuale() {
        return bonusStatistiche.attaccoPercentuale();
    }

    @Override
    public int bonusDifesaPercentuale() {
        return bonusStatistiche.difesaPercentuale();
    }

    @Override
    public void avanzaTurno() {
        abilita.avanzaTurno();
    }
}
