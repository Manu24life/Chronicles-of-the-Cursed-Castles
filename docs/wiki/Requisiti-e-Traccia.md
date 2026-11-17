# Requisiti e Traccia

## Descrizione della traccia

Il progetto realizza un'applicazione Java per un gioco di ruolo. I requisiti comprendono una build Gradle, un'interfaccia grafica, la persistenza dei dati, l'estendibilità e la documentazione su GitHub.

Il progetto interpreta la traccia con una campagna di combattimenti a turni ambientata in tre castelli maledetti. Il giocatore controlla un solo eroe e sceglie una delle due classi disponibili.

## Requisiti principali

| Requisito | Realizzazione nel progetto |
| --- | --- |
| Applicazione Java | Codice Java 25 organizzato in package e modulo JPMS. |
| Build ed esecuzione con Gradle | Wrapper Gradle 9.1.0, task `build`, `test` e `run`. |
| Esecuzione su altri computer | Dipendenze risolte da Gradle; task `jlink` per creare un runtime per il sistema di destinazione. La build da sorgente richiede JDK 25 e ambiente grafico. |
| Interfaccia grafica | Applicazione JavaFX con schermate FXML e tema CSS. |
| Persistenza | Un checkpoint locale gestito tramite Hibernate ORM e database H2 su file. |
| Estendibilità | Contratti per combattenti, abilità, ricarica, cure, fabbriche e repository; cataloghi di classi e castelli. |

Un'immagine `jlink` è specifica della piattaforma su cui viene costruita. La disponibilità dei sorgenti e del wrapper permette di ricostruire l'applicazione sulle piattaforme supportate dalle dipendenze; questa documentazione non certifica test su tutti i sistemi operativi.

## Requisiti di progettazione

La ripartizione delle responsabilità distingue:

- **Dominio:** personaggi, statistiche, abilità, combattimenti e avanzamento nella campagna.
- **Servizi applicativi:** sessione attiva, checkpoint e ricostruzione della partita.
- **Controller:** comandi del menu e della battaglia, con snapshot per la vista.
- **Persistenza:** conversione dei progressi in entità relazionali e gestione delle transazioni.
- **Vista:** presentazione JavaFX, input, navigazione e feedback visivo.

Gli esempi concreti sono descritti in [Architettura](Architettura), [Responsabilità delle Classi](Responsabilita-delle-Classi) e [Principi SOLID](Principi-SOLID).

## Strumenti e struttura

Il progetto usa Git/GitHub, Gradle e Java 25. Il package `it.unicam.cs.mpgc.rpg<matricola>` è specializzato in:

```text
it.unicam.cs.mpgc.rpg125657.cursedcastles
├── model
├── application
├── controller
├── persistence
└── view
```

Le risorse grafiche sono in `src/main/resources`; i test sono in `src/test/java`.

## Funzionalità scelte per questa implementazione

La campagna prevede creazione dell'eroe, selezione dei bersagli, attacco base, abilità di classe, cure limitate, risposta nemica, vittoria, sconfitta, avanzamento e ripresa dal checkpoint. Il perimetro dettagliato è in [Funzionalità](Funzionalita).

## Consegna

La consegna comprende un repository pubblico, sorgenti compilabili ed eseguibili, README e wiki. Il repository del progetto contiene codice, wrapper, configurazione della build e test. Il README spiega l'avvio; questa wiki approfondisce il progetto.

Le istruzioni del docente restano il riferimento per la valutazione e per eventuali aggiornamenti dei requisiti.
