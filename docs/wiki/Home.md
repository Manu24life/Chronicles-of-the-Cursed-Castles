# Chronicles of the Cursed Castles

## Descrizione

**Chronicles of the Cursed Castles** è un gioco di ruolo a turni per desktop, sviluppato in Java per il progetto di Modellazione e Gestione della Conoscenza dell'Università di Camerino.

Il giocatore sceglie un Mago o un Cavaliere e affronta una campagna dark fantasy composta da tre castelli. Ogni scontro richiede di scegliere il bersaglio e alternare attacchi, abilità speciali e cure limitate. L'obiettivo è sconfiggere il drago nella Rocca della Maledizione.

Il progetto separa regole di gioco, servizi applicativi, controller, interfaccia JavaFX e persistenza Hibernate/H2. Questa wiki documenta le funzionalità e le scelte progettuali presenti nel codice.

## Indice della wiki

📌 [Funzionalità](Funzionalita)\
🏗️ [Architettura](Architettura)\
🧩 [Responsabilità delle Classi](Responsabilita-delle-Classi)\
🔌 [Interfacce e Astrazioni](Interfacce-e-Astrazioni)\
📐 [Principi SOLID](Principi-SOLID)\
💾 [Persistenza dei Dati](Persistenza-dei-Dati)\
🎨 [Interfaccia Grafica](Interfaccia-Grafica)\
➕ [Estendibilità](Estendibilita)\
⚠️ [Problemi e Soluzioni](Problemi-e-Soluzioni)\
🤖 [Uso dell'IA](Uso-dell-AI)\
🚀 [Miglioramenti Futuri](Miglioramenti-Futuri)\
🛠️ [Strumenti e Metodologie](Strumenti-e-Metodologie)

## Avvio rapido

È richiesto un **JDK 25**, con `JAVA_HOME` configurato. Gradle e JavaFX vengono gestiti dalla build; il primo avvio richiede una connessione per scaricare le dipendenze.

Su Windows, da PowerShell:

```powershell
git clone https://github.com/Manu24life/Chronicles-of-the-Cursed-Castles.git
Set-Location Chronicles-of-the-Cursed-Castles
.\gradlew.bat build
.\gradlew.bat run
```

Su Linux e macOS:

```bash
git clone https://github.com/Manu24life/Chronicles-of-the-Cursed-Castles.git
cd Chronicles-of-the-Cursed-Castles
./gradlew build
./gradlew run
```

La suite comprende test JavaFX e richiede una sessione grafica. Le istruzioni complete sono nel [README del progetto](https://github.com/Manu24life/Chronicles-of-the-Cursed-Castles/blob/main/README.md).

## Informazioni sul progetto

| Voce | Valore |
| --- | --- |
| Repository | [Manu24life/Chronicles-of-the-Cursed-Castles](https://github.com/Manu24life/Chronicles-of-the-Cursed-Castles) |
| Matricola nel package | `125657` |
| Package base | `it.unicam.cs.mpgc.rpg125657.cursedcastles` |
| Linguaggio | Java 25 |
| Build | Gradle 9.1.0, Kotlin DSL, Gradle Wrapper |
| Interfaccia | JavaFX 25.0.3, FXML, CSS, FXGL Core 25.0.1 |
| Persistenza | Hibernate ORM 7.4.12.Final e H2 2.5.250 |
| Test | JUnit Jupiter 5.12.1 |
| Licenza del repository | MIT |

I contenuti descrivono questa implementazione; le proposte di evoluzione sono raccolte in [Miglioramenti Futuri](Miglioramenti-Futuri).
