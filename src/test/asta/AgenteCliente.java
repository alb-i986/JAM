/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package asta;

import jam.eccezioni.JAMADSLException;
import jam.*;
import jam.eccezioni.JAMRemoteMessageBoxException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Il cliente è un agente che cerca di aggiudicarsi un oggetto messo all'asta.
 * Ogni cliente è caratterizzato da:
 * <ul>
 * <li>una disponibilita' iniziale massima di soldi per le offerte
 * </ul>
 *
 * @author tosco
 */
public class AgenteCliente extends JAMAgent {

    private int portafoglio; // la mia iniziale disponibilita' massima di soldi

    /**
     * Crea un nuovo agente-cliente, dotato di un portafoglio iniziale,
     * che cerca di aggiudicarsi l'oggetto messo all'asta bandita da <i>banditoreID</i>
     * 
     * @param portafoglio l'iniziale disponibilita' massima di soldi di questo cliente
     * @param myAgentID l'ID dell'agente associato a questo comportamento
     * @param banditoreID l'ID dell'agente banditore che ha messo all'asta l'oggetto
     *                    che questo cliente vuole aggiudicarsi
     * @param adsl_ip
     * @param adsl_name
     * @param adsl_port
     *
     * @throws JAMRemoteMessageBoxException se la creazione della MessageBox
     *              (oggetto remoto) di questo agente non è andata a buon fine
     */
    public AgenteCliente(int portafoglio, PersonalAgentID myAgentID, PersonalAgentID banditoreID, String adsl_ip, String adsl_name, int adsl_port)
            throws JAMRemoteMessageBoxException {
        super(myAgentID, adsl_ip, adsl_name, adsl_port);
        if (portafoglio < 0) {
            throw new IllegalArgumentException("L'importo iniziale del portafoglio deve essere un intero non negativo.");
        }
        if (myAgentID == null || banditoreID == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        this.portafoglio = portafoglio;
        addBehaviour(new FaiOffertaBehaviour(this, banditoreID));
    }

    /**
     * Crea un nuovo agente-cliente, dotato di un portafoglio iniziale,
     * che cerca di aggiudicarsi l'oggetto messo all'asta bandita da <i>banditoreID</i>.
     * <p>
     * Viene inizializzato con i seguenti parametri di default:
     * <ul>
     * <li>IP ADSL: localhost
     * <li>Porta ADSL: 1099
     * <li>Nome ADSL: adsl
     * </ul>
     *
     * @param portafoglio l'iniziale disponibilita' massima di soldi di questo cliente
     * @param myAgentID l'ID dell'agente associato a questo comportamento
     * @param banditoreID l'ID dell'agente banditore che ha messo all'asta l'oggetto
     *                    che questo cliente vuole aggiudicarsi
     *
     * @throws JAMRemoteMessageBoxException se la creazione della MessageBox
     *              (oggetto remoto) di questo agente non è andata a buon fine
     */
    public AgenteCliente(int portafoglio, PersonalAgentID myAgentID, PersonalAgentID banditoreID) 
            throws JAMRemoteMessageBoxException {
        this(portafoglio, myAgentID, banditoreID, "localhost", "adsl", 1099);
    }

    /**
     * Associa un nuovo comportamento all'agente.<br>
     * Se si desidera che il comportamento aggiunto venga eseguito, occorre invocare <code>start</code>.
     *
     * @param behaviour il nuovo comportamento da associare a questo agente
     * @throws IllegalArgumentException se il comportamento da assocciare a questo agente
     *                                  non e' un comportamento di tipo ClienteBehaviour
     */
    @Override
    public void addBehaviour(JAMBehaviour behaviour) {
        if (!(behaviour instanceof ClienteBehaviour)) {
            throw new IllegalArgumentException("Ad un agente-cliente e' possibile associare solo comportamenti di tipo ClienteBehaviour");
        }
        super.addBehaviour(behaviour);
    }

    /**
     * @return l'iniziale disponibilita' massima di soldi di questo cliente
     */
    public int getPortafoglio() {
        return portafoglio;
    }

    /**
     * @param portafoglio l'iniziale disponibilita' massima di soldi di questo cliente
     */
    public void setPortafoglio(int portafoglio) {
        this.portafoglio = portafoglio;
    }

    public static void main(String args[]) {
        int portafoglio = 0;
        Scanner s = new Scanner(System.in);
        System.out.println("Inserisci il nome dell'agente cliente.");
        String nomeCliente = s.nextLine();

        boolean inputOk = false;
        while (!inputOk) {
            try {
                System.out.println("Inserisci l'importo del portafoglio inizlale.");
                portafoglio = s.nextInt();
                if (portafoglio <= 0) {
                    System.out.println("Devi inserire un numero intero positivo.");
                } else {
                    inputOk = true;
                }
            } catch (NumberFormatException ex) {
                System.out.println("Input errato: devi inserire un numero intero.");
            }
        }

        try {
            AgenteCliente clt = new AgenteCliente(portafoglio, new ClienteAgentID(nomeCliente), new BanditoreAgentID("ebay"));
            clt.init();
            clt.start();
        } catch (JAMRemoteMessageBoxException ex) {
            Logger.getLogger(AgenteCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAMADSLException ex) {
            Logger.getLogger(AgenteCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
