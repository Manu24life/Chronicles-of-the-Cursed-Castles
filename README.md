# Chronicles-of-the-Cursed-Castles
Progetto per modellazione e gestione della conoscenza UNICAM

RPG a turni con una View JavaFX e grafica 2D dark fantasy: sprite raster dettagliati,
scenari originali e texture/animazioni FXGL.

## Avvio

È richiesto un JDK 25. Il progetto usa il wrapper Gradle 9.1.0 e JavaFX 25.0.3;
non occorre installare separatamente JavaFX o Gradle. Impostare `JAVA_HOME` al JDK.

Da PowerShell, nella cartella del progetto:

```powershell
.\gradlew.bat run
```

Su Linux/macOS usare `./gradlew run`. Il primo avvio della build richiede accesso a
Maven Central per scaricare le dipendenze; con cache completa è disponibile `--offline`.

## Interfaccia e comandi

- **Nuova partita:** nome (massimo 100 caratteri) e classe, con statistiche del catalogo reale.
- **Continua dal checkpoint:** riparte dal primo castello incompleto; se una sessione è ancora
  aperta, **Riprendi lo scontro** torna allo stato in memoria senza ricostruirlo.
- **Combattimento:** clic su un nemico o tasti `1`, `2`, `3` per selezionarlo;
  `A` attacca, `S` usa l'abilità, `C` cura. È disponibile anche la navigazione con Tab.
- Un'azione riuscita è seguita dalla risposta automatica di **un solo nemico**, dopo 650 ms.
  Durante la risposta non è possibile impartire altri comandi o tornare al menu.
- Le vittorie vengono salvate automaticamente; il passaggio al castello successivo richiede
  un comando esplicito. Se il salvataggio fallisce, **Riprova salvataggio** conserva la vittoria
  e ritenta soltanto la scrittura.
- Cancellazione dello slot, sostituzione con una nuova partita e uscita da una sessione
  richiedono una conferma. La finestra parte a 1280×800 ed è ridimensionabile fino a 960×640.

## Salvataggio

Esiste un solo slot H2, nella directory `.cursed-castles` della home dell'utente
(`salvataggio.mv.db`). Conserva nome, classe e castelli completati. Non conserva una
battaglia a metà: uscendo dall'applicazione si perde soltanto lo stato successivo all'ultimo
checkpoint. Vita, bonus, abilità e cure sono ricostruiti secondo le regole del modello.

Per una sessione di prova isolata, la proprietà JVM `cursedcastles.database` accetta il
percorso del database senza estensione. Esempio PowerShell (variabile valida nel processo corrente):

```powershell
$env:JAVA_TOOL_OPTIONS = '-Dcursedcastles.database=build/ui-smoke/salvataggio'
.\gradlew.bat run
Remove-Item Env:JAVA_TOOL_OPTIONS
```

## Organizzazione della View

La View è nel package `it.unicam.cs.mpgc.rpg125657.cursedcastles.view`:

- `GestoreViste`: caricamento delle schermate FXML, tema e transizioni.
- `CoordinatoreInterfaccia`: navigazione e collegamento ai controller applicativi esistenti.
- `EsecutoreComandi`: accessi serializzati alla sessione e al database fuori dal thread JavaFX;
  la chiusura delle risorse viene accodata dopo gli eventuali comandi in corso.
- `DialoghiGioco`: errori e conferme con il tema comune.
- `schermate`: `SchermataMenu`, `SchermataNuovaPartita` e `SchermataBattaglia` gestiscono
  presentazione ed eventi FXML. I controller del pattern MVC restano nel package applicativo `controller`.
- `componenti`: barre vita, schede bersaglio, sprite 2D FXGL e panorami delle battaglie.
  Il menu conserva il suo paesaggio Canvas. Non vengono scaricati asset a runtime.
- `grafica.RisorseBattaglia`: precaricamento degli asset fuori dal thread JavaFX, cache delle
  immagini e creazione di nodi `Texture` indipendenti per ogni nemico.

