package interfaccedao;

import model.Bacheca;
import model.ChecklistItem;
import model.Task;
import model.Utente;
import java.sql.Date;
import java.util.List;

/**
 * Interfaccia principale del DAO per il Task Manager.
 * Definisce il contratto per tutte le operazioni di persistenza sui dati, 
 * le operazioni CRUD per tutte le entità del sistema 
 * e fornisce un'interfaccia unificata per l'accesso ai dati.
 */
public interface DAOInterface {
        
    /**
     * Autentica un utente nel sistema verificando username e password.
     * 
     * @param username 	Username per l'autenticazione 
     * @param password 	Password per l'autenticazione
     * @return oggetto {@link Utente} corrispondente se l'autenticazione ha successo,
     *         {@code null} se le credenziali sono errate
     *         
     * @throws IllegalArgumentException se username o password sono nulli/vuoti
     * @throws RuntimeException se si verifica un errore di accesso al database
     */
    Utente login(String username, String password);
    
    /**
     * Registra un nuovo utente nel sistema.
     * 
     * @param username 	Username desiderato (univoco)
     * @param password 	Password per il nuovo account 
     * @return {@code true} se la registrazione ha successo,
     *         {@code false} se lo username è già esistente
     *         
     * @throws IllegalArgumentException se username o password sono nulli/vuoti
     * @throws RuntimeException se si verifica un errore di accesso al database
     */
    boolean register(String username, String password);
        
    /**
     * Crea un nuovo task associato a un utente e a una bacheca.
     * 
     * @param userId 		Identificatore dell'utente proprietario (deve essere > 0)
     * @param title 		Titolo del task 
     * @param description 	Descrizione 
     * @param deadline 		Data di scadenza 
     * @param bachecaId 	Identificatore della bacheca (deve essere > 0)
     * @return {@code true} se il task è stato creato con successo,
     *         {@code false} in caso di errore
     *         
     * @throws IllegalArgumentException se parametri obbligatori non sono validi
     * @throws RuntimeException se si verifica un errore di accesso al database
     */
    boolean createTask(int userId, String title, String description, 
                      Date deadline, int bachecaId);
    
    /**
     * Aggiorna lo stato di un task esistente.
     * 
     * @param taskId 	Identificatore del task da aggiornare (deve essere > 0)
     * @param newStatus Nuovo stato del task 
     * 
     * @throws IllegalArgumentException se taskId &lt;= 0 o newStatus è nullo/vuoto
     * @throws RuntimeException se si verifica un errore di accesso al database
     * @throws IllegalStateException se il task non esiste o non può essere aggiornato
     */
    void updateStatus(int taskId, String newStatus);
    
    /**
     * Elimina un task dal sistema.
     * 
     * @param taskId Identificatore del task da eliminare (deve essere > 0)
     * @return {@code true} se il task è stato eliminato con successo,
     *         {@code false} se il task non esiste o non può essere eliminato
     *         
     * @throws IllegalArgumentException se taskId &lt;= 0
     * @throws RuntimeException se si verifica un errore di accesso al database
     */
    boolean deleteTask(int taskId);
    
    /**
     * Condivide un task con un altro utente.
     * 
     * @param taskId 			Identificatore del task da condividere (deve essere > 0)
     * @param usernameTarget 	Username del destinatario
     * @return {@code true} se la condivisione ha successo,
     *         {@code false} se il task o l'utente non esistono
     *         
     * @throws IllegalArgumentException se parametri non sono validi
     * @throws RuntimeException se si verifica un errore di accesso al database
     */
    boolean shareTask(int taskId, String usernameTarget);
        
    /**
     * Recupera tutti gli elementi della checklist associati a un task.
     * 
     * @param taskId Identificatore del task (deve essere > 0)
     * @return lista di {@link ChecklistItem} associati al task,
     *         lista vuota se il task non ha checklist o non esiste
     *         
     * @throws IllegalArgumentException se taskId &lt;= 0
     * @throws RuntimeException se si verifica un errore di accesso al database
     */
    List<ChecklistItem> getChecklist(int taskId);
    
    /**
     * Aggiunge un nuovo elemento alla checklist di un task.
     * 
     * @param taskId 	Identificatore del task (deve essere > 0)
     * @param name 		Nome del nuovo elemento 
     * 
     * @throws IllegalArgumentException se parametri non sono validi
     * @throws RuntimeException se si verifica un errore di accesso al database
     * @throws IllegalStateException se il task non esiste
     */
    void addChecklistItem(int taskId, String name);
    
    /**
     * Cambia lo stato di completamento di un elemento della checklist.
     * Se l'elemento è completato, lo marca come non completato e viceversa.
     * 
     * @param itemId 	Identificatore dell'elemento della checklist (deve essere > 0)
     * @param taskId 	Identificatore del task di appartenenza (deve essere > 0)
     * @return {@code true} se l'operazione ha successo,
     *         {@code false} se l'elemento non esiste
     *         
     * @throws IllegalArgumentException se itemId o taskId &lt;= 0
     * @throws RuntimeException se si verifica un errore di accesso al database
     */
    boolean toggleChecklistItem(int itemId, int taskId);
        
    /**
     * Recupera tutti i task di una specifica bacheca.
     * 
     * @param bachecaId 	Identificatore della bacheca (deve essere > 0)
     * @return lista di {@link Task} della bacheca,
     *         lista vuota se non ci sono task
     *         
     * @throws IllegalArgumentException se bachecaId &lt;= 0
     * @throws RuntimeException se si verifica un errore di accesso al database
     */
    List<Task> getTasksByBacheca(int bachecaId);


    /**
     * Elimina una voce specifica dalla checklist.
     * @param itemId ID della voce da eliminare
     */
    void deleteChecklistItem(int itemId);


    /**
     * Recupera tutte le bacheche di un utente.
     *
     * @param userId Identificatore dell'utente proprietario (deve essere > 0)
     * @return lista di {@link Bacheca} dell'utente,
     *         lista vuota se non ci sono bacheche
     *
     * @throws IllegalArgumentException se userId &lt;= 0
     * @throws RuntimeException se si verifica un errore di accesso al database
     */
    List<Bacheca> getBacheche(int userId);

    /**
     * Recupera i task che sono stati condivisi con l'utente specificato.
     * @param userId ID dell'utente che riceve la condivisione
     * @return Lista dei task condivisi
     */
    List<Task> getTasksSharedWithUser(int userId);
}