package main;

import gui.LoginView;
import javax.swing.SwingUtilities;

/**
 * Classe principale dell'applicativo Task Manager.
 * Da questa classe parte l'esecuzione dell'applicazione
 * e gestisce l'avvio dell'interfaccia grafica.
 */
public class Main {
    /**
     * Costruttore privato della classe Main per prevenire istanziazioni non necessarie.
     */
    private Main(){}

    /**
     * Il metodo main avvia l'applicazione schedulando la creazione
     * della finestra di login.
     * 1. {@code SwingUtilities.invokeLater()} schedula l'esecuzione
     * 2. La finestra {@code LoginView} viene creata e resa visibile
     * 3. Tutte le operazioni GUI vengono eseguite
     * @param args Argomenti da riga di comando (non usati)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }
}