/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Test3;

import jam.GenericAgentID;
import jam.Message;
import jam.MessageBox;
import jam.Performative;
import jam.PersonalAgentID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tosco
 */
public class MsgThread extends Thread {

    private MessageBox box;
    private PersonalAgentID mitt;

    public MsgThread(MessageBox box, PersonalAgentID a) {
        super();
        this.box = box;
        this.mitt = a;
    }

    public void run() {
        try {
            System.out.println("RUNNO " + Thread.currentThread().getName());
            box.writeMessage(new Message(mitt, new GenericAgentID(), Performative.valueOf("INFORM"), ""));
            Thread.sleep(2000);
            Message m = box.readMessage(mitt);
            System.out.println(Thread.currentThread().getName() + " > Stampo messaggio \n" + m);
            return;
        } catch (InterruptedException ex) {
            Logger.getLogger(MsgThread.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("errore! " + Thread.currentThread().getName() + " e' stato interrotto");
        }
    }
}
