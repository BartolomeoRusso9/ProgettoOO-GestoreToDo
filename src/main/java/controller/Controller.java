package controller;

import dao.DatabaseDAO;
import interfaccedao.DAOInterface;
import model.Bacheca;
import model.ChecklistItem;
import model.Task;
import model.Utente;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller dell'applicazione Task Manager.
 * Gestisce la logica di business e funge da intermediario tra la View
 * e il DAO.
 * Segue il pattern MVC (Model-View-Controller).
 */
public class Controller {

    /**
     * Costruttore predefinito del Controller.
     * Inizializza il controller con le impostazioni di default.
     */
    public Controller() {
        // Costruttore vuoto intenzionale per warning Javadoc
    }

    /**
     * Logger per tracciare le operazioni del controller.
     */
    private static final Logger LOGGER = Logger.getLogger(Controller.class.getName());

    /**
     * Riferimento all'interfaccia DAO per l'accesso ai dati.
     * Inizializzato con l'implementazione concreta DatabaseDAO.
     */
    private final DAOInterface dao = new DatabaseDAO();

    /**
     * Utente attualmente loggato nell'applicazione.
     * Memorizzato dopo il login e utilizzato per le operazioni successive.
     */
    private Utente utenteCorrente = null;

    /**
     * Autentica un utente nel sistema.
     * Esegue l'hashing della password prima di delegare al DAO.
     *
     * @param username Username per l'autenticazione
     * @param password Password in testo semplice
     * @return oggetto {@link Utente} autenticato se le credenziali sono valide,
     * {@code null} in caso di credenziali errate
     * @throws IllegalArgumentException se username o password sono nulli/vuoti
     */
    public Utente login(String username, String password) {
        final String methodName = "login";
        LOGGER.entering(getClass().getName(), methodName, username);

        if (username == null || username.trim().isEmpty()) {
            LOGGER.warning("Login fallito: username vuoto");
            throw new IllegalArgumentException("Username non può essere vuoto");
        }
        if (password == null || password.trim().isEmpty()) {
            LOGGER.warning("Login fallito: password vuota");
            throw new IllegalArgumentException("Password non può essere vuota");
        }

        try {

            Utente utente = dao.login(username, password);

            if (utente != null) {
                this.utenteCorrente = utente;
                LOGGER.log(Level.INFO, "Login riuscito per utente: {0}", username);
                LOGGER.log(Level.INFO, "Utente corrente impostato - ID: {0}, Username: {1}",
                        new Object[]{utente.getId(), utente.getUsername()});
            } else {
                LOGGER.log(Level.WARNING, "Login fallito per utente: {0}", username);
            }

            LOGGER.exiting(getClass().getName(), methodName, utente);
            return utente;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante il login per utente: {0}", username);
            return null;
        }
    }

    /**
     * Registra un nuovo utente nel sistema.
     * Esegue l'hashing della password.
     *
     * @param username Username desiderato
     * @param password Password per il nuovo account
     * @return {@code true} se la registrazione ha successo,
     * {@code false} se lo username è già esistente o in caso di errore
     * @throws IllegalArgumentException se username o password non sono validi
     */
    public boolean register(String username, String password) {
        final String methodName = "register";
        LOGGER.entering(getClass().getName(), methodName, username);

        if (username == null || username.trim().isEmpty()) {
            LOGGER.warning("Registrazione fallita: username vuoto");
            throw new IllegalArgumentException("Username non può essere vuoto");
        }
        if (password == null || password.trim().isEmpty()) {
            LOGGER.warning("Registrazione fallita: password vuota");
            throw new IllegalArgumentException("Password non può essere vuota");
        }

        try {
            boolean successo = dao.register(username, password);

            if (successo) {
                LOGGER.log(Level.INFO, "Registrazione completata per utente: {0}", username);
            } else {
                LOGGER.log(Level.WARNING, "Registrazione fallita per utente: {0}", username);
            }

            LOGGER.exiting(getClass().getName(), methodName, successo);
            return successo;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore registrazione per utente: {0}", username);
            return false;
        }
    }


