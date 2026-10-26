module it.unicam.cs.mpgc.rpg125657.cursedcastles {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.naming;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires static lombok;

    exports it.unicam.cs.mpgc.rpg125657.cursedcastles;
    exports it.unicam.cs.mpgc.rpg125657.cursedcastles.model.progressione;
    exports it.unicam.cs.mpgc.rpg125657.cursedcastles.application;
    exports it.unicam.cs.mpgc.rpg125657.cursedcastles.model.castello;
    exports it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento;
    exports it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe;
    exports it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.abilita;
    exports it.unicam.cs.mpgc.rpg125657.cursedcastles.model.mostro;
    exports it.unicam.cs.mpgc.rpg125657.cursedcastles.model.partita;
    exports it.unicam.cs.mpgc.rpg125657.cursedcastles.model.personaggio;
    exports it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence;
    exports it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.hibernate;

    opens it.unicam.cs.mpgc.rpg125657.cursedcastles.controller to javafx.fxml;
    opens it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.hibernate to org.hibernate.orm.core;
}
