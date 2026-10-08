package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro.Mostro;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro.Nemico;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Implementazione che crea un nuovo mostro per ogni elemento del setup. */
public final class FabbricaNemiciStandard implements FabbricaNemici {

    @Override
    public List<Nemico> creaNemici(Castello castello) {
        Objects.requireNonNull(castello, "Il castello non può essere null");
        List<Nemico> nemici = new ArrayList<>();
        for (ConfigurazioneNemici configurazione : castello.configurazioni()) {
            aggiungiNemici(nemici, configurazione);
        }
        return List.copyOf(nemici);
    }

    private void aggiungiNemici(List<Nemico> nemici, ConfigurazioneNemici configurazione) {
        for (int indice = 1; indice <= configurazione.quantita(); indice++) {
            String nome = configurazione.quantita() == 1
                    ? configurazione.tipo().nome()
                    : configurazione.tipo().nome() + " " + indice;
            nemici.add(new Mostro(nome, configurazione.tipo()));
        }
    }
}