    /**
     * Crea un nuovo task per l'utente corrente.
     *
     * @param userId    Identificatore dell'utente proprietario (deve essere > 0)
     * @param titolo    Titolo del task
     * @param desc      Descrizione del task
     * @param scadenza  Data di scadenza
     * @param bachecaId Identificatore della bacheca di appartenenza (deve essere > 0)
     * @return {@code true} se il task è stato creato con successo,
     * {@code false} in caso di errore
     * @throws IllegalArgumentException se parametri obbligatori non sono validi
     * @throws IllegalStateException    se l'utente non è loggato
     */
    public boolean creaNuovoTask(int userId, String titolo, String desc,
                                 Date scadenza, int bachecaId) {
        final String methodName = "creaNuovoTask";
        LOGGER.entering(getClass().getName(), methodName,
                new Object[]{userId, titolo, bachecaId});


        if (titolo == null || titolo.trim().isEmpty()) {
            LOGGER.warning("Creazione task fallita: titolo vuoto");
            throw new IllegalArgumentException("Il titolo non può essere vuoto");
        }
        if (bachecaId <= 0) {
            LOGGER.warning("Creazione task fallita: bachecaId non valido");
            throw new IllegalArgumentException("ID Bacheca non valido");
        }

        if (scadenza != null) {
            Date oggi = new Date(System.currentTimeMillis());
            if (scadenza.before(oggi)) {
                LOGGER.warning("Creazione task fallita: scadenza nel passato");
                throw new IllegalArgumentException("La scadenza non può essere nel passato");
            }
        }

        try {
            boolean creato = dao.createTask(userId, titolo.trim(), desc, scadenza, bachecaId);

            if (creato) {
                LOGGER.log(Level.INFO,
                        "Nuovo task creato - UserID: {0}, Titolo: {1}, Categoria: {2}",
                        new Object[]{userId, titolo, bachecaId});
            } else {
                LOGGER.log(Level.WARNING, "Creazione task fallita per UserID: {0}", userId);
            }

            LOGGER.exiting(getClass().getName(), methodName, creato);
            return creato;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore creazione task per UserID: {0}", userId);
            return false;
        }
    }

    /**
     * Prepara i dati per la visualizzazione in tabella, filtrandoli per bacheca e stato.
     *
     * @param bachecaId      Identificatore della bacheca
     * @param statoRichiesto Stato dei task da recuperare ("Da Fare" o "Completato")
     * @return Lista di array di oggetti (ID, Titolo, Data di Scadenza, Descrizione) pronti per il TableModel
     */
    public List<Object[]> caricaDatiTabella(int bachecaId, String statoRichiesto) {
        List<Object[]> datiPronti = new ArrayList<>();
        List<Task> tasks = dao.getTasksByBacheca(bachecaId);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        for (Task t : tasks) {
            if (t.getStatus().equals(statoRichiesto)) {
                String dataIta = (t.getDeadline() != null) ? sdf.format(t.getDeadline()) : "";
                datiPronti.add(new Object[]{t.getId(), t.getTitle(), t.getDescription(), dataIta});
            }
        }
        return datiPronti;
    }

    /**
     * Aggiorna lo stato di un task esistente.
     *
     * @param taskId      Identificatore del task da aggiornare (deve essere > 0)
     * @param nuovoStatus Nuovo stato del task
     * @throws IllegalArgumentException se parametri non sono validi
     * @throws IllegalStateException    se l'utente non è autorizzato a modificare il task
     */
    public void spostaTask(int taskId, String nuovoStatus) {
        final String methodName = "spostaTask";
        LOGGER.entering(getClass().getName(), methodName, new Object[]{taskId, nuovoStatus});

        if (taskId <= 0) {
            LOGGER.warning("Spostamento task fallito: taskId non valido");
            throw new IllegalArgumentException("ID task non valido");
        }
        if (nuovoStatus == null || nuovoStatus.trim().isEmpty()) {
            LOGGER.warning("Spostamento task fallito: stato vuoto");
            throw new IllegalArgumentException("Il nuovo stato non può essere vuoto");
        }

        try {
            dao.updateStatus(taskId, nuovoStatus.trim());
            LOGGER.log(Level.INFO,
                    "Stato task aggiornato - TaskID: {0}, Nuovo stato: {1}",
                    new Object[]{taskId, nuovoStatus});

        } catch (Exception e) {
            throw new IllegalStateException("Errore durante l aggiornamento del task: " + taskId, e);
        }

        LOGGER.exiting(getClass().getName(), methodName);
    }

