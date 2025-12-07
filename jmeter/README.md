# Guida al Test di Carico con JMeter

Ho creato un Test Plan di JMeter pronto all'uso per testare il tuo e-commerce.

## File del Test Plan
Il file si trova in questa cartella: `Ecommerce_Load_Test.jmx`

## Prerequisiti
1.  **Java**: Assicurati di avere Java installato (JDK 8+).
2.  **JMeter**: Se non lo hai installato, su Mac puoi usare Homebrew:
    ```bash
    brew install jmeter
    ```
3.  **La tua App**: L'e-commerce deve essere avviato e raggiungibile (es. su `localhost:8080`).

## Come Eseguirlo

### Opzione A: Interfaccia Grafica (Consigliata per la prima volta)
1.  Apri il terminale e digita:
    ```bash
    jmeter
    ```
2.  Vai su **File > Open** e seleziona il file `Ecommerce_Load_Test.jmx` in questa cartella.
3.  Premi il pulsante **Start** (freccia verde in alto).
4.  Guarda i risultati in **View Results Tree** (per i dettagli) o **Summary Report** (per le statistiche).

### Opzione B: Riga di Comando (Consigliata per test reali)
Per non appesantire il test con la grafica, esegui questo comando dalla cartella del progetto:
```bash
jmeter -n -t jmeter/Ecommerce_Load_Test.jmx -l jmeter/risultati.jtl
```
*   `-n`: Non-GUI mode
*   `-t`: File del test plan
*   `-l`: File dove salvare i risultati

## Configurazione
Se il tuo server non gira su `localhost:8080`, modifica le impostazioni in:
*   **HTTP Request Defaults** (nel pannello di sinistra): Cambia "Server Name or IP" e "Port Number".

## Nota sui Dati
*   Lo scenario di Login usa `test@example.com` / `Password123!`. Assicurati che questo utente esista nel tuo database o modificalo nel Test Plan.
*   Gli ID dei prodotti sono impostati a `1`. Se i tuoi prodotti hanno ID diversi, aggiornali nei parametri delle richieste HTTP.

## Risultati
Per vedere i risultati mentre esegui il test, seleziona **View Results Tree** o **Summary Report** e premi il pulsante **Start** (freccia verde).
