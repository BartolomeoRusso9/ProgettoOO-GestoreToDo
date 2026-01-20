package model;

/**
 * Classe che rappresenta una bacheca nel Task Manager.
 * Una bacheca raggruppa le task di un utente.
 */
public class Bacheca {

    /**
     * Identificatore univoco della bacheca.
     */
    private final int id;

    /**
     * Titolo della bacheca (Università, Lavoro, Tempo Libero).
     */
    private final String titolo;

    /**
     * Costruttore parametrizzato.
     *
     * @param id 			Identificatore univoco della bacheca (> 0)
     * @param userId 		Identificatore dell'utente (> 0)
     * @param titolo 		Titolo della bacheca
     */
    public Bacheca(int id, int userId, String titolo) {
        this.id = id;
        /*
         * Identificatore univoco dell'utente.
         */
        this.titolo = titolo;
    }

    /**
     * Restituisce l'identificatore univoco della bacheca.
     *
     * @return id numerico della bacheca (sempre > 0)
     */
    public int getId() { return id; }

    /**
     * Restituisce il titolo della bacheca.
     *
     * @return titolo della bacheca
     */
    public String getTitolo() { return titolo; }

    /**
     * Rappresentazione in formato stringa del titolo della bacheca.
     *
     * @return stringa formattata che rappresenta il titolo
     */
    @Override
    public String toString() {
        return titolo;
    }
}