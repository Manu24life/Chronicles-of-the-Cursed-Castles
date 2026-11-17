# Miglioramenti Futuri

Le proposte di questa pagina non sono funzionalità già implementate né impegni di sviluppo. Individuano possibili evoluzioni a partire dalla struttura attuale.

## Contenuti e meccaniche

| Proposta | Intervento previsto |
| --- | --- |
| Nuove classi e abilità | Estendere il catalogo `ClasseEroe`, creare abilità e aggiungere test e presentazioni. |
| Nuovi mostri e castelli | Aggiungere configurazioni, aggiornare la campagna e preparare le risorse grafiche. |
| Comportamenti specifici dei nemici | Introdurre una politica per scegliere le azioni di ciascun nemico. |
| Bilanciamento | Valutare statistiche, efficacia delle cure e durata degli scontri tramite casi riproducibili. |
| Inventario ed equipaggiamento | Modellare oggetti, regole di utilizzo e modificatori, poi integrare persistenza e vista. |
| Esplorazione e dialoghi | Introdurre stato di esplorazione, eventi e progressione narrativa. |

## Salvataggi

Un'evoluzione potrebbe introdurre più slot e un formato versionato del checkpoint, con migrazioni per i cambiamenti dei cataloghi.

Il ripristino di una battaglia interrotta richiederebbe un modello durevole aggiuntivo per nemici, vita residua, bonus, ricariche, cure e turno corrente. La soluzione attuale conserva soltanto i castelli completati.

## Interfaccia ed esperienza d'uso

Possibili interventi comprendono impostazioni persistenti, rimappatura dei tasti, regolazione della dimensione dei testi, verifiche di contrasto, localizzazione, audio e animazioni a più fotogrammi.

L'attuale separazione delle schermate consente di affrontare questi aspetti nella vista, aggiungendo contratti o dati applicativi quando una preferenza deve essere conservata.

## Struttura e distribuzione

Il collegamento dei componenti potrebbe essere estratto da `CoordinatoreInterfaccia.avvia()` in un componente dedicato all'avvio. I cataloghi statici potrebbero essere sostituiti con configurazioni esterne validate, mantenendo i codici stabili dei contenuti.

Altri sviluppi possibili sono migrazioni esplicite dello schema, una configurazione di integrazione continua e pacchetti dedicati alle piattaforme di destinazione. La distribuzione attuale offre il task `jlink`; una futura procedura di rilascio potrebbe automatizzare costruzione e verifica delle immagini per ciascun sistema.

## Verifica

Per le estensioni andrebbero aggiunti test sui nuovi comportamenti, mantenendo quelli che proteggono progressione, indipendenza dello stato, checkpoint ed errori di persistenza. Una modalità grafica headless richiederebbe una configurazione dedicata; al momento i test JavaFX usano una sessione desktop.

I punti già predisposti sono descritti in [Estendibilità](Estendibilita).
