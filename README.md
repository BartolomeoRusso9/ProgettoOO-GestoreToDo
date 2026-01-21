# ProgettoOO-GestoreToDo

# Task Manager - Object Orientation Project

**Applicativo Java per la gestione di task personali e condivisi.**

Questo progetto è stato sviluppato per l'esame di Object Orientation dell'Università Federico II. L'applicazione, dotata di interfaccia grafica realizzata in Java Swing, consente la gestione completa di una lista di attività con persistenza dei dati affidata al database relazionale PostgreSQL.

## Funzionalità

### Registrazione e Autenticazione

Sistema di sign-in e sign-up sicuro per gestire l'accesso ai propri dati.

### Gestione dei Task

* **Creazione:** Aggiunta di nuovi task con titolo, descrizione e data di scadenza.
* **Check-list:** Possibilità di suddividere un'attività complessa in sotto-task da spuntare singolarmente.
* **Cancellazione:** Rimozione dei task non più necessari.
* **Stato Completamento:** Possibilità di spuntare i task come "Completati".

### Dettagli e Organizzazione

* **Scadenze:** Gestione delle date di scadenza (Deadline) con controlli sulla validità temporale.
* **Organizzazione in Bacheche:** L'applicazione organizza il flusso di lavoro attraverso 3 Bacheche dedicate:
1. **Bacheca Università**
2. **Bacheca Lavoro**
3. **Bacheca Tempo Libero**

### Condivisione

* **Task Condivisi:** Possibilità di condividere un task con altri utenti registrati nella piattaforma.

---

## Requisiti

* Java 17 (o superiore)
* PostgreSQL
* Maven (per la gestione delle dipendenze)
* Client SQL (pgAdmin o DataGrip) per l'esecuzione dello script

---

## Installazione e Avvio

### 1. Clona repository

Apri il terminale e clona il progetto nella tua cartella di lavoro:

```bash
git clone https://github.com/BartolomeoRusso9/ProgettoOO-GestoreToDo.git

cd ProgettoOO-GestoreToDo
```
### 2. Importa database
1. Apri il tuo client per database.
2. Crea un nuovo database vuoto chiamato: `task_manager_db`.
3. Apri il Query Tool selezionando il nuovo database appena creato.
4. Esegui lo script presente nel file:
   `database/database_creation_script.sql`

### 3. Configura connessione al Database

Apri il file nel percorso: `src/main/java/database/ConnessioneDatabase.java`

Modifica le costanti inserendo la porta (se diversa da 5432) e la **password** del tuo database locale (quella scelta durante l’installazione di PostgreSQL):

```java
private static final String URL = "jdbc:postgresql://localhost:5432/task_manager_db";
private static final String USER = "postgres";
private static final String PASSWORD = "INSERIRE_TUA_PASSWORD"; //
```
### 4. Compila e Avvia l'applicativo
Esegui i comandi Maven da terminale.
```bash
mvn clean install
mvn exec:java -Dexec.mainClass="main.Main"
```
-----------------------------------

## Autori

* **Bartolomeo Russo — N86005210**
* **Desiree Quaranta — N86004705**
* **Sabrina Oliva — N86004167**
