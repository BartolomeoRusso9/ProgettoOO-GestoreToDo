package dao;

import database.ConnessioneDatabase;
import interfaccedao.DAOInterface;
import model.Bacheca;
import model.ChecklistItem;
import model.Task;
import model.Utente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione dell'interfaccia DAOInterface per l'accesso al database.
 * Gestisce operazioni CRUD per utenti, bacheche, task e checklist.
 */
public class DatabaseDAO implements DAOInterface {

    /**
     * Stringhe costanti che rappresentano i nomi delle colonne del database.
     */
    private static final String COL_USER_ID = "user_id";
    private static final String COL_USERNAME = "username";
    private static final String COL_TASK_ID = "task_id";
    private static final String COL_TITLE = "title";
    private static final String COL_DESC = "description";
    private static final String COL_DEADLINE = "deadline";
    private static final String COL_BACHECA_ID = "bacheca_id";
    private static final String COL_STATUS = "status";
    private static final String COL_ITEM_ID = "item_id";
    private static final String COL_NAME = "name";
    private static final String COL_IS_COMPLETED = "is_completed";
    private static final String COL_BACHECA_TITOLO = "titolo";

    /**
     * Costruttore predefinito della classe DatabaseDAO.
     */
    public DatabaseDAO() {
        //
    }

    /**
     * Autentica un utente nel sistema verificando username e password.
     * 
     * @param username Il nome utente dell'utente che tenta il login
     * @param password La password dell'utente che tenta il login
     * 
     * @return Un oggetto Utente se le credenziali sono corrette, null altrimenti
     */
    @Override
    public Utente login(String username, String password) {
        String query = "SELECT user_id, username FROM users WHERE username = ? AND password = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Utente(rs.getInt(COL_USER_ID), rs.getString(COL_USERNAME));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /**
     * Registra un nuovo utente nel sistema con bacheche di default.
     * 
     * @param username Il nome utente scelto per la registrazione
     * @param password La password scelta per la registrazione
     * 
     * @return true se la registrazione è avvenuta con successo, false altrimenti
     */
    @Override
    public boolean register(String username, String password) {
        String insertUserSql = "INSERT INTO users (username, password) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement psUser = null;
        ResultSet rs = null;

        try {
            conn = ConnessioneDatabase.getConnection();
            if (conn == null) return false;

            conn.setAutoCommit(false);

            psUser = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, username);
            psUser.setString(2, password);

            int affectedRows = psUser.executeUpdate();
            if (affectedRows == 0) {
                rollback(conn);
                return false;
            }

            int newUserId = -1;
            rs = psUser.getGeneratedKeys();
            if (rs.next()) {
                newUserId = rs.getInt(1);
            } else {
                rollback(conn);
                return false;
            }

            inserisciBachecheDefault(conn, newUserId);

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            rollback(conn);
            return false;
        } finally {
            closeOperations(rs, psUser, conn);
        }
    }

    /**
     * Inserisce le bacheche predefinite per un nuovo utente registrato.
     * Le bacheche sono: "Università", "Lavoro", "Tempo Libero".
     * 
     * @param conn  La connessione al database attiva
     * @param userId L'id dell'utente per cui creare le bacheche
     * 
     * @throws SQLException Se si verifica un errore durante l'operazione sul database
     */
    private void inserisciBachecheDefault(Connection conn, int userId) throws SQLException {
        String insertBachecaSql = "INSERT INTO bacheche (user_id, titolo) VALUES (?, ?)";

        try (PreparedStatement psBacheca = conn.prepareStatement(insertBachecaSql)) {
            psBacheca.setInt(1, userId);

            String[] defaults = {"Università", "Lavoro", "Tempo Libero"};

            for (String titolo : defaults) {
                psBacheca.setString(2, titolo);
                psBacheca.addBatch();
            }
            psBacheca.executeBatch();
        }
    }

