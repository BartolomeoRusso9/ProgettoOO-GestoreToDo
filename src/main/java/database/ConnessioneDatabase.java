package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe per la gestione della connessione al database.
 * Fornisce un punto di accesso centralizzato alla connessione del database.
 */
public class ConnessioneDatabase {

    /**
     * Logger per tracciare eventi ed errori della connessione database.
     * Utilizza la classe stessa come nome del logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ConnessioneDatabase.class.getName());

    /**
     * URL di connessione JDBC al database (PostgresSQL).
     */
    private static final String URL = "jdbc:postgresql://localhost:5432/task_manager_db";

    /**
     * Nome utente per l'autenticazione al database.
     * Valore di default per PostgresSQL: "postgres"
     */
    private static final String USER = "postgres";

    /**
     * Password per l'autenticazione al database da inserire manualmente.
     */
    private static final String PASSWORD = "INSERIRE_TUA_PASSWORD";

    /**
     * Connessione condivisa tra tutti i componenti dell'applicazione.
     * Inizializzata a null e creata al primo accesso.
     */
    private static Connection connection = null;

    /**
     * Costruttore privato per prevenire l'istanziazione diretta.
     * Garantisce che solo questa classe possa creare istanze di se stessa.
     * @throws IllegalStateException se si tenta di istanziare la classe in altro modo
     */
    private ConnessioneDatabase() {
        if (connection != null) {
            throw new IllegalStateException("Classe singleton - usare getConnection()");
        }
    }

    /**
     * Restituisce l'unica istanza della connessione al database.
     * Se la connessione non esiste o è stata chiusa, ne crea una nuova.
     * @return oggetto {@link Connection} attivo al database
     * @throws IllegalStateException se si verifica un errore durante la connessione
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);

                connection.setAutoCommit(true); // Auto-commit abilitato di default

                LOGGER.info("Connessione al database stabilita con successo.");
                LOGGER.log(Level.FINE, "URL: {0}, User: {1}", new Object[]{URL, USER});
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Impossibile connettersi al database", e);
        }
        return connection;
    }
}