    /**
     * Elimina un task dal sistema.
     *
     * @param taskId Identificatore del task da eliminare (deve essere > 0)
     * @throws IllegalArgumentException se taskId non è valido
     * @throws IllegalStateException    se l'utente non è autorizzato a eliminare il task
     */
    public void eliminaTask(int taskId) {
        final String methodName = "eliminaTask";
        LOGGER.entering(getClass().getName(), methodName, taskId);

        if (taskId <= 0) {
            LOGGER.warning("Eliminazione task fallita: taskId non valido");
            throw new IllegalArgumentException("ID task non valido");
        }
        try {
            boolean eliminato = dao.deleteTask(taskId);

            if (eliminato) {
                LOGGER.log(Level.INFO, "Task eliminato - TaskID: {0}", taskId);
            } else {
                LOGGER.log(Level.WARNING, "Eliminazione task fallita - TaskID: {0} non trovato", taskId);
                throw new IllegalStateException("Task non trovato o non autorizzato");
            }

        } catch (Exception e) {
            throw new IllegalStateException("Errore durante l eliminazione del task: " + taskId, e);
        }

        LOGGER.exiting(getClass().getName(), methodName);
    }

    /**
     * Condivide un task con un altro utente.
     *
     * @param taskId        Identificatore del task da condividere (deve essere > 0)
     * @param amicoUsername Username del destinatario
     * @return {@code true} se la condivisione ha successo,
     * {@code false} se il task o l'utente non esistono
     * @throws IllegalArgumentException se parametri non sono validi
     */
    public boolean condividiTask(int taskId, String amicoUsername) {
        final String methodName = "condividiTask";
        LOGGER.entering(getClass().getName(), methodName, new Object[]{taskId, amicoUsername});

        if (taskId <= 0) {
            LOGGER.warning("Condivisione task fallita: taskId non valido");
            throw new IllegalArgumentException("ID task non valido");
        }
        if (amicoUsername == null || amicoUsername.trim().isEmpty()) {
            LOGGER.warning("Condivisione task fallita: username amico vuoto");
            throw new IllegalArgumentException("Username amico non può essere vuoto");
        }

        try {
            boolean condiviso = dao.shareTask(taskId, amicoUsername.trim());

            if (condiviso) {
                LOGGER.log(Level.INFO,
                        "Task condiviso - TaskID: {0} con utente: {1}",
                        new Object[]{taskId, amicoUsername});
            } else {
                LOGGER.log(Level.WARNING,
                        "Condivisione task fallita - TaskID: {0} o utente: {1} non trovati",
                        new Object[]{taskId, amicoUsername});
            }

            LOGGER.exiting(getClass().getName(), methodName, condiviso);
            return condiviso;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore condivisione del task: {0}", taskId);
            return false;
        }
    }

    /**
     * Recupera le bacheche dell'utente.
     *
     * @param userId Identificatore dell'utente (Deve essere > 0)
     * @return Lista di oggetti Bacheca
     * @throws IllegalArgumentException se userId non è valido
     */
    public List<Bacheca> getBacheche(int userId) {
        return dao.getBacheche(userId);
    }

    /**
     * Imposta manualmente l'utente corrente (utile quando si cambia vista).
     *
     * @param utente L'utente da impostare come loggato
     */
    public void setUtenteCorrente(Utente utente) {
        this.utenteCorrente = utente;
    }


    /**
     * Recupera tutti gli elementi della checklist di un task.
     *
     * @param taskId Identificatore del task (deve essere > 0)
     * @return lista di elementi della checklist,
     * lista vuota se il task non ha checklist
     * @throws IllegalArgumentException se taskId non è valido
     */
    public List<ChecklistItem> recuperaChecklist(int taskId) {
        final String methodName = "recuperaChecklist";
        LOGGER.entering(getClass().getName(), methodName, taskId);

        if (taskId <= 0) {
            LOGGER.warning("Recupero checklist fallito: taskId non valido");
            throw new IllegalArgumentException("ID task non valido");
        }

        try {
            List<ChecklistItem> checklist = dao.getChecklist(taskId);

            LOGGER.log(Level.FINE,
                    "Recuperati {0} elementi checklist per TaskID: {1}",
                    new Object[]{checklist.size(), taskId});

            LOGGER.exiting(getClass().getName(), methodName, checklist);
            return checklist;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore recupero checklist per TaskID: {0}", taskId);
            return List.of();
        }
    }

