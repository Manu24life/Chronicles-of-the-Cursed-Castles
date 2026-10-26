package it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.hibernate;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.progressione.ProgressiPartita;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

import java.util.HashSet;
import java.util.Set;

/** Rappresentazione relazionale dell'unico slot di salvataggio. */
@Entity
@Table(name = "salvataggio")
final class SalvataggioEntity {

    static final long ID_SLOT_PRINCIPALE = 1L;

    @Id
    private Long id;

    @Column(name = "nome_eroe", nullable = false, length = 100)
    private String nomeEroe;

    @Column(name = "codice_classe", nullable = false, length = 64)
    private String codiceClasse;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "castello_completato",
            joinColumns = @JoinColumn(name = "salvataggio_id", nullable = false),
            uniqueConstraints = @UniqueConstraint(
                    name = "uk_salvataggio_castello",
                    columnNames = {"salvataggio_id", "codice_castello"}
            )
    )
    @Column(name = "codice_castello", nullable = false, length = 64)
    private Set<String> codiciCastelliCompletati = new HashSet<>();

    @Version
    @Column(name = "versione", nullable = false)
    private long versione;

    protected SalvataggioEntity() {
    }

    SalvataggioEntity(ProgressiPartita progressi) {
        id = ID_SLOT_PRINCIPALE;
        aggiornaDa(progressi);
    }

    void aggiornaDa(ProgressiPartita progressi) {
        nomeEroe = progressi.nomeEroe();
        codiceClasse = progressi.codiceClasse();
        codiciCastelliCompletati.clear();
        codiciCastelliCompletati.addAll(progressi.codiciCastelliCompletati());
    }

    ProgressiPartita aProgressi() {
        return new ProgressiPartita(nomeEroe, codiceClasse, codiciCastelliCompletati);
    }
}
