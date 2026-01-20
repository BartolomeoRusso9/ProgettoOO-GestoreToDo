package gui;

import controller.Controller;

import javax.swing.*;
import java.awt.*;

/**
 * Interfaccia grafica per la registrazione di nuovi utenti nel Task Manager.
 * Fornisce un form per l'inserimento di username e password
 * con validazione dei dati.
 */
public class RegistrationView extends JFrame {

    /**
     * Identificatore univoco per la serializzazione della classe.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Controller per la gestione della logica di autenticazione.
     * Transient per evitare problemi di serializzazione
     * (nel caso in cui Controller non sia serializzabile).
     */
    private final transient Controller controller;

    /**
     * Campo per l'inserimento dell'username.
     */
    private final JTextField txtUsername;

    /**
     * Campo protetto per l'inserimento della password.
     * Nasconde i caratteri inseriti.
     */
    private final JPasswordField txtPassword;

    /**
     * Campo protetto per la conferma della password.
     * Usato per la verifica dell'inserimento da parte dell'utenti.
     */
    private final JPasswordField txtConfermaPassword;

    /**
     * Costruttore.
     * Inizializza il controller, configura la finestra e crea tutti i componenti grafici.
     * Imposta le proprietà della finestra e i listener degli eventi.
     */
    public RegistrationView() {
        this.controller = new Controller();

        setTitle("Task Manager - Registrazione");
        setSize(400, 300);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Username (min 3 alfanum):"));
        txtUsername = new JTextField();
        formPanel.add(txtUsername);

        formPanel.add(new JLabel("Password (min 5 alfanum):"));
        txtPassword = new JPasswordField();
        formPanel.add(txtPassword);

        formPanel.add(new JLabel("Conferma Password:"));
        txtConfermaPassword = new JPasswordField();
        formPanel.add(txtConfermaPassword);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton btnRegistra = new JButton("Registra");
        JButton btnAnnulla = new JButton("Annulla");

        btnRegistra.addActionListener(e -> eseguiRegistrazione());

        btnAnnulla.addActionListener(e -> {
            dispose();
            new LoginView().setVisible(true);
        });

        buttonPanel.add(btnRegistra);
        buttonPanel.add(btnAnnulla);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    /**
     * Gestisce logica di validazione e registrazione dell'utente.
     * Viene eseguito quando l'utente clicca sul button "Registra".
     * 1. Recupera i dati dai campi di input
     * 2. Valida il formato dell''username
     * 3. Valida il formato della password
     * 4. Verifica che le password siano uguali
     * 5. Chiama Controller per completare la registrazione
     * 6. Gestisce il feedback all'utente (successo/errore)
     * Le validazioni includono:
     * - Username: min. 3 caratteri, solo alfanumerici
     * - Password: min. 5 caratteri, solo alfanumerici
     * - Conferma password: deve coincidere con la password inserita
     */
    private void eseguiRegistrazione() {
        String user = txtUsername.getText();
        String pass = new String(txtPassword.getPassword());
        String conferma = new String(txtConfermaPassword.getPassword());

        if (user.length() < 3 || !user.matches("[a-zA-Z0-9]+")) {
            JOptionPane.showMessageDialog(this,
                    "L'username deve avere almeno 3 caratteri e contenere solo lettere o numeri.",
                    "Errore Formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (pass.length() < 5 || !pass.matches("[a-zA-Z0-9]+")) {
            JOptionPane.showMessageDialog(this,
                    "La password deve avere almeno 5 caratteri e contenere solo lettere o numeri.",
                    "Errore Formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!pass.equals(conferma)) {
            JOptionPane.showMessageDialog(this,
                    "Le password non coincidono.",
                    "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean successo = controller.register(user, pass);

        if (successo) {
            JOptionPane.showMessageDialog(this, "Registrazione avvenuta con successo!");
            dispose();
            new LoginView().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Username già esistente. Scegline un altro.",
                    "Errore Registrazione", JOptionPane.ERROR_MESSAGE);
        }
    }
}