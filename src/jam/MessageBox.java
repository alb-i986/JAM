/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jam;

import jam.eccezioni.*;
import java.rmi.RemoteException;

/**
 * Rappresenta una casella di posta <i>sincronizzata</i>, accessibile da remoto, appartenente
 * ad un certo agente, nella quale gli altri agenti possono recapitare dei messaggi.
 * Ad una tale casella, solo l'agente proprietario vi può accedere in lettura, mentre
 * gli altri agenti vi possono accedere solo in scrittura.
 *
 * @author tosco
 */
public class MessageBox extends MessageBoxNoSync implements RemoteMessageBox {

    public MessageBox(PersonalAgentID owner, int maxMessages) throws RemoteException {
        super(owner, maxMessages);

        if (owner == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        if (maxMessages <= 0) {
            throw new IllegalArgumentException("La dimensione della message box deve essere un numero positivo.");
        }
    }

    public MessageBox(PersonalAgentID owner) throws RemoteException {
        super(owner);

        if (owner == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
    }

    /**
     * @return true se la coda di messaggi e' vuota; false altrimenti
     */
    synchronized public boolean isBoxEmpty() {
        return super.isBoxEmpty();
    }

    /**
     * @return true se la coda di messaggi e' piena; false altrimenti
     */
    synchronized public boolean isBoxFull() {
        return super.isBoxFull();
    }

    /**
     * Inserisce message in coda alla casella.
     *
     * @param message il messaggio da inserire in coda
     * @throws InterruptedException se il thread viene interrotto da un interrupt mentre è in wait
     */
    synchronized public void writeMessage(Message message) throws InterruptedException {
        if (message == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }

        boolean done = false;
        while (!done) {
            try {
                super.writeMessage(message);
                done = true;
            } catch (JAMMessageBoxException e) {
                wait();
            }
        }
        notifyAll();
        /* versione non ottimizzata
        while (isBoxFull()) {
        try {
        wait();
        } catch (InterruptedException ex) {
        ex.printStackTrace();
        }
        }
        notifyAll();*/
    }

    /**
     * Legge e poi cancella il primo messaggio in coda (i.e. il piu' vecchio in attesa).
     *
     * @return il piu' vecchio messaggio in coda
     * @throws InterruptedException se il thread viene interrotto da un interrupt mentre è in wait
     */
    synchronized public Message readMessage() throws InterruptedException {
        return readMessage(new GenericAgentID());
    }

    /**
     * Legge e poi cancella il primo (i.e. il piu' vecchio) messaggio in coda inviato da un certo agente (o da un gruppo di agenti).
     * <p>
     * Nello specifico:
     * <ul>
     * <li>se a è di tipo CategoryAgentID allora viene letto il primo messaggio in coda
     * inviato da un agente appartenente a quella categoria
     * <li>se a è di tipo PersonalAgentID allora viene letto il primo messaggio in coda
     * inviato da quello specifico agente
     * <li>se a è di tipo GenericAgentID allora viene letto il primo messaggio in coda (i.e. il piu' vecchio in attesa)
     * </ul>
     *
     * @param a ID dell'agente (o del gruppo di agenti) mittente del messaggio che si vuole leggere
     * @return il piu' vecchio messaggio in coda inviato da a
     * @throws InterruptedException se il thread viene interrotto da un interrupt mentre è in wait
     */
    synchronized public Message readMessage(AgentID a) throws InterruptedException {
        if (a == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }

        Message m = null;
        boolean done = false;
        while (!done) {
            try {
                m = super.readMessage(a);
                done = true;
            } catch (JAMMessageBoxException e) {
                wait();
            }
        }
        notifyAll();
        return m;

        /*  versione non ottimizzata
        while (!isThereMessage(a)) {
        try {
        wait();
        } catch (InterruptedException ex) {
        ex.printStackTrace();
        }
        }
        Message m = super.readMessage(a);
        notifyAll();
        return m;
         */
    }

    /**
     * Legge e poi cancella il primo (i.e. il piu' vecchio) messaggio in coda corrispondente ad una certa performativa.
     *
     * @param p la performativa che viene usata come criterio di ricerca
     * @return il piu' vecchio messaggio in coda che è caratterizzato dalla performativa p
     * @throws InterruptedException se il thread viene interrotto da un interrupt mentre è in wait
     */
    synchronized public Message readMessage(Performative p) throws InterruptedException {
        if (p == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }

        Message m = null;
        boolean done = false;
        while (!done) {
            try {
                m = super.readMessage(p);
                done = true;
            } catch (JAMMessageBoxException e) {
                wait();
            }
        }
        notifyAll();
        return m;
    }

    /**
     * Legge e poi cancella il primo (i.e. il piu' vecchio) messaggio in coda corrispondente ad una certa performativa
     * e inviato da un certo agente (o gruppo di agenti).
     *
     * @param a ID dell'agente (o del gruppo di agenti) mittente del messaggio che si vuole leggere
     * @param p la performativa che viene usata come criterio di ricerca
     * @return il piu' vecchio messaggio in coda corrispondente ad una certa performativa
     * e inviato da un certo agente (o gruppo di agenti)
     * @throws InterruptedException se il thread viene interrotto da un interrupt mentre è in wait
     */
    synchronized public Message readMessage(AgentID a, Performative p) throws InterruptedException {
        if (a == null || p == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }

        Message m = null;
        boolean done = false;
        while (!done) {
            try {
                m = super.readMessage(a, p);
                done = true;
            } catch (JAMMessageBoxException e) {
                wait();
            }
        }
        notifyAll();
        return m;
    }

    /**
     * @return true se nella coda è presente un qualunque messaggio;
     *         false altrimenti
     */
    synchronized public boolean isThereMessage() {
        return super.isThereMessage();
    }

    /**
     * @param a ID dell'agente (o del gruppo di agenti) mittente del messaggio che si vuole cercare
     * @return true se nella coda è presente almeno un messaggio il cui mittente matcha con a;
     *         false altrimenti
     */
    synchronized public boolean isThereMessage(AgentID a) {
        if (a == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }

        return super.isThereMessage(a);
    }

    /**
     * @param p la performativa che viene usata come criterio di ricerca
     * @return true se nella coda è presente almeno un messaggio caratterizzato dalla performativa p;
     *         false altrimenti
     */
    synchronized public boolean isThereMessage(Performative p) {
        if (p == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }

        return super.isThereMessage(p);
    }

    /**
     * @param a ID dell'agente (o del gruppo di agenti) mittente del messaggio che si vuole leggere
     * @param p la performativa che viene usata come criterio di ricerca
     * @return true se nella coda è presente almeno un messaggio caratterizzato dalla performativa p, e il cui mittente matcha con a;
     *         false altrimenti
     */
    synchronized public boolean isThereMessage(AgentID a, Performative p) {
        if (a == null || p == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }

        return super.isThereMessage(a, p);
    }

    synchronized public String toString() {
        return super.toString();
    }

    synchronized public void print() {
        super.print();
    }
}
