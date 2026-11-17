# Principi SOLID

## Single Responsibility Principle

Le responsabilità sono distribuite in base al tipo di cambiamento:

- `AttaccoBase` cambia quando cambia la formula del danno.
- `RicaricaATurni` cambia quando cambia la politica di disponibilità.
- `Battaglia` cambia quando cambia l'ordine delle azioni o la determinazione dell'esito.
- `Partita` cambia quando cambia il ciclo della campagna.
- `HibernateProgressiRepository` cambia quando cambia l'accesso ai dati.
- Le schermate cambiano quando cambia la presentazione.

Per esempio, modificare i colori dei pulsanti richiede un intervento nella vista; modificare la percentuale di cura riguarda la regola del dominio. `ServizioGioco` coordina i casi d'uso senza duplicare la formula dell'attacco.

## Open/Closed Principle

`Battaglia` riceve `AzioneCombattimento` e `RegolaCura`, mentre le abilità delegano la disponibilità a `Ricarica`. Una nuova strategia può essere introdotta implementando il relativo contratto e collegandola nella costruzione degli oggetti.

Le configurazioni `ClasseEroe`, `TipoMostro` e `Castello` descrivono i contenuti del gioco. Aggiungere un nemico con statistiche diverse può riutilizzare `Mostro`, senza modificare il calcolo del danno.

L'estensione richiede comunque la registrazione nel catalogo e, quando necessario, nuove risorse o presentazioni. La configurazione di produzione usa i cataloghi statici `ClassiEroe` e `Castelli`.

## Liskov Substitution Principle

Le strategie di attacco lavorano con `Combattente`, mentre i servizi ricevono `ProgressiRepository`. Un'implementazione sostitutiva deve rispettare i contratti: vita entro i limiti, impossibilità di rianimare con una cura e operazioni del repository coerenti con lo slot.

La separazione consente di usare `EroeGiocabile` e `Mostro` nelle azioni standard e repository di test nei servizi. I test del dominio e dei servizi esercitano questi usi; costituiscono esempi verificabili, senza rappresentare una dimostrazione formale per ogni implementazione futura.

## Interface Segregation Principle

I contratti distinguono capacità diverse: `Combattente` descrive il combattimento, `Potenziabile` i bonus, `Ricarica` la disponibilità e `ProgressiRepository` la persistenza.

Un nemico implementa `Nemico`, senza dover gestire l'abilità e i bonus propri di `Eroe`. Una regola di cura espone cura e utilizzi rimanenti, senza includere operazioni di salvataggio o di interfaccia grafica.

## Dependency Inversion Principle

`ServizioProgressione` riceve un `ProgressiRepository`; `ServizioGioco` riceve una `FabbricaNemici`; `AbilitaConRicarica` riceve una `Ricarica`. Le dipendenze vengono passate ai costruttori.

L'implementazione Hibernate resta nel livello di persistenza. La vista collega i componenti di produzione durante l'avvio; nei test gli stessi servizi possono ricevere implementazioni alternative.

## Verifiche e limiti

La [suite di test](https://github.com/Manu24life/Chronicles-of-the-Cursed-Castles/tree/main/src/test/java/it/unicam/cs/mpgc/rpg125657/cursedcastles) include indipendenza delle ricariche, nuove istanze dei nemici, progressione, errori del repository e controller. Questi casi aiutano a verificare le separazioni descritte.

Il collegamento dei componenti di produzione si trova attualmente in `CoordinatoreInterfaccia.avvia()`. Estrarre questo assemblaggio in un componente dedicato renderebbe più esplicita la separazione tra avvio e navigazione; è una possibile evoluzione descritta in [Miglioramenti Futuri](Miglioramenti-Futuri).
