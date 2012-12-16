package jam;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Una GUI per la gestione dell'ADSL.
 * Ha un campo di testo per stabilire la porta su cui avviare il registro RMI, e
 * tre bottoni:
 * <ul>
 * <li><i>Start reg</i> per inizializzare l'agente
 * <li><i>Start ADSL</i> per avviare l'ADSL
 * <li><i>Stop ADSL</i> per terminare l'ADSL
 * </ul>
 *
 * @author as
 */
public class ADSLMonitor extends JFrame {

    private boolean registryAlreadyStarted;
    private int port; // viene valorizzato solo quando l'utente clicca sul bottone "start reg"
    private ADSLImpl adsl;

    public ADSLMonitor() {
        super("ADSL Monitor");
        registryAlreadyStarted = false;

        label = new JLabel("Port:");
        portTextField = new JTextField("1099");
        startRegButton = new JButton("Start reg");
        startAdslButton = new JButton("Start ADSL");
        stopAdslButton = new JButton("Stop ADSL");

        startRegButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                if (registryAlreadyStarted) {
                    JOptionPane.showMessageDialog(null, "Il registro e' gia' stato avviato.\nImpossibile avviarlo di nuovo.",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    try {
                        port = Integer.parseInt(portTextField.getText());
                        adsl = new ADSLImpl("adsl", port);
                        adsl.startRMIRegistry();
                        portTextField.setEditable(false);
                        registryAlreadyStarted = true;
                        // informo l'utente del successo dell'operazione
                        JOptionPane.showMessageDialog(null, "Registro RMI avviato.",
                                "Info",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Si e' verificato un errore nell'avviare i servizi remoti. Riprovare.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Per favore, inserisci come porta un numero intero",
                                "Warning",
                                JOptionPane.WARNING_MESSAGE);
                    } catch (IllegalArgumentException ex) {
                        if (ex.getMessage().equals("Il numero della porta deve essere un intero non negativo, maggiore di 1024.")) {
                            JOptionPane.showMessageDialog(null, "Per favore, inserisci come porta un numero intero maggiore di 1024",
                                    "Warning",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            }
        });

        startAdslButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                if (adsl == null) {
                    JOptionPane.showMessageDialog(null, "Per poter avviare l'ADSL devi prima avviare il registro RMI",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    try {
                        adsl.start();
                        // informo l'utente del successo dell'operazione
                        JOptionPane.showMessageDialog(null, "ADSL avviato.",
                                "Info",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Per poter avviare l'ADSL devi prima avviare il registro RMI",
                                "Warning",
                                JOptionPane.WARNING_MESSAGE);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Si e' verificato un errore nell'avviare i servizi remoti. Riprovare.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } catch (MalformedURLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Si e' verificato un errore nell'avviare i servizi remoti.\nPer l'ADSL e' stata specificata una URL non valida.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        stopAdslButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                try {
                    if (adsl != null) {
                        adsl.stop();
                        // informo l'utente del successo dell'operazione
                        JOptionPane.showMessageDialog(null, "ADSL terminato.",
                                "Info",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Si e' verificato un errore nell'avviare i servizi remoti. Riprovare.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } catch (NotBoundException ex) {
                    //ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "ADSL non in esecuzione.\nImpossibile terminarlo.",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Si e' verificato un errore nell'usare i servizi remoti.\nPer l'ADSL e' stata specificata una URL non valida.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel = new JPanel();
        upPanel = new JPanel();
        downPanel = new JPanel();
        upPanel.add(label);
        upPanel.add(portTextField);
        upPanel.add(startRegButton);
        downPanel.add(startAdslButton);
        downPanel.add(stopAdslButton);
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(upPanel);
        panel.add(downPanel);
        add(panel);
        setResizable(false);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    // componenti grafici
    private JLabel label;
    private JPanel upPanel;
    private JPanel downPanel;
    private JPanel panel;
    private JTextField portTextField;
    private JButton startRegButton;
    private JButton startAdslButton;
    private JButton stopAdslButton;

    public static void main(String[] args) {
        ADSLMonitor gui = new ADSLMonitor();
        gui.setVisible(true);
    }
}