Gli aggiornamenti della scena usano gli snapshot immutabili esposti dai controller.
FXML e `cursed-castles.css` risiedono nel package risorse dell'applicazione. Il font Georgia
viene usato quando disponibile, altrimenti viene selezionato il font logico Serif.
FXGL è integrato tramite `com.github.almasb:fxgl-core:25.0.1` (Java 25), usando `Texture`
e `AnimationBuilder` senza avviare una seconda `GameApplication`. JavaFX resta alla versione
25.0.3; il modulo `javafx.media` soddisfa una dipendenza di FXGL Core, senza aggiungere audio
al gioco. Le dipendenze Kotlin/Gluon necessarie vengono risolte da Gradle.
Le regole di Model e Controller non sono state modificate.
La configurazione jlink dichiara esplicitamente gli SPI Hibernate caricati mediante ServiceLoader;
gli errori di collegamento o configurazione dei servizi vengono mostrati nell'interfaccia,
senza lasciare l'indicatore di caricamento attivo indefinitamente.
Il launcher jlink include inoltre le aperture modulari mirate necessarie a Hibernate/ByteBuddy
per il package delle entità; il normale avvio Gradle conserva la configurazione JPMS esistente.

Per costruire e avviare un runtime autonomo su Windows:

```powershell
.\gradlew.bat jlink
.\build\image\bin\app.bat
```

## Asset delle battaglie

I sette PNG sono incorporati nelle risorse del package dell'applicazione, sotto `grafica`:
quattro sprite trasparenti (`nemici`) e tre panorami (`scenari`). 
Il file `grafica/README.md` ne descrive uso e provenienza.
Gli asset sono stati realizzati inizialmente in Figma insieme allo strumento immagini integrato prendendo spunto dal noto gioco Fear & Hunger.



I nemici mantengono le proporzioni; il drago ha un'area visiva più ampia. Gli sfondi vengono
ritagliati proporzionalmente al ridimensionamento, mantenendo il pavimento. Il danno produce
un breve sobbalzo FXGL; la sconfitta una dissolvenza. Si tratta di sprite statici con feedback
di trasformazione/opacità, senza sequenze disegnate fotogramma per fotogramma. Le animazioni
si fermano al distacco dalla scena e i timer sono attivi soltanto durante il feedback.

## Verifica

```powershell
.\gradlew.bat test
.\gradlew.bat build
```

I test includono dominio, servizi, persistenza, ordine dei comandi asincroni e percorsi della
View JavaFX: creazione, entrambe le abilità, bersaglio, cure, risposta nemica, vittoria,
avanzamento, conclusione, sconfitta, checkpoint ed errori di salvataggio. La suite grafica
richiede una sessione desktop con toolkit JavaFX disponibile; non è configurata una modalità
headless. I test usano repository in memoria o database dedicati, senza aprire lo slot personale.

Report dei test: `build/reports/tests/test/index.html`. Le verifiche di layout producono
anteprime PNG in `build/reports/ui`, anche alla risoluzione minima e per i tre castelli.

## Uso dell'intelligenza artificiale

Ho definito l'architettura, le regole del gioco e le scelte tecniche del progetto. Ho usato
**Codex (GPT-6.1 Sol)** come supporto per costruttori, test, grafica, documentazione e soprattutto
per il debug della persistenza. Ho usato **Claude Sonnet 5.5** per un secondo controllo della qualità
del codice. Ho revisionato personalmente tutti i contenuti prodotti dall'IA prima di integrare
quelli utili al progetto.

Per i dettagli, consulta la pagina [Uso dell'IA nella wiki](docs/wiki/Uso-dell-AI.md).

## Ambito attuale

La View rappresenta le funzionalità esistenti. Inventario, equipaggiamento, esplorazione libera,
dialoghi narrativi, audio e impostazioni persistenti non sono implementati nel dominio e non
vengono simulati dall'interfaccia.
