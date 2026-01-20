package model;

import java.sql.Date;

/**
 * Classe che rappresenta una task (attività) nel Task Manager.
 * Una task è un'attività assegnata a un utente con scadenza (deadline), una bacheca di appartenenza
 * e stato ("Da fare", "Completato").
 */
public class Task {
    
    /**
     * Identificatore univoco della task.
     */
    private int id;
    
    /**
     * Identificatore dell'utente proprietario della task.
     * Riferimento a Utente.
     */
    private int userId;

    /**
     * Identificatore della bacheca di appartenenza.
     * Riferimento a Bacheca.
     */
    private int bachecaId;
    
    /**
     * Titolo breve della task.
     */
    private String title;
    
    /**
     * Descrizione dettagliata della task. Può essere più lunga del titolo.
     */
    private String description;
    
    /**
     * Data di scadenza della task. Utilizza java.sql.Date per compatibilità con database.
     */
    private Date deadline;
    
    /**
     * Stato corrente di avanzamento della task.
     */
    private String status;

    /**
     * Costruttore completo per creare una nuova task.
     * 
     * @param id          Identificatore univoco della task (> 0)
     * @param userId      Identificatore dell'utente proprietario (> 0)
     * @param title       Titolo della task
     * @param description Descrizione dettagliata
     * @param deadline    Data di scadenza
     * @param bachecaId   Identificatore univoco della bacheca (> 0)
     * @param status      Stato della task 
     * 
     * @throws IllegalArgumentException se i parametri non sono validi
     */
    public Task(int id, int userId, String title, String description, 
                Date deadline, int bachecaId, String status) {
        
        if (id <= 0) {
            throw new IllegalArgumentException("L'ID task deve essere maggiore di 0");
        }
        if (userId <= 0) {
            throw new IllegalArgumentException("L'ID utente deve essere maggiore di 0");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Il titolo non può essere nullo o vuoto");
        }
        if (title.length() > 100) {
            throw new IllegalArgumentException("Il titolo non può superare 100 caratteri");
        }
        if (bachecaId <= 0) {
            throw new IllegalArgumentException("L'ID bacheca deve essere maggiore di 0");
        }
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Lo stato non può essere nullo o vuoto");
        }

        this.id = id;
        this.userId = userId;
        this.title = title.trim();
        this.description = (description != null) ? description.trim() : "";
        this.deadline = deadline;
        this.bachecaId = bachecaId;
        this.status = status.trim();
    }

    /**
     * Restituisce l'identificatore univoco della task.
     * 
     * @return ID numerico della task (sempre > 0)
     */
    public int getId() { 
        return id; 
    }

    /**
     * Restituisce il titolo della task.
     * 
     * @return titolo della task 
     */
    public String getTitle() { 
        return title; 
    }
    
    /**
     * Restituisce la descrizione dettagliata della task.
     * 
     * @return descrizione della task (stringa vuota se non presente)
     */
    public String getDescription() { 
        return description; 
    }

    /**
     * Restituisce lo stato corrente della task.
     * 
     * @return stato di avanzamento
     */
    public String getStatus() { 
        return status; 
    }

    /**
     * Restituisce la data di scadenza del task.
     * @return data di scadenza
     */
    public Date getDeadline() { return deadline; }

    /**
     * Rappresentazione in formato stringa della task.
     * Utile per logging e debug.
     * 
     * @return stringa formattata che rappresenta la task
     */
    @Override
    public String toString() {
        return String.format(
            "Task[id=%d, userId=%d, title='%s', deadline=%s, bachecaId=%d, status='%s']",
            id, userId, title, 
            (deadline != null) ? deadline.toString() : "Nessuna",
            bachecaId, status
        );
    }
    
    /**
     * Confronta questa task con un altro oggetto per verificarne l'uguaglianza.
     * Due task sono considerati uguali se hanno lo stesso id.
     * 
     * @param obj oggetto da confrontare
     * @return {@code true} se gli oggetti sono uguali, {@code false} altrimenti
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return id == task.id;
    }
    
    /**
     * Restituisce il codice hash della task, basato sull'id.
     * 
     * @return codice hash calcolato dall'id
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}