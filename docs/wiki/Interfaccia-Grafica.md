# Interfaccia Grafica

## Tecnologie e schermate

L'interfaccia usa JavaFX 25.0.3, layout FXML e il foglio di stile `cursed-castles.css`. FXGL Core 25.0.1 fornisce texture e animazioni integrate nella stessa applicazione JavaFX.

| Risorsa | Ruolo |
| --- | --- |
| `main-view.fxml` | Contenitore principale delle schermate e dello stato di caricamento. |
| `menu-view.fxml` | Nuova partita, continuazione, riepilogo dello slot e cancellazione. |
| `nuova-partita-view.fxml` | Nome dell'eroe e scelta della classe, con statistiche del catalogo. |
| `battaglia-view.fxml` | Stato dell'eroe, nemici selezionabili, azioni, registro e avanzamento. |

La scena iniziale misura 1280 × 800; la finestra è ridimensionabile con minimo 960 × 640. Il tema usa Georgia quando disponibile e il font logico Serif come alternativa.

## Controlli

| Input | Azione |
| --- | --- |
| Clic su un nemico, oppure `1`, `2`, `3` | Selezione del bersaglio corrispondente. |
| `A` | Attacco base. |
| `S` | Abilità della classe. |
| `C` | Cura. |
| Tab | Navigazione tra i controlli. |

Le azioni possono essere attivate anche dai pulsanti. La vista abilita i comandi secondo lo snapshot corrente: turno, stato della battaglia, disponibilità dell'abilità e delle cure.

Durante la risposta nemica non è possibile impartire una nuova azione o tornare al menu. La risposta automatica parte dopo 650 ms. Se il bersaglio selezionato viene sconfitto, la vista aggiorna la selezione in base ai nemici disponibili.

## Presentazione dello stato

La schermata di battaglia mostra vita, attacco, difesa e bonus dell'eroe, nemici del castello, cure residue, stato dell'abilità e registro degli eventi. Vittoria, sconfitta e campagna completata determinano la presentazione dei comandi successivi.

La vista distingue una vittoria salvata da una vittoria ancora da scrivere. Nel secondo caso presenta **Riprova salvataggio**; dopo la scrittura consente l'avanzamento esplicito.

## Componenti e grafica

`RisorseBattaglia` precarica le immagini fuori dal thread JavaFX e ne mantiene una cache. Le immagini possono essere condivise, mentre ciascun nemico riceve nodi `Texture` indipendenti.

Le risorse locali comprendono quattro sprite trasparenti dei nemici e tre scenari. Gli sprite conservano le proporzioni; il drago occupa un'area visiva maggiore. Gli scenari vengono ritagliati proporzionalmente al ridimensionamento, mantenendo il pavimento. Il menu usa un paesaggio Canvas.

Il danno produce un breve sobbalzo e la sconfitta una dissolvenza. Gli sprite sono immagini statiche animate con trasformazioni e opacità. Animazioni e timer vengono fermati quando il componente viene rimosso dalla scena. Gli asset sono inclusi nell'applicazione e non vengono scaricati a runtime.

Il modulo `javafx.media` soddisfa una dipendenza di FXGL Core; il gioco non implementa audio.

## Responsività e gestione degli errori

`EsecutoreComandi` esegue comandi e accessi al database su un executor seriale. Gli aggiornamenti della scena tornano sul thread JavaFX e usano gli snapshot dei controller.

`DialoghiGioco` presenta errori e conferme. Nuova partita su uno slot occupato, cancellazione e uscita da una sessione attiva prevedono conferme. Un errore di avvio del database permette di ritentare o uscire; un salvataggio non valido viene segnalato prima di una possibile cancellazione esplicita.

## Verifiche della vista

`InterfacciaJavaFxTest` copre caricamento delle schermate, classi e abilità, bersagli, cure, risposta automatica, vittoria, avanzamento, conclusione, sconfitta, checkpoint e recupero dagli errori di salvataggio. Produce anteprime locali in `build/reports/ui`, incluse viste compatte e i tre castelli.

La suite richiede il toolkit JavaFX e una sessione desktop; non è configurata una modalità headless. Le anteprime sono output della build e non sono incluse nelle pagine della wiki.

Fonti: [vista](https://github.com/Manu24life/Chronicles-of-the-Cursed-Castles/tree/main/src/main/java/it/unicam/cs/mpgc/rpg125657/cursedcastles/view), [risorse FXML e CSS](https://github.com/Manu24life/Chronicles-of-the-Cursed-Castles/tree/main/src/main/resources/it/unicam/cs/mpgc/rpg125657/cursedcastles) e [test JavaFX](https://github.com/Manu24life/Chronicles-of-the-Cursed-Castles/blob/main/src/test/java/it/unicam/cs/mpgc/rpg125657/cursedcastles/view/InterfacciaJavaFxTest.java).
