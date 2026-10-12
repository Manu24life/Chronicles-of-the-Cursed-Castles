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

/** Punto centralizzato di configurazione e creazione della SessionFactory. */
public final class HibernateSessionFactory {

    private static final String CARTELLA_DATI = ".cursed-castles";
    private static final String NOME_DATABASE = "salvataggio";

    private HibernateSessionFactory() {
    }

    /** Apre il database locale predefinito nella cartella dell'utente. */
    public static SessionFactory apriPredefinita() {
        Path percorso = Path.of(System.getProperty("user.home"), CARTELLA_DATI, NOME_DATABASE);
        return apri(percorso);
    }

    /** Apre un database H2 persistente e crea la directory necessaria. */
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

    /** Metodo configurabile usato anche dai test con un database H2 in memoria. */
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
