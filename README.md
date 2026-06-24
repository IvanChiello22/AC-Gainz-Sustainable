# AC-Gainz Sustainable

Benvenuto nel repository di **AC-Gainz Sustainable**, una piattaforma e-commerce full-stack, scalabile e containerizzata. Il progetto è focalizzato sulla vendita di prodotti online (presumibilmente integratori e articoli per il fitness, coerentemente con la radice "Gainz" del nome), con un set completo di funzionalità sia dal lato consumer che dal lato amministrativo.

## 📚 Panoramica del Progetto
L'applicazione è progettata per supportare l'intero flusso e-commerce: dalla visualizzazione iniziale della vetrina dei prodotti (con filtri e ricerca avanzati) alla gestione del carrello, login e registrazione utente sicura, fino ad arrivare al checkout per il completamento dell'ordine. Integra anche un'area amministrativa per gestire agevolmente il catalogo prodotti (CRUD).

Il software non bada solo all'affidabilità ma anche all'efficienza, testimoniata dall'ampia area dedicata ai microbenchmark architetturali ed agli stress-test.

---

## 🛠 Tecnologie Utilizzate in Dettaglio

### 1. Stack Backend
Il "cuore" del backend si appoggia sullo stack enterprise e adotta il pattern di progettazione **MVC (Model-View-Controller)**.
*   **Java EE (Servlets)**: Le API esposte dall'applicazione e le logiche di elaborazione web sono scritte mediante Jakarta/Java EE Servlets (es. `AdminServlet`, `CarrelloServlet`, e `SecurityFilter`), configurate all'interno di un Servlet-Container (come Apache Tomcat/TomEE) mediante il file di deployment `WEB-INF/web.xml`.
*   **Gestione Dati ed Entità (Model)**: Accesso al DB modellato tramite il pattern DAO (Data Access Object - `ProdottoDAO`, `UtenteDAO`).
*   **Maven**: Utilizzato come build automation e package manager del progetto (`pom.xml` in unione ai wrapper `mvnw`).

### 2. Stack Frontend
L'interfaccia utente è Server-Side Rendered e arricchita via browser per i refresh dinamici.
*   **JSP (JavaServer Pages)**: Generazione dinamica a runtime delle Views della web application (`index.jsp`, `Cart.jsp`, ecc...).
*   **HTML & CSS3**: Fogli di stile implementati in modo modulare (situati nella cartella `CSS/`) per separare accuratamente grafica ed impaginazione per la pagina (es. `Header.css`, `ProductCard.css`).
*   **JavaScript Vanilla/ES6+**: Logica lato browser per supportare filtri reattivi per i prodotti (`genericFilter.js`, `showTastes.js`), pop-up del carrello interattivo (`CartPopUp.js`) e pre-validazione dei form asincrona (`validateForm.js`).

### 3. Database
*   **Database Relazionale (SQL)**: Strutturato ed interrogato primariamente in sintassi RDBMS. (Il file `DB/CodiceSQL/` include DDL e DML del progetto).
*   **Connection Pooling (`ConPool.java`)**: Ricicla e condivide in maniera asincrona le connessioni al database tramite un pool (tipicamente usando Tomcat JDBC o HikariCP), riducendo radicalmente la latenza per l'accessibilità dei dati rispetto all'instaurare una rotta nuova di volta in volta.

### 4. Containerizzazione & DevOps
*   **Docker & Docker Compose**: Assicurano la scalabilità, la facile portabilità cross-platform e l'isolamento completo dei task di progetto.
    *   Il `Dockerfile` costruisce l'immagine immutabile del servizio.
    *   Il `docker-compose.yml` orchestra i server per innescare tutto con un solo comando (app web unita verosimilmente al demone generatore per il Database).

### 5. Testing & Performance Tuning
Punto di forza del progetto è la dedizione all'efficienza in termini computazionali e di carico, provata nell'implementazione di test complessi strutturati minuziosamente:
*   **JMH (Java Microbenchmark Harness)**: Nella cartella `src/test/java/benchmark/` risiedono benchmark metodici che stressano il sistema isolatamente misurando l'efficacia (al nanosecondo o in transazioni per secondo) di operazioni costose lato backend come esecuzione query in batch (`BatchQueryBenchmark`), filtri al volo json (`JsonFilterBenchmark`) e meccaniche stringenti d'ordine (`OrderProcessingBenchmark`).
*   **Apache JMeter ("jmeter/")**: Test di simulazione per comprendere come regge il server ai carichi di rete estremi. Si dirama simulando quattro piani utenza separati:
    1.  *Scenario_A_Anonymous*: Simulazione massiva di visitatori privi di utenza.
    2.  *Scenario_B_Registered*: Simulazione con utenze pre-generate che effettuano Login e visitano il proprio account.
    3.  *Scenario_C_CheckoutStress*: Stress della logica per la costruzione, validazione cart e chiusura checkout (pagamento/conclusione).
    4.  *Scenario_D_CartStress*: Stress per il solo impatto sul lifecycle (CRUD) degli elementi all'interno di un carrello online.

---

## 📁 Struttura del Repository

```text
AC-Gainz-Sustainable/
├── DB/                 # File per l'importazione ed avvio del database SQL
├── docker-compose.yml  # File di orchestrazione container
├── Dockerfile          # Regole di building container dell'applicazione web
├── jmeter/             # Scenari Apache JMeter (file `.jmx`) per Stress test
├── src/                
│   ├── main/           
│   │   ├── java/       # Logica backend: Controller Servlet, Modelli e DAO
│   │   └── webapp/     # Frontend Client: File CSS, JS, immagini JSP Views (inclusa dir `WEB-INF/`)
│   └── test/           # Suite contenente tutti i Microbenchmark in JMH
├── pom.xml             # Dipendenze Maven (Application Config)
├── mvnw / mvnw.cmd     # Script terminale per forzare auto-avvio maven
└── LICENSE             # Informazioni di copyright open source del progetto
```

---

## 🚀 Guida all'Avvio 

### Opzione 1: Esecuzione automatica con Docker (Scelta Consigliata)
Non avrai bisogno di alcuna dipendenza java/server configurata localmente.
1. Assicurati di aver clonato il repository e spostati nella directory su terminale.
2. Controlla di avere il demone di Docker (e Docker Compose) già in funzione nel tuo software.
3. Esegui la build & start dei nodi containerizzati invocando da bash il comando:
   ```bash
   docker-compose up --build -d
   ```
4. Apri il browser al localhost sulla porta specificata nel `.yml` per la web-app Tomcat.

### Opzione 2: Esecuzione Manuale Locale (Per Sviluppatori)
1. Installa ed avvia un server **Database** SQL basandoti sugli script sql nella cartella `/DB` e configura le credenziali d'accesso per il backend sistemando il blocco in `ConPool.java` (o contestualizzandolo per tomcat).
2. Assicurati che **Java JDK (11 o superiore)** e **Maven** siano mappati correttamente nel server di sviluppo.
3. Risolvi le dipendenze scaricando ed assemblando le classi eseguendo il tool maven interno:
   ```bash
   ./mvnw clean install
   ```
4. Carica il pacchetto esito `.war` compilato del webapp-module all'interno di un Application Server J2EE (i.e. **Apache Tomcat**) e apri il server.

---

## 📜 Licenza
Questo progetto include un set base per una licenza protetta. Puoi visualizzarne una ripartizione e le condizioni d'uso consultando il file sorgente integrato `LICENSE`.