    /**
     * Esegue il rollback di una transazione in caso di errore.
     * 
     * @param conn La connessione al database su cui eseguire il rollback
     */
    private void rollback(Connection conn) {
        try {
            if (conn != null) conn.rollback();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Chiude tutte le risorse del database (ResultSet, Statement, Connection)
     * in modo sicuro, gestendo eventuali eccezioni.
     * 
     * @param rs    Il ResultSet da chiudere
     * @param stmt  Lo Statement/PreparedStatement da chiudere
     * @param conn  La Connection da chiudere
     */
    private void closeOperations(ResultSet rs, Statement stmt, Connection conn) {
        try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        try {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Crea una nuova task nella bacheca specificata.
     * 
     * @param userId        L'id dell'utente che crea la task
     * @param title         Il titolo della task
     * @param description   La descrizione della task
     * @param deadline      La data di scadenza della task
     * @param bachecaId     L'id della bacheca in cui inserire il task
     * 
     * @return true se la task è stata creata con successo, false altrimenti
     */
    @Override
    public boolean createTask(int userId, String title, String description, Date deadline, int bachecaId) {
        String query = "INSERT INTO tasks (user_id, title, description, deadline, bacheca_id, status) VALUES (?, ?, ?, ?, ?, 'Da Fare')";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setDate(4, deadline);
            ps.setInt(5, bachecaId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    /**
     * Aggiorna lo stato di una task esistente.
     * 
     * @param taskId    L'id della task da aggiornare
     * @param newStatus Il nuovo stato da assegnare alla task
     */
    @Override
    public void updateStatus(int taskId, String newStatus) {
        String query = "UPDATE tasks SET status = ? WHERE task_id = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, taskId);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * Elimina una task dal database.
     * 
     * @param taskId L'id della task da eliminare
     * 
     * @return true se la task è stata eliminata con successo, false altrimenti
     */
    @Override
    public boolean deleteTask(int taskId) {
        String query = "DELETE FROM tasks WHERE task_id = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, taskId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    /**
     * Condivide una task con un altro utente del sistema.
     * 
     * @param taskId            L'id della task da condividere
     * @param usernameTarget    Il nome utente con cui condividere la task
     * 
     * @return true se la condivisione è avvenuta con successo, false altrimenti
     */
    @Override
    public boolean shareTask(int taskId, String usernameTarget) {
        String findUserSql = "SELECT user_id FROM users WHERE username = ?";
        String shareSql = "INSERT INTO shared_tasks (task_id, user_id) VALUES (?, ?)";
        try (Connection conn = ConnessioneDatabase.getConnection()) {
            int friendId = -1;
            try (PreparedStatement psUser = conn.prepareStatement(findUserSql)) {
                psUser.setString(1, usernameTarget);
                ResultSet rs = psUser.executeQuery();
                if (rs.next()) friendId = rs.getInt(COL_USER_ID);
            }
            if (friendId == -1) return false;
            try (PreparedStatement psShare = conn.prepareStatement(shareSql)) {
                psShare.setInt(1, taskId);
                psShare.setInt(2, friendId);
                psShare.executeUpdate();
                return true;
            }
        } catch (SQLException e) { return false; }
    }

    /**
     * Recupera tutti gli elementi della checklist associata a una task.
     * 
     * @param taskId L'id della task di cui recuperare la checklist
     * 
     * @return Una lista di ChecklistItem ordinati per id
     */
    @Override
    public List<ChecklistItem> getChecklist(int taskId) {
        List<ChecklistItem> items = new ArrayList<>();
        String query = "SELECT item_id, task_id, name, is_completed FROM checklist_items WHERE task_id = ? ORDER BY item_id";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, taskId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                items.add(new ChecklistItem(
                        rs.getInt(COL_ITEM_ID),
                        rs.getInt(COL_TASK_ID),
                        rs.getString(COL_NAME),
                        rs.getBoolean(COL_IS_COMPLETED)
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return items;
    }

    /**
     * Aggiunge un nuovo elemento alla checklist di una task.
     * 
     * @param taskId     L'id della task a cui aggiungere l'elemento
     * @param name       Il nome/descrizione del nuovo elemento della checklist
     */
    @Override
    public void addChecklistItem(int taskId, String name) {
        String query = "INSERT INTO checklist_items (task_id, name, is_completed) VALUES (?, ?, false)";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, taskId);
            pstmt.setString(2, name);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * Alterna lo stato di completamento di un elemento della checklist.
     * Se tutti gli elementi della checklist sono completati, 
     * lo stato della task viene automaticamente aggiornato a "Completato".
     * 
     * @param itemId L'id dell'elemento della checklist da modificare
     * @param taskId L'id della task a cui appartiene la checklist
     * 
     * @return true se lo stato della task è stato automaticamente aggiornato a "Completato", false altrimenti
     */
    @Override
    public boolean toggleChecklistItem(int itemId, int taskId) {
        boolean autoCompleted = false;
        String toggleSql = "UPDATE checklist_items SET is_completed = NOT is_completed WHERE item_id = ?";
        String checkSql = "SELECT (SELECT COUNT(*) FROM checklist_items WHERE task_id=?) as tot, " +
                "(SELECT COUNT(*) FROM checklist_items WHERE task_id=? AND is_completed=true) as done";
        try (Connection conn = ConnessioneDatabase.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(toggleSql)) {
                ps.setInt(1, itemId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setInt(1, taskId);
                ps.setInt(2, taskId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int tot = rs.getInt("tot");
                    int done = rs.getInt("done");
                    if (tot > 0 && tot == done) {
                        updateStatus(taskId, "Completato");
                        autoCompleted = true;
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return autoCompleted;
    }

    @Override
    public void deleteChecklistItem(int itemId) {
        String query = "DELETE FROM checklist_items WHERE item_id = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, itemId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Recupera tutte le task (incluse quelle condivise) appartenenti a una specifica bacheca.
     * 
     * @param bachecaId L'id della bacheca di cui recuperare le task
     * 
     * @return Una lista di Task appartenenti alla bacheca specificata
     */
    @Override
    public List<Task> getTasksByBacheca(int bachecaId) {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT DISTINCT t.task_id, t.user_id, t.title, t.description, t.deadline, t.bacheca_id, t.status " +
                "FROM tasks t LEFT JOIN shared_tasks st ON t.task_id = st.task_id " +
                "WHERE t.bacheca_id = ?";

        return getTasks(bachecaId, tasks, query);
    }

    private List<Task> getTasks(int bachecaId, List<Task> tasks, String query) {
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, bachecaId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tasks.add(new Task(
                        rs.getInt(COL_TASK_ID),
                        rs.getInt(COL_USER_ID),
                        rs.getString(COL_TITLE),
                        rs.getString(COL_DESC),
                        rs.getDate(COL_DEADLINE),
                        rs.getInt(COL_BACHECA_ID),
                        rs.getString(COL_STATUS)
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return tasks;
    }

    /**
     * Recupera tutte le bacheche appartenenti a un utente.
     * 
     * @param userId L'id dell'utente di cui recuperare le bacheche
     * 
     * @return Una lista di Bacheca appartenenti all'utente specificato
     */
    @Override
    public List<Bacheca> getBacheche(int userId) {
        List<Bacheca> bacheche = new ArrayList<>();
        String query = "SELECT bacheca_id, user_id, titolo FROM bacheche WHERE user_id = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bacheche.add(new Bacheca(
                        rs.getInt(COL_BACHECA_ID),
                        rs.getInt(COL_USER_ID),
                        rs.getString(COL_BACHECA_TITOLO)
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return bacheche;
    }

    public List<Task> getTasksSharedWithUser(int userId) {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT t.* FROM tasks t " +
                "JOIN shared_tasks st ON t.task_id = st.task_id " +
                "WHERE st.user_id = ?";

        return getTasks(userId, tasks, query);
    }
}