    /**
     * Aggiunge un nuovo elemento alla checklist di un task.
     *
     * @param taskId Identificatore del task (deve essere > 0)
     * @param nome   Nome del nuovo elemento
     * @throws IllegalArgumentException se parametri non sono validi
     */
    public void aggiungiVoceChecklist(int taskId, String nome) {
        final String methodName = "aggiungiVoceChecklist";
        LOGGER.entering(getClass().getName(), methodName, new Object[]{taskId, nome});

        if (taskId <= 0) {
            LOGGER.warning("Aggiunta voce checklist fallita: taskId non valido");
            throw new IllegalArgumentException("ID task non valido");
        }
        if (nome == null || nome.trim().isEmpty()) {
            LOGGER.warning("Aggiunta voce checklist fallita: nome vuoto");
            throw new IllegalArgumentException("Il nome della voce non può essere vuoto");
        }

        try {
            dao.addChecklistItem(taskId, nome.trim());
            LOGGER.log(Level.INFO,
                    "Voce checklist aggiunta - TaskID: {0}, Nome: {1}",
                    new Object[]{taskId, nome});

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore aggiunta voce checklist per TaskID: {0}", taskId);
        }

        LOGGER.exiting(getClass().getName(), methodName);
    }

    /**
     * Carica i dati dei task condivisi con l'utente corrente.
     * @param userId ID dell'utente
     * @param statoRichiesto Stato del task da filtrare ("Da Fare" o "Completato")
     * @return Lista di oggetti per la tabella
     */
    public List<Object[]> caricaDatiCondivisi(int userId, String statoRichiesto) {
        List<Object[]> datiPronti = new ArrayList<>();
        List<Task> tasks = dao.getTasksSharedWithUser(userId);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        for (Task t : tasks) {
            if (t.getStatus().equals(statoRichiesto)) {
                String dataIta = (t.getDeadline() != null) ? sdf.format(t.getDeadline()) : "";
                datiPronti.add(new Object[]{ t.getId(), t.getTitle(), t.getDescription(), dataIta});
            }
        }
        return datiPronti;
    }

    /**
     * Cambia lo stato di completamento di un elemento della checklist.
     * Se tutti gli elementi sono completati, il task viene automaticamente
     * contrassegnato come "Completato".
     *
     * @param itemId Identificatore dell'elemento (deve essere > 0)
     * @param taskId Identificatore del task (deve essere > 0)
     * @return {@code true} se il task è stato automaticamente completato,
     * {@code false} altrimenti
     * @throws IllegalArgumentException se parametri non sono validi
     */
    public boolean spuntaVoce(int itemId, int taskId) {
        final String methodName = "spuntaVoce";
        LOGGER.entering(getClass().getName(), methodName, new Object[]{itemId, taskId});

        if (itemId <= 0 || taskId <= 0) {
            LOGGER.warning("Spunta voce fallita: ID non validi");
            throw new IllegalArgumentException("ID elemento o task non validi");
        }

        try {
            boolean taskCompletato = dao.toggleChecklistItem(itemId, taskId);

            if (taskCompletato) {
                LOGGER.log(Level.INFO, "Task completato automaticamente - TaskID: {0}", taskId);
            } else {
                LOGGER.log(Level.FINE,
                        "Voce checklist spuntata - ItemID: {0}, TaskID: {1}",
                        new Object[]{itemId, taskId});
            }

            LOGGER.exiting(getClass().getName(), methodName, taskCompletato);
            return taskCompletato;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore spunta voce checklist: {0}", itemId);
            return false;
        }
    }

    /**
     * Elimina una voce della checklist.
     * @param itemId Identificatore della voce da eliminare
     */
    public void eliminaVoceChecklist(int itemId) {
        if (itemId <= 0) return;
        dao.deleteChecklistItem(itemId);
    }

    /**
     * Effettua il logout dell'utente corrente.
     */
    public void logout() {
        String username = (utenteCorrente != null ? utenteCorrente.getUsername() : "nessuno");
        LOGGER.log(Level.INFO, "Logout utente: {0}", username);
        utenteCorrente = null;
    }
}