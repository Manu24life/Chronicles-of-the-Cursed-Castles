# Estendibilità

## Aggiungere una classe dell'eroe

Una classe è una configurazione `ClasseEroe` con codice stabile, nome, statistiche base e `FabbricaAbilitaEroe`.

Per aggiungerla occorre implementare l'eventuale nuova abilità, registrare la configurazione nel catalogo e aggiungere i test del comportamento. Il menu legge le classi esposte dal servizio e può quindi mostrare una nuova configurazione senza introdurre una nuova sottoclasse di `Personaggio`.

Ogni fabbrica deve creare un'abilità distinta per ogni eroe, con una propria ricarica. Se la nuova classe richiede una presentazione specifica, vanno aggiornate anche la vista e le relative risorse.

## Aggiungere nemici e castelli

Un nuovo `TipoMostro` definisce codice, nome e statistiche. Un `Castello` contiene una lista di `ConfigurazioneNemici`, ciascuna con tipo e quantità.

La fabbrica standard può creare un nuovo mostro configurato senza modificare l'algoritmo di attacco. Per inserirlo nella campagna si aggiorna il catalogo dei castelli; per visualizzarlo con una grafica dedicata si aggiornano `RisorseBattaglia`, i componenti interessati e gli asset.

I mostri attuali condividono la regola di attacco della battaglia. Comportamenti diversi per singolo nemico richiederebbero un ulteriore punto di estensione nella scelta delle azioni avversarie.

## Modificare le regole

| Estensione | Punto previsto |
| --- | --- |
| Formula o tipo di attacco | Nuova implementazione di `AzioneCombattimento`. |
| Cura con percentuale o limite diverso | Parametri di `CuraPercentualeLimitata` oppure nuova `RegolaCura`. |
| Disponibilità dell'abilità | Nuova implementazione di `Ricarica`. |
| Effetto dell'abilità | Nuova `AbilitaEroe` o specializzazione di `AbilitaConRicarica`. |
| Regola di cura distinta per castello | `FabbricaRegolaCura`, ricevuta da `Partita`. |
| Modalità di creazione degli oggetti | Implementazioni alternative di `FabbricaEroi` e `FabbricaNemici`. |

I costruttori estesi di `Partita` e `Battaglia` consentono di fornire strategie alternative. La costruzione standard usata dal servizio di progressione mantiene le regole iniziali: per cambiare quelle di produzione occorre adattare anche il collegamento applicativo.

## Sostituire la persistenza

Un nuovo adattatore può implementare `ProgressiRepository` per conservare `ProgressiPartita` in un altro formato o database. I servizi continuerebbero a usare `carica`, `sostituisci` ed `elimina`.

L'adattatore deve preservare la semantica dello slot e la gestione degli errori. La sua costruzione sostituirebbe il collegamento Hibernate nell'avvio dell'applicazione.

## Compatibilità dei salvataggi

I checkpoint contengono i codici delle classi e dei castelli. Rinominare un'etichetta può conservarne il significato mantenendo il codice; eliminare o cambiare un codice richiede una strategia di migrazione.

L'ordine dei castelli è significativo: i completamenti devono corrispondere a un prefisso della campagna. Un cambiamento della sequenza può rendere un vecchio checkpoint non valido. Attualmente il servizio rileva l'incompatibilità e la segnala; non esiste una migrazione automatica.

La [pagina sui miglioramenti](Miglioramenti-Futuri) distingue queste possibilità dalle funzionalità già implementate.
