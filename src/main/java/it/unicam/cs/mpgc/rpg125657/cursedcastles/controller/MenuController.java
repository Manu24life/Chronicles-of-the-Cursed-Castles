package it.unicam.cs.mpgc.rpg125657.cursedcastles.controller;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello.Castelli;
import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello.Castello;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

/** Adatta i dati del modello ai controlli della schermata iniziale. */
public final class MenuController {

    @FXML
    private ListView<String> elencoCastelli;

    @FXML
    private void initialize() {
        Castelli.tutti().stream()
                .map(Castello::nome)
                .forEach(elencoCastelli.getItems()::add);
    }
}
