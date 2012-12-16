/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Test2;

import jam.eccezioni.JAMMessageBoxException;
import jam.*;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tosco
 */
public class ProvaMessageBoxNoSync {

    public static void main(String[] args) throws RemoteException, JAMMessageBoxException {
        try {
            MessageBoxNoSync box = new MessageBoxNoSync(new PersonalAgentID("cat", "nome"));
            Message m;

            box.writeMessage(new Message(new PersonalAgentID("x", "y"), new GenericAgentID(), Performative.valueOf("QUERY_IF"), "ciao1"));
            box.writeMessage(new Message(new PersonalAgentID("x", "c1"), new GenericAgentID(), Performative.valueOf("QUERY_IF"), "ciao2"));
            box.writeMessage(new Message(new PersonalAgentID("nome", "c2"), new GenericAgentID(), Performative.valueOf("QUERY_IF"), "ciao3"));
            box.writeMessage(new Message(new PersonalAgentID("nome", "c1"), new GenericAgentID(), Performative.valueOf("QUERY_IF"), "ciao4"));
            box.writeMessage(new Message(new PersonalAgentID("nome", "c1"), new GenericAgentID(), Performative.valueOf("QUERY_IF"), "ciao5"));
            System.out.println("Stampo messaggi presenti nella message box");
            box.print();
            System.out.println();

            PersonalAgentID pa = new PersonalAgentID("x", "c1");
            System.out.println("presente msg inviato da " + pa + "? " + box.isThereMessage(pa));
            m = box.readMessage(pa);
            System.out.println(m);

            CategoryAgentID pc = new CategoryAgentID("c1");
            System.out.println("presente msg con categoria c1? " + box.isThereMessage(pc));
            m = box.readMessage(pc);
            System.out.println(m);

            System.out.println("presente msg con performativa INFORM? " + box.isThereMessage(Performative.valueOf("INFORM")));

            pa = new PersonalAgentID("x", "y");
            System.out.println("presente msg con performativa QUERY_IF e inviato da " + pa + "? " + box.isThereMessage(pa, Performative.valueOf("QUERY_IF")));
            m = box.readMessage(pa, Performative.valueOf("QUERY_IF"));
            System.out.println(m);

            pc = new CategoryAgentID("c2");
            System.out.println("presente msg con performativa QUERY_IF e con categoria " + pc + "? " + box.isThereMessage(pc, Performative.valueOf("QUERY_IF")));
            m = box.readMessage(pc, Performative.valueOf("QUERY_IF"));
            System.out.println(m);

            System.out.println("presente msg con categoria c0? " + box.isThereMessage(new CategoryAgentID("c0")));

            System.out.println("presente msg con categoria c1? " + box.isThereMessage(new CategoryAgentID("c1")));
            m = box.readMessage();
            System.out.println(m);

            System.out.println("ora tento un'altra lettura, ma siccome la coda e' vuota, lancera' un'eccezione");
            m = box.readMessage();
            System.out.println(m);
        } catch (InterruptedException ex) {
            Logger.getLogger(ProvaMessageBoxNoSync.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
