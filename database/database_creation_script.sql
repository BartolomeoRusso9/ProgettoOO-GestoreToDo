/**
 * Comando per eliminazione permanente di una tabella dal database, se già presente.
 */
DROP TABLE IF EXISTS checklist_items;
DROP TABLE IF EXISTS shared_tasks;
DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS bacheche;
DROP TABLE IF EXISTS users;

/**
 * Comando per la creazione della tabella users nel database, con attributi:
		- user_id   Id dell’utente (Chiave Primaria Serializzata)
		- username 	Username dell’utente (Valore univoco)
        - password  Password dell’utente
*/
-- TABELLA UTENTI
CREATE TABLE users (
                       user_id SERIAL PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL
);

/**
 * Comando per la creazione della tabella bacheche nel database, con attributi:
		- bacheca_id  	Id della bacheca (Chiave Primaria Serializzata)
		- user_id 	Id dell’utente (Chiave Esterna da users)
- titolo Titolo della bacheca 
*/
-- TABELLA BACHECHE
CREATE TABLE bacheche (
                          bacheca_id SERIAL PRIMARY KEY,
                          user_id INT NOT NULL,
                          titolo VARCHAR(50) NOT NULL,

                          FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

/**
 * Comando per la creazione della tabella tasks nel database, con attributi:
		- task_id  	Id della task (Chiave Primaria Serializzata)
		- user_id 	Id dell’utente (Chiave Esterna da users)
        - bacheca_id Id della bacheca  (Chiave Esterna da bacheche)
        - title Titolo della task
        - description Descrizione della task
        - deadline Data di scadenza della task
        - status Stato di completamento della task (‘Da Fare’ di default, ‘Completato’)
*/
-- TABELLA TASKS
CREATE TABLE tasks (
                       task_id SERIAL PRIMARY KEY,
                       user_id INT NOT NULL,
                       bacheca_id INT NOT NULL,
                       title VARCHAR(100) NOT NULL,
                       description TEXT,
                       deadline DATE,
                       status VARCHAR(20) DEFAULT 'Da Fare',

                       FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                       FOREIGN KEY (bacheca_id) REFERENCES bacheche(bacheca_id) ON DELETE CASCADE
);

/**
 * Comando per la creazione della tabella shared_tasks nel database, con attributi:
		- task_id  	Id della task (Chiave Esterna da tasks)
		- user_id 	Id dell’utente (Chiave Esterna da users)
*/
-- TABELLA CONDIVISIONE
CREATE TABLE shared_tasks (
                              task_id INT NOT NULL,
                              user_id INT NOT NULL,
                              PRIMARY KEY (task_id, user_id),

                              FOREIGN KEY (task_id) REFERENCES tasks(task_id) ON DELETE CASCADE,
                              FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

/**
 * Comando per la creazione della tabella checklist_items nel database, con attributi:
		- item_id  	Id della sotto-attività (Chiave Primaria Serializzata)
		- task_id 	Id della task (Chiave Esterna da tasks)
        - name Nome della sotto-attività  
        - title Titolo della task
        - is_completed Stato di completamento della sotto-attività (true/false)
*/
-- TABELLA CHECKLIST
CREATE TABLE checklist_items (
                                 item_id SERIAL PRIMARY KEY,
                                 task_id INT NOT NULL,
                                 name VARCHAR(255) NOT NULL,
                                 is_completed BOOLEAN DEFAULT FALSE,

                                 FOREIGN KEY (task_id) REFERENCES tasks(task_id) ON DELETE CASCADE
);