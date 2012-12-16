/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Test3;

import jam.*;
import java.rmi.RemoteException;

/**
 *
 * @author tosco
 */
public class TestParteIII {

    public static void main(String[] args) throws RemoteException {
        PersonalAgentID a = new PersonalAgentID("cat", "a"); // il proprietario della messagebox
        MessageBox box = new MessageBox(a);

        // ora creo un thread per ogni agente
        MsgThread t1 = new MsgThread(box, a);
        MsgThread t2 = new MsgThread(box, new PersonalAgentID("c1", "b"));
        MsgThread t3 = new MsgThread(box, new PersonalAgentID("c1", "c"));
        t1.start();
        t2.start();
        t3.start();
    }
}
