package gui;

import controller.Controller;
import model.Bacheca;
import model.ChecklistItem;
import model.Utente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

/**
 * Classe principale dell'interfaccia grafica (Dashboard).
 * Visualizza i task dell'utente organizzati per bacheche e permette di eseguire
 * tutte le operazioni principali (creazione, modifica, completamento, eliminazione).
 */
public class DashboardView extends JFrame {

    /**
     * Identificatore univoco per la serializzazione.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Costante per lo stato "Da Fare".
     */
    private static final String STATUS_TODO = "Da Fare";

    /**
     * Costante per lo stato "Completato".
     */
    private static final String STATUS_DONE = "Completato";

    /**
     * Utente attualmente loggato e visualizzato nella dashboard.
     */
    private final transient Utente currentUser;

    /**
     * Controller per la gestione della logica di business.
     * Dichiarato come transient per compatibilità serializzazione.
     */
    private final transient Controller controller;

    /**
     * Pannello principale della finestra.
     */
    private final JPanel mainPanel;

    /**
     * Pannello a schede per navigazione tra categorie.
     */
    private JTabbedPane tabbedPane;

    /**
     * Costruttore principale della dashboard.
     * Inizializza l'interfaccia grafica con tutte le tabelle e i componenti necessari.
     * @param user Utente loggato da visualizzare nella dashboard
     * @throws IllegalArgumentException se l'utente è null
     */
    public DashboardView(Utente user) {
        this.currentUser = user;
        this.controller = new Controller();
        this.controller.setUtenteCorrente(user);

        setTitle("Task Manager - " + user.getUsername());
        setSize(1000, 700);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout());

        createTopPanel();
        createTabbedPane();

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        setContentPane(mainPanel);

