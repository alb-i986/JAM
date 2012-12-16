/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Test4;

import jam.eccezioni.JAMADSLException;
import jam.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author st064481
 */
public class ProvaRemoteMessageBoxDue {

    public static void main(String[] args) throws NotBoundException, RemoteException, MalformedURLException, JAMADSLException {
        AgentID myID = new PersonalAgentID("cat1", "b");
        System.out.println("Ciao, sono " + myID);

        MessageBox box2 = new MessageBox((PersonalAgentID) myID);

        System.out.println("Ora registro la mia box presso l'ADSL");
        ADSL adsl = (ADSL) Naming.lookup("//localhost:1099/adsl");
        adsl.insertRemoteMessageBox(box2);
        try {
            List<RemoteMessageBox> boxes = adsl.getRemoteMessageBox(new GenericAgentID());
            System.out.println("stampo le boxes presenti nell'ADSL");
            for (RemoteMessageBox b : boxes) {
                b.print();
            }
            /*
            AgentID destinatario = new PersonalAgentID("b", "cat1");
            Message m = new Message((PersonalAgentID) myID, destinatario, Performative.valueOf("INFORM"), "");
            List<RemoteMessageBox> remoteBox2 = adsl.getRemoteMessageBox(destinatario);
            for (RemoteMessageBox rmb : remoteBox2) {
            rmb.writeMessage(m);
            }
             */

            System.out.println("Leggo il primo messaggio (appena arriva)");
            Message myMsg = box2.readMessage();
            System.out.println(myMsg);

        } catch (Exception e) {
            //System.out.println(" Failed rmi\n " + e);
            e.printStackTrace();
        } finally {
            System.out.println("Rimuovo la mia box dall'ADSL");
            try {
                adsl.removeRemoteMessageBox(myID);
            } catch (Exception ex) {
                Logger.getLogger(ProvaRemoteMessageBoxUno.class.getName()).log(Level.SEVERE, null, ex);
            }

            System.out.println("Due > FINITO!");
        }
    }
}
