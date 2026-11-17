# Responsabilità delle Classi

## Personaggi e configurazioni

| Classe o contratto | Responsabilità |
| --- | --- |
| `Statistiche` | Rappresentare e validare vita massima, attacco e difesa base. |
| `Personaggio` | Conservare nome, statistiche e vita corrente; applicare danni e recuperi entro i limiti. |
| `BonusStatistiche` e `ModificatoreStatistiche` | Accumulare e descrivere bonus percentuali separati dalle statistiche base. |
| `Eroe` | Definire il contratto dell'eroe, inclusi abilità, avanzamento della ricarica e preparazione del castello. |
| `EroeGiocabile` | Comporre personaggio, classe, abilità e bonus per l'eroe concreto. |
| `ClasseEroe` e `ClassiEroe` | Descrivere una classe e fornire il catalogo iniziale. |
| `FabbricaEroiStandard` | Creare un eroe con una nuova istanza dell'abilità della classe. |
| `TipoMostro` e `TipiMostro` | Descrivere e catalogare le configurazioni dei mostri. |
| `Mostro` | Realizzare un nemico con le statistiche del suo tipo. |
| `Castello` e `ConfigurazioneNemici` | Descrivere un castello e le quantità dei tipi di nemico. |
| `Castelli` | Fornire la sequenza iniziale dei tre castelli e la ricerca per codice. |
| `FabbricaNemiciStandard` | Generare nuove istanze dei nemici per ogni battaglia. |

## Combattimento e campagna

| Classe | Responsabilità |
| --- | --- |
| `AttaccoBase` | Calcolare e applicare il danno standard. |
| `CuraPercentualeLimitata` | Applicare la cura e contare gli utilizzi nel castello. |
| `RicaricaATurni` | Gestire disponibilità, consumo e ripristino di una ricarica. |
| `AbilitaConRicarica` | Gestire le verifiche comuni e il consumo della ricarica per le abilità. |
| `AssorbimentoMagico` | Applicare il danno speciale e la cura del Mago. |
| `TempraDelCavaliere` | Aggiungere bonus cumulativi all'eroe senza bersaglio nemico. |
| `Battaglia` | Ammettere le azioni nel turno corretto, scegliere il nemico che risponde e rilevare vittoria o sconfitta. |
| `Partita` | Governare la campagna, l'ordine dei castelli e il reset dello stato temporaneo. |
| `RisultatoAzione` ed `EsitoAzione` | Descrivere l'effetto o il motivo di rifiuto di un'azione. |

`Battaglia` applica le regole dello scontro; il coordinamento del checkpoint appartiene ai servizi applicativi. `Partita` governa l'avanzamento, senza aprire connessioni al database.

## Servizi e controller

| Classe | Responsabilità |
| --- | --- |
| `SessioneGioco` | Conservare il riferimento alla partita attiva. |
| `ServizioGioco` | Coordinare sessione, azioni, salvataggio della vittoria e avanzamento. |
| `ServizioProgressione` | Validare progressi e cataloghi, creare o ricostruire la partita e aggiornare il checkpoint. |
| `MenuController` | Tradurre i comandi del menu nei casi d'uso ed esporre `StatoMenu`. |
| `BattagliaController` | Tradurre i comandi di combattimento ed esporre snapshot immutabili. |
| Record del package `controller` | Trasportare informazioni per la presentazione senza esporre il modello mutabile. |

## Persistenza

| Classe | Responsabilità |
| --- | --- |
| `ProgressiPartita` | Rappresentare nome, codice classe e insieme immutabile dei castelli completati. |
| `HibernateProgressiRepository` | Implementare caricamento, sostituzione e cancellazione dello slot in transazione. |
| `SalvataggioEntity` | Mappare i dati del checkpoint nello schema relazionale e convertirli da/verso `ProgressiPartita`. |
| `HibernateSessionFactory` | Configurare e aprire Hibernate e il database H2. |
| `EccezionePersistenza` | Segnalare al livello applicativo un errore nell'accesso ai dati. |
| `SalvataggioNonValidoException` | Segnalare progressi incompatibili con i cataloghi o con l'ordine della campagna. |

## Interfaccia grafica

| Classe | Responsabilità |
| --- | --- |
| `CursedCastlesApplication` | Avviare JavaFX, creare la finestra e collegare la chiusura. |
| `GestoreViste` | Caricare FXML, applicare il tema e mostrare schermate e transizioni. |
| `CoordinatoreInterfaccia` | Collegare componenti, navigazione e comandi; presentare risultati ed errori. |
| `EsecutoreComandi` | Serializzare il lavoro fuori dal thread grafico e consegnare i risultati alla vista. |
| `DialoghiGioco` | Presentare conferme ed errori con il tema comune. |
| `SchermataMenu`, `SchermataNuovaPartita`, `SchermataBattaglia` | Gestire presentazione, controlli FXML e input delle singole schermate. |
| `RisorseBattaglia` | Precaricare le immagini, condividerne la cache e creare texture indipendenti. |
| Componenti della vista | Disegnare scenari, barre vita, schede e illustrazioni dei nemici. |

Le classi sono consultabili nell'[albero dei sorgenti](https://github.com/Manu24life/Chronicles-of-the-Cursed-Castles/tree/main/src/main/java/it/unicam/cs/mpgc/rpg125657/cursedcastles).
