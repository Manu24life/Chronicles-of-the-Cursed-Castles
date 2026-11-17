# Problemi e Soluzioni

Questa pagina raccoglie problemi affrontati dal codice e le soluzioni presenti nell'implementazione.

## Separare checkpoint e battaglia in memoria

**Problema:** un salvataggio parziale della battaglia richiederebbe di ripristinare vita di tutti i combattenti, bonus, ricariche, cure e turno corrente.

**Soluzione:** `ProgressiPartita` conserva soltanto identità dell'eroe e castelli completati. `ServizioProgressione` ricostruisce una nuova battaglia dal primo castello incompleto; `SessioneGioco` mantiene lo scontro corrente finché l'applicazione resta aperta.

**Conseguenza:** tornare al menu può conservare lo scontro in memoria, mentre riavviare l'applicazione riporta al checkpoint.

## Conservare una vittoria se la scrittura fallisce

**Problema:** l'azione può aver già sconfitto l'ultimo nemico quando il database restituisce un errore. Ripetere l'intero comando altererebbe lo stato di gioco.

**Soluzione:** `ServizioGioco` conserva la vittoria e lo stato del salvataggio. Il coordinatore rilegge lo snapshot dopo l'errore; il comando successivo ritenta soltanto `salvaVittoria`. L'avanzamento richiede un checkpoint riuscito.

**Verifica nel codice:** `ServizioGiocoTest.riprovaLaVittoriaSenzaPerdereLoStatoRicreandoIlController` e `InterfacciaJavaFxTest.ritentaSoloIlSalvataggioDopoUnCommitFallito`.

## Proteggere sessione e salvataggio durante una nuova partita

**Problema:** una creazione non riuscita non deve cancellare una progressione valida o sostituire la sessione precedente.

**Soluzione:** il servizio costruisce la partita candidata prima della sostituzione persistente. La sessione viene aggiornata dopo il completamento riuscito della creazione e della scrittura.

**Verifica nel codice:** `ServizioGiocoTest` simula fallimenti delle fabbriche e del repository.

## Mantenere reattiva l'interfaccia

**Problema:** inizializzazione di Hibernate e operazioni sul database possono occupare il thread grafico; comandi concorrenti possono modificare la stessa partita.

**Soluzione:** `EsecutoreComandi` serializza il lavoro su un solo thread e consegna gli aggiornamenti tramite `Platform.runLater`. Durante le operazioni la vista blocca i gesti non consentiti. `PauseTransition` programma la risposta del nemico senza attese bloccanti.

**Verifica nel codice:** `EsecutoreComandiTest` controlla ordine, errori, consegna dei risultati e chiusura dopo le scritture.

## Evitare stato condiviso tra personaggi e castelli

**Problema:** riutilizzare la stessa abilità o la stessa regola di cura trasferirebbe ricariche e consumi tra istanze diverse.

**Soluzione:** le fabbriche producono un'abilità per ogni eroe, nuovi nemici per ogni battaglia e una nuova cura per ogni castello. `Eroe.preparaNuovoCastello()` ripristina vita e abilità e azzera i bonus.

**Verifica nel codice:** `AbilitaEroeTest`, `CastelliTest` e `BattagliaTest`.

## Rilevare checkpoint incoerenti

**Problema:** un codice sconosciuto o castelli completati fuori ordine non permettono una ricostruzione affidabile della campagna.

**Soluzione:** `ServizioProgressione` valida cataloghi, riferimenti e ordine. La vista segnala il problema; la cancellazione dello slot richiede conferma esplicita.

**Verifica nel codice:** `ServizioGiocoTest.rifiutaSalvataggiFuoriOrdineOConRiferimentiSconosciuti` e il test JavaFX sul recupero dello slot non valido.

## Gestire transazioni ed errori di rollback

**Problema:** una scrittura incompleta non deve lasciare il checkpoint modificato parzialmente, e un errore di rollback non deve nascondere la causa originale.

**Soluzione:** ogni operazione Hibernate usa una transazione. In caso di errore viene tentato il rollback; un suo eventuale fallimento è associato come eccezione soppressa.

**Verifica nel codice:** `HibernateProgressiRepositoryTest` e `HibernateTransazioneTest`.

## Distribuire Hibernate in un runtime modulare

**Problema:** Hibernate usa riflessione e servizi caricati dinamicamente; il solo rilevamento automatico delle dipendenze può non includere tutti gli SPI nell'immagine `jlink`.

**Soluzione:** la build dichiara gli SPI nel modulo unificato e configura le aperture e le letture modulari richieste dal launcher. L'esecutore converte anche errori di collegamento o di configurazione dei servizi in errori presentabili dalla vista.

**Riferimenti:** [configurazione della build](https://github.com/Manu24life/Chronicles-of-the-Cursed-Castles/blob/main/build.gradle.kts) e `EsecutoreComandiTest.segnalaAncheGliErroriDiConfigurazioneDeiServiziModulari`.

## Condividere le immagini senza condividere i nodi JavaFX

**Problema:** un nodo grafico non può appartenere contemporaneamente a più contenitori, mentre ricaricare le stesse immagini spreca risorse.

**Soluzione:** `RisorseBattaglia` conserva le immagini in cache e crea texture indipendenti per ciascun nemico. Il feedback viene arrestato al distacco del componente dalla scena.

**Verifica nel codice:** i test JavaFX sulle texture FXGL, sulla trasparenza e sull'arresto delle animazioni.

I test citati sono disponibili nella [directory dei test](https://github.com/Manu24life/Chronicles-of-the-Cursed-Castles/tree/main/src/test/java/it/unicam/cs/mpgc/rpg125657/cursedcastles). I comandi per eseguirli sono in [Strumenti e Metodologie](Strumenti-e-Metodologie).
