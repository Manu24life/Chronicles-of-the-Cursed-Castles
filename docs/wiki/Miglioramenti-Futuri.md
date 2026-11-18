# Miglioramenti Futuri

Le proposte di questa pagina non sono funzionalità già implementate né impegni di sviluppo. Individuano possibili evoluzioni a partire dalla struttura attuale.

## Contenuti e meccaniche

| Proposta | Intervento previsto |
| --- | --- |
| Nuove classi e abilità | Aggiungere voci in `ConfigurazioneEroe`, creare abilità e aggiungere test e presentazioni. |
| Nuovi mostri e castelli | Definire i tipi in `ConfigurazioneMostro`, aggiornare la campagna in `Castelli` e preparare le risorse grafiche. |
| [Comportamenti specifici dei nemici](#comportamenti-specifici-dei-nemici) | Introdurre una politica per scegliere le azioni di ciascun nemico. |
| [Bilanciamento](#bilanciamento) | Valutare statistiche, efficacia delle cure e durata degli scontri tramite casi riproducibili. |
| Inventario ed equipaggiamento | Modellare oggetti, regole di utilizzo e modificatori, poi integrare persistenza e vista. |
| Esplorazione e dialoghi | Introdurre stato di esplorazione, eventi e progressione narrativa. |

## Comportamenti specifici dei nemici e bilanciamento

### Comportamenti specifici dei nemici

Ogni tipo di nemico potrebbe scegliere come agire in base allo stato dello scontro. La proposta mantiene la regola attuale di **un solo nemico in risposta per turno** e rende riconoscibili le mosse speciali con un segnale prima che abbiano effetto.

| Nemico | Possibile comportamento |
| --- | --- |
| **Scheletro** | Alterna un attacco semplice a una guardia temporanea. Il giocatore può cambiare bersaglio mentre la guardia è attiva. |
| **Orco** | Usa una risposta per preparare un colpo pesante. L’avviso dà al giocatore un turno per decidere se finirlo, curarsi o usare un’abilità. |
| **Vampiro** | Quando ha poca vita, può recuperare una parte del danno inflitto. Un numero limitato di utilizzi evita che lo scontro si prolunghi eccessivamente. |
| **Drago** | Sotto una soglia di vita passa a una seconda fase, alternando gli attacchi normali a un soffio annunciato in anticipo. |

### Bilanciamento

Il bilanciamento potrebbe partire da **scontri riproducibili con Mago e Cavaliere in ciascun castello**. Per ogni prova si registrerebbero turni impiegati, vita residua, cure consumate e utilizzi delle abilità. Nella Fortezza di Ferro sarebbe utile confrontare anche ordini diversi di eliminazione dei nemici, per verificare quanto la scelta del bersaglio incida sul risultato.

Le prove partirebbero dalle regole attuali. Dopo aver introdotto un comportamento speciale, si ripeterebbero gli stessi scontri modificando **un parametro alla volta**, per esempio il danno del colpo caricato o la frequenza della cura del vampiro. L’obiettivo è lasciare a entrambe le classi più di una scelta praticabile e fare in modo che il giocatore possa reagire alle minacce annunciate.

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
