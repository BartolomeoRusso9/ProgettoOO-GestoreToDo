package gui;

import controller.Controller;
import model.Utente;

import javax.swing.*;

/**
 * Classe che rappresenta la finestra di login dell'applicazione Task Manager.
 * Fornisce un'interfaccia grafica per l'autenticazione degli utenti, con campi per
 * username e password e pulsanti per il login e la registrazione.
 * Una volta effettuato il login con successo, la finestra viene chiusa e viene aperta la dashboard principale dell'applicazione.
 */
public class LoginView extends JFrame {

    private static final long serialVersionUID = 1L;

    /** Campo di testo per l'inserimento dello username */
    private final JTextField userText;

    /** Campo di testo per l'inserimento della password */
    private final JPasswordField passText;

    /**
     * Controller per la gestione della logica di autenticazione.
     * Contrassegnato come transient per evitare problemi di serializzazione nel caso in cui Controller non sia serializzabile.
     */
    private final transient Controller controller = new Controller();

    /**
     * Costruttore che inizializza la finestra di login.
     * Crea e posiziona tutti i componenti grafici (etichette, campi di testo, pulsanti) e configura gli event listener per la gestione delle azioni dell'utente.
     * La finestra ha dimensioni fisse (350x220 pixel), non è ridimensionabile
     * e utilizza un layout assoluto per il posizionamento dei componenti.
     */
    public LoginView() {
        setTitle("Task Manager - Login");
        setSize(350, 220);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(30, 30, 80, 25);
        add(userLabel);

        userText = new JTextField(20);
        userText.setBounds(110, 30, 190, 25);
        add(userText);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(30, 70, 80, 25);
        add(passLabel);

        passText = new JPasswordField(20);
        passText.setBounds(110, 70, 190, 25);
        add(passText);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(40, 120, 120, 30);
        add(loginButton);

        JButton registerButton = new JButton("Registrati");
        registerButton.setBounds(170, 120, 120, 30);
        add(registerButton);


        /*
          Listener per il pulsante di login.
          Recupera le credenziali inserite dall'utente, effettua l'autenticazione
          tramite il controller e, in caso di successo, apre la dashboard.
          In caso di errore mostra un messaggio di avviso.
         */
        loginButton.addActionListener(e -> {
            String user = userText.getText();
            String pass = new String(passText.getPassword());

            Utente utente = controller.login(user, pass);

            if (utente != null) {
                dispose();
                new DashboardView(utente).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(LoginView.this,
                        "Dati errati!", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });

        /*
          Listener per il pulsante di registrazione.
          Chiude la finestra di login e apre la finestra di registrazione
          per la creazione di un nuovo account utente.
         */
        registerButton.addActionListener(e -> {
            dispose();
            new RegistrationView().setVisible(true);
        });
    }
}