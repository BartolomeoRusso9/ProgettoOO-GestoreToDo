package model;

/**
 * Classe che rappresenta un elemento di una checklist associato a una task.
 * Ogni task può avere una lista di elementi di checklist per suddividere
 * attività complesse in sotto-attività più gestibili.
 */
public class ChecklistItem {
    
    /**
     * Identificatore univoco dell'elemento della checklist.
     */
    private int id;
    
    /**
     * Identificatore della task a cui appartiene questo elemento.
     * Riferimento a Task.
     */
    private int taskId;
    
    /**
     * Nome dell'elemento della checklist.
     */
    private String name;
    
    /**
     * Flag che indica se l'elemento è stato completato.
     * Valore booleano: true = completato, false = da fare.
     */
    private boolean isCompleted;

    
    /**
     * Costruttore completo per creare un nuovo elemento di checklist.
     * 
     * @param id 			Identificatore univoco dell'elemento (> 0)
     * @param taskId 		Identificatore della task padre (> 0)
     * @param name 			Nome dell'elemento (
     * @param isCompleted 	Stato di completamento
     * 
     * @throws IllegalArgumentException se i parametri obbligatori non sono validi
     */
    public ChecklistItem(int id, int taskId, String name, boolean isCompleted) {
        if (id <= 0) {
            throw new IllegalArgumentException("L'ID dell'elemento deve essere maggiore di 0");
        }
        if (taskId <= 0) {
            throw new IllegalArgumentException("L'ID della task deve essere maggiore di 0");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome non può essere nullo o vuoto");
        }
        if (name.length() > 200) {
            throw new IllegalArgumentException("Il nome non può superare 200 caratteri");
        }
        
        this.id = id;
        this.taskId = taskId;
        this.name = name.trim();
        this.isCompleted = isCompleted;
    }


    /**
     * Restituisce l'identificatore univoco dell'elemento della checklist.
     * 
     * @return id numerico dell'elemento (sempre > 0)
     */
    public int getId() { 
        return id; 
    }
    
    /**
     * Restituisce l'identificatore della task a cui appartiene questo elemento.
     * 
     * @return id della task padre (sempre > 0)
     */
    public int getTaskId() { 
        return taskId; 
    }
    
    /**
     * Restituisce il nome dell'elemento.
     * 
     * @return nome dell'elemento
     */
    public String getName() { 
        return name; 
    }
    
    /**
     * Verifica se l'elemento della checklist è stato completato.
     * 
     * @return {@code true} se l'elemento è completato, {@code false} altrimenti
     */
    public boolean isCompleted() { 
        return isCompleted; 
    }

    /**
     * Rappresentazione in formato stringa dell'elemento della checklist.
     * Include uno stato visivo (✓, ✗) per indicare il completamento.
     * 
     * @return stringa formattata che rappresenta l'elemento
     */
    @Override
    public String toString() {
        char statusIcon = isCompleted ? '✓' : '✗';
        return String.format("[%c] %s (ID: %d, TaskID: %d)", 
                           statusIcon, name, id, taskId);
    }

    /**
     * Confronta questo elemento con un altro per verificarne l'uguaglianza.
     * Due elementi sono considerati uguali se hanno lo stesso id.
     * 
     * @param obj Oggetto da confrontare
     * @return {@code true} se gli oggetti sono uguali, {@code false} altrimenti
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ChecklistItem that = (ChecklistItem) obj;
        return id == that.id;
    }
    
    /**
     * Restituisce il codice hash dell'elemento, basato sull'id.
     * 
     * @return codice hash calcolato dall'id
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

}