        refreshAllData();
    }

    /**
     * Crea il pannello superiore contenente i pulsanti principali.
     * Include funzionalità di creazione task, aggiornamento e logout.
     */
    private void createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnNuovoTask = new JButton("Nuovo Task");
        JButton btnAggiorna = new JButton("Aggiorna");
        JButton btnLogout = new JButton("Esci");

        btnNuovoTask.addActionListener(e -> mostraDialogNuovoTask());
        btnAggiorna.addActionListener(e -> refreshAllData());
        btnLogout.addActionListener(e -> {
            controller.logout();
            dispose();
            new LoginView().setVisible(true);
        });

        topPanel.add(btnNuovoTask);
        topPanel.add(btnAggiorna);
        topPanel.add(btnLogout);

        mainPanel.add(topPanel, BorderLayout.NORTH);
    }



    /**
     * Crea un modello di tabella standardizzato con colonne predefinite.
     * @return DefaultTableModel configurato con colonne Id, Titolo, Descrizione
     */
    private DefaultTableModel createModel() {
        DefaultTableModel model = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.addColumn("ID");
        model.addColumn("Titolo");
        model.addColumn("Descrizione");
        model.addColumn("Scadenza");
        return model;
    }

    /**
     * Nasconde la colonna Id in una tabella mantenendola nei dati ma non nella visualizzazione.
     * @param table la tabella in cui nascondere la colonna Id
     */
    private void nascondiColonnaID(JTable table) {
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
    }

    /**
     * Crea il pannello a schede dinamicamente in base alle bacheche dell'utente nel DB.
     * Ogni scheda contiene un pannello con tabelle per stati "Da Fare" e "Completato".
     */
    private void createTabbedPane() {
        tabbedPane = new JTabbedPane();
        refreshAllData();
    }

    /**
     * Ricarica tutte le bacheche dal DB e rigenera le tab.
     */
    private void refreshAllData() {
        tabbedPane.removeAll();
        List<Bacheca> bacheche = controller.getBacheche(currentUser.getId());

        for (Bacheca b : bacheche) {
            DefaultTableModel modelTodo = createModel();
            JTable tableTodo = new JTable(modelTodo);
            nascondiColonnaID(tableTodo);

            DefaultTableModel modelDone = createModel();
            JTable tableDone = new JTable(modelDone);
            nascondiColonnaID(tableDone);

            popolaTabelle(b.getId(), modelTodo, modelDone);

            JPanel panel = createCategoryPanel(tableTodo, tableDone);

            tabbedPane.addTab(b.getTitolo(), panel);
        }
        aggiungiTabCondivisi();
    }

    /**
     * Crea e aggiunge una scheda speciale al pannello per visualizzare i task condivisi.
     * Recupera dal database i task che altri utenti hanno condiviso con l'utente corrente,
     * li divide per stato ("Da Fare" e "Completato") e li visualizza in tabelle separate.
     */
    private void aggiungiTabCondivisi() {
        DefaultTableModel modelTodo = createModel();
        JTable tableTodo = new JTable(modelTodo);
        nascondiColonnaID(tableTodo);

        DefaultTableModel modelDone = createModel();
        JTable tableDone = new JTable(modelDone);
        nascondiColonnaID(tableDone);

        List<Object[]> todoData = controller.caricaDatiCondivisi(currentUser.getId(), STATUS_TODO);
        for (Object[] row : todoData) modelTodo.addRow(row);

        List<Object[]> doneData = controller.caricaDatiCondivisi(currentUser.getId(), STATUS_DONE);
        for (Object[] row : doneData) modelDone.addRow(row);

        JPanel panel = createCategoryPanel(tableTodo, tableDone);

        tabbedPane.addTab("Condivisi con me", panel);
    }

    /**
     * Popola i modelli delle tabelle chiedendo al controller i dati già filtrati.
     */
    private void popolaTabelle(int bachecaId, DefaultTableModel modelTodo, DefaultTableModel modelDone) {
        List<Object[]> todoData = controller.caricaDatiTabella(bachecaId, STATUS_TODO);
        for (Object[] row : todoData) {
            modelTodo.addRow(row);
        }

        List<Object[]> doneData = controller.caricaDatiTabella(bachecaId, STATUS_DONE);
        for (Object[] row : doneData) {
            modelDone.addRow(row);
        }
    }

    /**
     * Crea un pannello per una specifica categoria.
     * Il pannello contiene tabelle separate per stati "Da Fare" e "Completato", divise da uno split pane verticale.
     * @param tableTodo 	Tabella per task "Da Fare"
     * @param tableDone 	Tabella per task "Completato"
     * @return JPanel configurato per la categoria
     */
    private JPanel createCategoryPanel(JTable tableTodo, JTable tableDone) {
        JPanel mainCatPanel = new JPanel(new BorderLayout());

        JPanel pnlTodo = new JPanel(new BorderLayout());
        pnlTodo.setBorder(BorderFactory.createTitledBorder("DA FARE"));
        pnlTodo.add(new JScrollPane(tableTodo), BorderLayout.CENTER);
        pnlTodo.setMinimumSize(new Dimension(100, 150));

        JPanel pnlDone = new JPanel(new BorderLayout());
        pnlDone.setBorder(BorderFactory.createTitledBorder("COMPLETATO"));
        pnlDone.add(new JScrollPane(tableDone), BorderLayout.CENTER);
        pnlDone.setMinimumSize(new Dimension(100, 100));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnlTodo, pnlDone);
        splitPane.setResizeWeight(0.6);
        splitPane.setContinuousLayout(true);

        mainCatPanel.add(splitPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnChecklist = new JButton("Vedi Checklist");
        JButton btnShare = new JButton("Condividi");
        JButton btnComplete = new JButton("Sposta a Completato");
        JButton btnDelete = new JButton("Elimina");

        btnChecklist.addActionListener(e -> azioneChecklist(tableTodo));
        btnShare.addActionListener(e -> azioneCondividi(tableTodo));
        btnComplete.addActionListener(e -> azioneCompleta(tableTodo));
        btnDelete.addActionListener(e -> azioneElimina(tableTodo, tableDone));

        actionPanel.add(btnChecklist);
        actionPanel.add(btnShare);
        actionPanel.add(btnComplete);
        actionPanel.add(btnDelete);

        mainCatPanel.add(actionPanel, BorderLayout.SOUTH);
        return mainCatPanel;
    }

    /**
     * Gestisce l'azione di visualizzazione della checklist per un task selezionato.
     * @param table Tabella da cui recuperare il task selezionato
     * @throws IllegalStateException se nessun task è selezionato
     */
    private void azioneChecklist(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int taskId = (int) table.getValueAt(selectedRow, 0);
            String title = (String) table.getValueAt(selectedRow, 1);
            mostraDialogChecklist(taskId, title);
        } else {
            JOptionPane.showMessageDialog(this, "Seleziona un task 'Da Fare'.");
        }
    }

    /**
     * Gestisce l'azione di condivisione di un task con un altro utente.
     * @param table Tabella da cui recuperare il task selezionato
     * @throws IllegalStateException se nessun task è selezionato
     */
    private void azioneCondividi(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int taskId = (int) table.getValueAt(selectedRow, 0);
            String name = JOptionPane.showInputDialog(this, "Inserisci username:");

            if (name != null && !name.isEmpty()) {
                boolean esito = controller.condividiTask(taskId, name);

                if (esito) {
                    JOptionPane.showMessageDialog(this, "Condiviso con successo!");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Impossibile condividere: l'utente '" + name + "' non esiste.",
                            "Errore Condivisione",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleziona un task.");
        }
    }

    /**
     * Gestisce l'azione di completamento di un task.
     * Sposta il task selezionato dallo stato "Da Fare" a "Completato".
     * @param table Tabella da cui recuperare il task selezionato
     * @throws IllegalStateException se nessun task è selezionato
     */
    private void azioneCompleta(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int taskId = (int) table.getValueAt(selectedRow, 0);
            controller.spostaTask(taskId, STATUS_DONE);
            refreshAllData();
        } else {
            JOptionPane.showMessageDialog(this, "Seleziona un task da completare.");
        }
    }

    /**
     * Gestisce l'azione di eliminazione di un task.
     * Supporta eliminazione da entrambe le tabelle ("Da Fare" e "Completato").
     * @param tableTodo Tabella task "Da Fare"
     * @param tableDone Tabella task "Completato"
     */
    private void azioneElimina(JTable tableTodo, JTable tableDone) {
        JTable targetTable = null;
        if (tableTodo.getSelectedRow() >= 0) targetTable = tableTodo;
        else if (tableDone.getSelectedRow() >= 0) targetTable = tableDone;

        if (targetTable != null) {
            int taskId = (int) targetTable.getValueAt(targetTable.getSelectedRow(), 0);
            if (JOptionPane.showConfirmDialog(this, "Eliminare?", "Conferma", JOptionPane.YES_NO_OPTION) == 0) {
                controller.eliminaTask(taskId);
                refreshAllData();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleziona un task.");
        }
    }

    /**
     * Mostra dialog modale per la creazione di un nuovo task.
     * Raccoglie titolo, descrizione, data di scadenza e categoria dall'utente.
     */
    private void mostraDialogNuovoTask() {
        JDialog dialog = new JDialog(this, "Nuovo Task", true);
        dialog.setSize(400, 350);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setLocationRelativeTo(this);

        JTextField txtTitolo = new JTextField();
        JTextField txtDesc = new JTextField();
        JTextField txtScadenza = new JTextField(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        List<Bacheca> bacheche = controller.getBacheche(currentUser.getId());
        JComboBox<Bacheca> cmbBacheche = new JComboBox<>(bacheche.toArray(new Bacheca[0]));

        dialog.add(new JLabel("Titolo:")); dialog.add(txtTitolo);
        dialog.add(new JLabel("Descrizione:")); dialog.add(txtDesc);
        dialog.add(new JLabel("Scadenza (gg-mm-aaaa):")); dialog.add(txtScadenza);
        dialog.add(new JLabel("Categoria:")); dialog.add(cmbBacheche);

        JButton btnSalva = new JButton("Salva");
        btnSalva.addActionListener(e -> {
            try {
                Bacheca selezionata = (Bacheca) cmbBacheche.getSelectedItem();

                String dataInserita = txtScadenza.getText();
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
                java.time.LocalDate dataLocale = java.time.LocalDate.parse(dataInserita, formatter);
                Date scadenza = Date.valueOf(dataLocale);

                boolean esito = controller.creaNuovoTask(currentUser.getId(), txtTitolo.getText(), txtDesc.getText(), scadenza, selezionata.getId());

                if (esito) {
                    dialog.dispose();
                    refreshAllData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Errore nella creazione del task (titolo vuoto?)!", "Errore", JOptionPane.ERROR_MESSAGE);
                }
            } catch (java.time.format.DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Formato data errato! Usa: gg-mm-aaaa (es. 25-12-2025)", "Errore Data", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Errore generico: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(btnSalva);
        dialog.setVisible(true);
    }

    /**
     * Mostra dialog modale per la gestione della checklist di un task.
     * Permette di visualizzare, completare e aggiungere elementi alla checklist.
     * @param taskId 	Id del task di cui gestire la checklist
     * @param taskTitle Titolo del task
     */
    private void mostraDialogChecklist(int taskId, String taskTitle) {
        JDialog dialog = new JDialog(this, "Checklist: " + taskTitle, true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel pnlListaVoci = new JPanel();
        pnlListaVoci.setLayout(new BoxLayout(pnlListaVoci, BoxLayout.Y_AXIS));
        pnlListaVoci.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        List<ChecklistItem> items = controller.recuperaChecklist(taskId);

        for (ChecklistItem item : items) {
            JPanel pnlRiga = creaRigaChecklist(pnlListaVoci, item, taskId, dialog);
            pnlListaVoci.add(pnlRiga);
            pnlListaVoci.add(Box.createVerticalStrut(5));
        }

        JScrollPane scrollPane = new JScrollPane(pnlListaVoci);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel pnlAggiungi = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlAggiungi.setBorder(BorderFactory.createTitledBorder("Nuova Voce"));
        JTextField txtNuovaVoce = new JTextField(20);
        JButton btnAggiungiVoce = new JButton("Aggiungi");

        btnAggiungiVoce.addActionListener(e -> {
            String testo = txtNuovaVoce.getText();
            if (testo != null && !testo.isEmpty()) {
                controller.aggiungiVoceChecklist(taskId, testo);
                dialog.dispose();
                mostraDialogChecklist(taskId, taskTitle);
            }
        });

        pnlAggiungi.add(txtNuovaVoce);
        pnlAggiungi.add(btnAggiungiVoce);
        dialog.add(pnlAggiungi, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /**
     * Crea una riga per un elemento della checklist.
     * Include checkbox per completamento e pulsante eliminazione.
     * @param parentPanel 	Pannello contenitore delle righe
     * @param item 			Elemento della checklist da visualizzare
     * @param taskId 		Id del task di appartenenza
     * @param parentDialog 	Dialog padre per aggiornamenti
     * @return JPanel configurato come riga checklist
     */
    private JPanel creaRigaChecklist(JPanel parentPanel, ChecklistItem item, int taskId, JDialog parentDialog) {
        JPanel pnlRiga = new JPanel(new BorderLayout());
        pnlRiga.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        pnlRiga.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JCheckBox checkBox = new JCheckBox(item.getName());
        checkBox.setSelected(item.isCompleted());

        checkBox.addActionListener(e -> {
            boolean completato = controller.spuntaVoce(item.getId(), taskId);
            if (completato) {
                JOptionPane.showMessageDialog(parentDialog,
                        "Tutte le voci completate! Il task passa a 'Completato'.");
                parentDialog.dispose();
                refreshAllData();
            }
        });

        JButton btnElimina = new JButton("X");
        btnElimina.setForeground(Color.RED);
        btnElimina.setMargin(new Insets(0, 5, 0, 5));

        btnElimina.addActionListener(e -> {
            int conferma = JOptionPane.showConfirmDialog(pnlRiga, "Eliminare?", "Conferma", JOptionPane.YES_NO_OPTION);
            if (conferma == JOptionPane.YES_OPTION) {
                controller.eliminaVoceChecklist(item.getId());
                parentPanel.remove(pnlRiga);
                parentPanel.revalidate();
                parentPanel.repaint();
            }
        });

        pnlRiga.add(checkBox, BorderLayout.CENTER);
        pnlRiga.add(btnElimina, BorderLayout.EAST);
        return pnlRiga;
    }
}