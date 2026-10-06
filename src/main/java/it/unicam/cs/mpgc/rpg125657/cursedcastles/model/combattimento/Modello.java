package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento;

import lombok.NonNull;

public abstract class Modello {
    int puntiVita;
String nome;
public Modello(int puntiVita,  @NonNull String nome) {
if (puntiVita< 0)
    {
       throw new IllegalArgumentException("Punti vita invalido");
    }
    this.puntiVita = puntiVita;
    this.nome = nome;
}
}
