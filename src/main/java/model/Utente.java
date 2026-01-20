package model;

/**
 * Classe che rappresenta un utente del Task Manager.
 * Questa classe modella l'entità Utente con le sue proprietà private fondamentali 
 * e usa getter pubblici.
 */
public class Utente {
    
    /**
     * Identificatore univoco dell'Utente.
     */
    private int id;
    
    /**
     * Nome utente utilizzato per l'autenticazione e la visualizzazione.
     * Deve essere unico all'interno del sistema.
     */
    private String username;

    /**
     * Costruttore completo per creare una nuova istanza di Utente.
     * 
     * @param id Identificatore univoco dell'utente (> 0)
     * @param username Username per autenticazione 
     * @throws IllegalArgumentException se {@code id <= 0} o {@code username} è null/vuoto
     */
    public Utente(int id, String username) {

    	if (id <= 0) {
            throw new IllegalArgumentException("L'ID utente deve essere maggiore di 0");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome utente non può essere nullo o vuoto");
        }
        
        this.id = id;
        this.username = username.trim();
    }

    /**
     * Restituisce l'id (identificatore univoco) dell'utente.
     * 
     * @return ID numerico dell'utente (sempre > 0)
     */
    public int getId() { 
        return id; 
    }
    
    /**
     * Restituisce il nome utente per l'autenticazione e visualizzazione.
     * 
     * @return username 
     */
    public String getUsername() { 
        return username; 
    }
    
    /**
     * Rappresentazione in formato stringa dell'oggetto Utente.
     * Utile per logging e debug.
     * 
     * @return stringa formattata che rappresenta l'utente
     */
    @Override
    public String toString() {
        return String.format("Utente[id=%d, username=%s]", id, username);
    }
    
    /**
     * Confronta questo utente con un altro oggetto per verificarne l'uguaglianza.
     * Due utenti sono considerati uguali se hanno lo stesso ID.
     * 
     * @param obj Oggetto da confrontare
     * @return {@code true} se gli oggetti sono uguali, {@code false} altrimenti
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Utente utente = (Utente) obj;
        return id == utente.id;
    }
    
    /**
     * Restituisce il codice hash dell'utente, basato sull'id.
     * 
     * @return codice hash calcolato dall'id
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}