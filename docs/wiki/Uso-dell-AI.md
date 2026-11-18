# Uso dell'IA

## Il mio contributo al progetto

La progettazione del gioco è partita dalle regole e dalle meccaniche che volevo realizzare. Su questa base ho organizzato i package e l'intera struttura delle cartelle, definito i contratti tra i componenti e curato l'implementazione dei metodi principali. Mi sono occupato anche della scelta delle librerie a supporto del progetto. [Architettura](Architettura) · [Interfacce e Astrazioni](Interfacce-e-Astrazioni) · [Strumenti e Metodologie](Strumenti-e-Metodologie)

## Supporto di Codex (GPT-6.1 Sol)

Codex mi ha affiancato nella scrittura dei costruttori e dei test automatizzati, oltre che nel miglioramento dell'interfaccia grafica. La grafica era facoltativa per la consegna e mi sono dedicato a migliorarla per interesse personale. Codex mi è servito anche per velocizzare il lavoro sulla documentazione, dalla stesura di una Javadoc dettagliata a partire dai miei commenti all'organizzazione e all'aggiornamento delle pagine della wiki. [Interfaccia Grafica](Interfaccia-Grafica)

Per evitare di copiare a mano le pagine a ogni aggiornamento, ho realizzato con Codex lo script PowerShell `Publish-Wiki.ps1`. Lo avvio manualmente e lo script copia i file Markdown di `docs/wiki` in una copia locale della wiki GitHub, poi mostra un riepilogo delle modifiche. Quando voglio pubblicarle, lo eseguo con l'opzione `-Publish`, che effettua il commit e il push se ci sono davvero delle differenze. [Script di pubblicazione](https://github.com/Manu24life/Chronicles-of-the-Cursed-Castles/blob/main/scripts/Publish-Wiki.ps1)

Il contributo di Codex è stato fondamentale soprattutto nel debug della persistenza, dove mi ha aiutato a ricostruire il flusso di salvataggio e caricamento dei progressi e a individuare le cause degli errori nelle operazioni con Hibernate e H2. [Persistenza dei Dati](Persistenza-dei-Dati) · [Problemi e Soluzioni](Problemi-e-Soluzioni)

## Versionamento e controllo della qualità del codice

Alcune correzioni al codice le ho fatte direttamente nell'editor di GitHub. ChatGPT mi ha aiutato a risolvere i problemi di versionamento emersi quando ho dovuto riallineare quelle modifiche con la copia locale del progetto, su cui lavoravo in IntelliJ IDEA.

Per un secondo controllo della qualità del codice mi sono servito anche di Claude Sonnet 5.5.

## Revisione dei contributi dell'IA

Tutto ciò che gli strumenti di IA hanno prodotto è passato da una mia revisione. Prima di integrare un contributo, ne ho verificato la coerenza con i contratti e le scelte del progetto. Nel caso del codice, mi sono assicurato di comprenderne il funzionamento e l'ho adattato dove necessario.
