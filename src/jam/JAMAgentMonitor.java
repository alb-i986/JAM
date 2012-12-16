/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jam;

import jam.eccezioni.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import java.util.Observable;
import java.util.Observer;

/**
 * Una GUI per la gestione di un agente.
 * Ha una text-area per monitorare i messaggi scambiati dall'agente, e tre bottoni:
 * <ul>
 * <li><b>init</b> per inizializzare l'agente
 * <li><b>start</b> per avviare l'agente
 * <li><b>destroy</b> per terminare l'agente
 * </ul>
 *
 * @author as
 */
public class JAMAgentMonitor extends JFrame implements Observer {

    private JAMAgent agent;

    public JAMAgentMonitor(JAMAgent ag) {
        super("JAM Agent Monitor - " + ag.getMyID().getCategory() + " - " + ag.getMyID().getName());
        this.agent = ag;
        ag.addObserver(this); // osservo l'agente che sta per essere avviato

        consoleLabel = new JLabel("Connection Console:");
        statoLabel = new JLabel("Stato agente: ");
        stato = new JLabel(agent.getState().toString());
        consoleTextArea = new JTextArea(10, 25);
        consoleTextArea.setEditable(false);
        initButton = new JButton("Init Agent");
        startButton = new JButton("Start Agent");
        stopButton = new JButton("Stop Agent");

        initButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                initJAMAgent();
            }
        });

        startButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                startJAMAgent();
            }
        });

        stopButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                destroyJAMAgent();
            }
        });

        scrollPane = new JScrollPane(consoleTextArea); // fa si' che la text area sia scrollable
        panel = new JPanel();
        upPanel = new JPanel();
        middlePanel = new JPanel();
        downPanel = new JPanel();
        upPanel.add(initButton);
        upPanel.add(startButton);
        upPanel.add(stopButton);
        middlePanel.add(statoLabel);
        middlePanel.add(stato);
        downPanel.add(consoleLabel);
        downPanel.add(scrollPane);
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        downPanel.setLayout(new BoxLayout(downPanel, BoxLayout.PAGE_AXIS));
        panel.add(upPanel);
        panel.add(middlePanel);
        panel.add(downPanel);
        add(panel);
        setResizable(false);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Il metodo dichiarato dall'interfaccia Observer.
     */
    public void update(Observable o, Object arg) {
        consoleTextArea.append((String) arg + "\n");
    }

    protected void initJAMAgent() {
        try {
            agent.init();
            stato.setText(agent.getState().toString());
            // informo l'utente del successo dell'operazione
            JOptionPane.showMessageDialog(null, "Inizializzazione dell'agente completata.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(null, "Attenzione: agente gia' inizializzato.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        } catch (JAMADSLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Si e' verificato un errore nell'usare i servizi remoti.\n" +
                    "L'agente e' gia' stato registrato presso l'ADSL\n" +
                    "oppure l'ADSL non e' disponibile.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);

        }
    }

    protected void startJAMAgent() {
        try {
            agent.start();
            stato.setText(agent.getState().toString());
            // informo l'utente del successo dell'operazione
            JOptionPane.showMessageDialog(null, "Agente avviato.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(null, "Attenzione: agente non inizializzato.\nPer poter avviare l'agente devi prima cliccare sul bottone Init",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    protected void destroyJAMAgent() {
        try {
            agent.destroy();
            consoleTextArea.append("-------------\n");
            stato.setText(agent.getState().toString());
            // informo l'utente del successo dell'operazione
            String msg = "Agente terminato.";
            JOptionPane.showMessageDialog(null, msg,
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(null, "Attenzione: agente non inizializzato o non avviato.\nImpossibile terminare l'agente.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        } catch (JAMADSLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Si e' verificato un errore nell'usare i servizi remoti. Riprovare piu' tardi.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    // componenti grafici
    private JScrollPane scrollPane;
    private JPanel upPanel;
    private JPanel middlePanel;
    private JPanel downPanel;
    private JPanel panel;
    private JTextArea consoleTextArea;
    private JLabel consoleLabel;
    private JLabel statoLabel;
    private JLabel stato; // lo stato in cui si trova l'agente
    private JButton initButton;
    private JButton startButton;
    private JButton stopButton;
}
