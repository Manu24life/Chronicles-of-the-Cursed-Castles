package it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.hibernate;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.EccezionePersistenza;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Rappresenta il punto centralizzato di configurazione e creazione della SessionFactory.
 */
public final class HibernateSessionFactory {

    private static final String CARTELLA_DATI = ".cursed-castles";
    private static final String NOME_DATABASE = "salvataggio";

    private HibernateSessionFactory() {
    }

    /**
     * Apre il database H2 nella cartella {@code .cursed-castles} dell’utente; la fabbrica restituita deve essere
     * chiusa dal chiamante.
     *
     * @return fabbrica di sessioni da chiudere dopo l’uso
     */
    public static SessionFactory apriPredefinita() {
        Path percorso = Path.of(System.getProperty("user.home"), CARTELLA_DATI, NOME_DATABASE);
        return apri(percorso);
    }

    /**
     * Apre una fabbrica di sessioni H2 configurata per il database indicato; il chiamante ne gestisce la chiusura.
     *
     * @param percorsoDatabase percorso del database H2 senza estensione
     * @return fabbrica di sessioni da chiudere dopo l’uso
     * @throws NullPointerException se almeno uno tra {@code percorsoDatabase} è {@code null}
     * @throws EccezionePersistenza se la directory del database non può essere creata o Hibernate non può essere
     *     inizializzato
     */
    public static SessionFactory apri(Path percorsoDatabase) {
        Objects.requireNonNull(percorsoDatabase, "Il percorso del database non può essere null");
        Path assoluto = percorsoDatabase.toAbsolutePath().normalize();
        Path cartella = assoluto.getParent();
        try {
            if (cartella != null) {
                Files.createDirectories(cartella);
            }
        } catch (IOException errore) {
            throw new EccezionePersistenza("Impossibile creare la cartella del database", errore);
        }
        String percorsoH2 = assoluto.toString().replace('\\', '/');
        return apri("jdbc:h2:file:" + percorsoH2, "update");
    }

    /**
     * Apre una fabbrica di sessioni H2 configurata per il database indicato; il chiamante ne gestisce la chiusura.
     *
     * @param jdbcUrl URL JDBC del database H2
     * @param gestioneSchema modalità Hibernate di gestione dello schema, ad esempio {@code update} o
     *     {@code create-drop}
     * @return fabbrica di sessioni da chiudere dopo l’uso
     * @throws NullPointerException se almeno uno tra {@code jdbcUrl}, {@code gestioneSchema} è {@code null}
     * @throws IllegalArgumentException se URL JDBC o modalità di gestione dello schema sono vuoti
     * @throws EccezionePersistenza se la directory del database non può essere creata o Hibernate non può essere
     *     inizializzato
     */
    public static SessionFactory apri(String jdbcUrl, String gestioneSchema) {
        Objects.requireNonNull(jdbcUrl, "L'URL JDBC non può essere null");
        Objects.requireNonNull(gestioneSchema, "La gestione dello schema non può essere null");
        if (jdbcUrl.isBlank() || gestioneSchema.isBlank()) {
            throw new IllegalArgumentException("URL JDBC e gestione schema non possono essere vuoti");
        }

        Map<String, Object> impostazioni = new HashMap<>();
        impostazioni.put("hibernate.connection.driver_class", "org.h2.Driver");
        impostazioni.put("hibernate.connection.url", jdbcUrl);
        impostazioni.put("hibernate.connection.username", "sa");
        impostazioni.put("hibernate.connection.password", "");
        impostazioni.put("hibernate.hbm2ddl.auto", gestioneSchema);
        impostazioni.put("hibernate.show_sql", false);
        impostazioni.put("hibernate.format_sql", false);

        StandardServiceRegistry registro = new StandardServiceRegistryBuilder()
                .applySettings(impostazioni)
                .build();
        try {
            return new MetadataSources(registro)
                    .addAnnotatedClass(SalvataggioEntity.class)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (RuntimeException errore) {
            StandardServiceRegistryBuilder.destroy(registro);
            throw new EccezionePersistenza("Impossibile inizializzare Hibernate", errore);
        }
    }
}
