# Strumenti e Metodologie

## Stack del progetto

Le versioni riportate sono quelle dichiarate nella configurazione del repository.

| Strumento | Versione | Utilizzo |
| --- | --- | --- |
| Java | 25 | Linguaggio, toolchain e sistema di moduli JPMS. |
| Gradle Wrapper | 9.1.0 | Build riproducibile, dipendenze ed esecuzione dei task. |
| JavaFX | 25.0.3 | Controlli, FXML e interfaccia desktop; modulo media richiesto da FXGL Core. |
| FXGL Core | 25.0.1 | Texture e animazioni nella vista JavaFX. |
| Hibernate ORM | 7.4.12.Final | Mapping delle entità e gestione delle sessioni e transazioni. |
| H2 | 2.5.250 | Database embedded su file e database dedicati nei test. |
| JUnit Jupiter | 5.12.1 | Test automatizzati. |
| Lombok | 1.18.48 | Generazione a compilazione di metodi e supporto ai cataloghi di utilità. |
| Plugin OpenJFX | 0.1.0 | Configurazione JavaFX nella build Gradle. |
| Plugin Beryx jlink | 4.0.2 | Creazione del runtime autonomo. |
| Git e GitHub | — | Versionamento, repository pubblico e wiki. |

La build è scritta in Kotlin DSL. Lombok è configurato come dipendenza di compilazione e annotation processor. Le dipendenze vengono risolte da Maven Central.

## Build ed esecuzione

Da PowerShell, nella radice del progetto:

```powershell
.\gradlew.bat run
.\gradlew.bat test
.\gradlew.bat build
.\gradlew.bat javadoc
```

Su Linux/macOS si usa `./gradlew` al posto di `.\gradlew.bat`. `JAVA_HOME` deve indicare un JDK compatibile con la toolchain Java 25. Con tutte le dipendenze già in cache è possibile aggiungere `--offline`.

I sorgenti vengono compilati in UTF-8. Il task `build` comprende i test; `javadoc` genera la documentazione dell'API in `build/docs/javadoc`.

## Runtime autonomo

Su Windows:

```powershell
.\gradlew.bat jlink
.\build\image\bin\app.bat
```

L'immagine in `build/image` contiene il runtime necessario all'applicazione ed è specifica della piattaforma di costruzione. Per gli altri sistemi occorre costruire e verificare l'immagine corrispondente.

La configurazione del modulo unificato include esplicitamente i servizi Hibernate caricati dinamicamente e le opzioni modulari del launcher.

## Strategia di verifica

| Livello | Casi presenti nella suite |
| --- | --- |
| Dominio | Statistiche, limiti della vita, abilità, ricariche, bonus, turni, cure e reset tra castelli. |
| Servizi applicativi | Creazione, ricostruzione, validazione dei checkpoint, ordine dei castelli e conservazione dello stato in caso di errore. |
| Controller | Traduzione dei comandi, snapshot, turno nemico e avanzamento esplicito. |
| Persistenza | Salvataggio e caricamento, sostituzione, cancellazione, rollback e cause degli errori. |
| Esecuzione asincrona | Serializzazione dei comandi, errori e chiusura ordinata delle risorse. |
| Interfaccia JavaFX | Schermate, input, abilità, risposte automatiche, progressione, errori e comportamento grafico degli asset. |

I test possono usare repository in memoria e database H2 dedicati. La suite JavaFX richiede una sessione grafica e non è configurata per l'esecuzione headless.

I risultati di una nuova esecuzione sono consultabili in `build/reports/tests/test/index.html`. Le anteprime prodotte dai test di layout si trovano in `build/reports/ui`. Queste directory sono output locali ignorati da Git.

## Criteri adottati

Il codice usa contratti e dipendenze passate ai costruttori per distinguere le regole dalle implementazioni. Record, copie difensive e snapshot immutabili limitano la condivisione dello stato; le entità Hibernate restano nel livello di persistenza.

La documentazione segue due livelli: il [README](https://github.com/Manu24life/Chronicles-of-the-Cursed-Castles/blob/main/README.md) contiene le istruzioni operative, mentre la wiki motiva le scelte ed espone i collegamenti tra i componenti.

Fonti: [build Gradle](https://github.com/Manu24life/Chronicles-of-the-Cursed-Castles/blob/main/build.gradle.kts), [wrapper](https://github.com/Manu24life/Chronicles-of-the-Cursed-Castles/blob/main/gradle/wrapper/gradle-wrapper.properties) e [test](https://github.com/Manu24life/Chronicles-of-the-Cursed-Castles/tree/main/src/test/java/it/unicam/cs/mpgc/rpg125657/cursedcastles).
