/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jam;

import jam.eccezioni.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * Rappresenta una casella di posta (NON SINCRONIZZATA) appartenente ad un certo 
 * agente, nella quale gli altri agenti possono recapitare dei messaggi.
 * <br>
 * Ad una tale casella, solo l'agente proprietario vi può accedere in lettura,
 * mentre gli altri agenti vi possono accedere solo in scrittura.
 *
 * @author tosco
 */
public class MessageBoxNoSync extends UnicastRemoteObject {

    private PersonalAgentID owner;      // il proprietario della casella postale
    private List<Message> box;          // coda di messagi
    private final int maxMessages;      // numero massimo di messaggi che possono essere inseriti in box

    /**
     * Crea una nuova message box di capacità 20.
     * 
     * @param owner l'agente che possiede questa message box
     * @throws RemoteException se la creazione di questa message box non va a
     *                         buon fine
     */
    public MessageBoxNoSync(PersonalAgentID owner) throws RemoteException {
        this(owner,20);
    }

    /**
     * @param owner l'agente che possiede questa message box
     * @param maxMessages la capienza massima di questa coda di messaggi
     * @throws RemoteException se la creazione di questa message box non va a
     *                         buon fine
     * @throws IllegalArgumentException se maxMessages<=0 o se owner=null
     */
    public MessageBoxNoSync(PersonalAgentID owner, int maxMessages) throws RemoteException {
        super();
        if (owner == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        if (maxMessages <= 0) {
            throw new IllegalArgumentException("La capienza massima della coda di messaggi deve essere un intero strettamente positivo.");
        }
        this.maxMessages = maxMessages;
        this.owner = owner;
        box = new LinkedList<Message>();
    }

    public PersonalAgentID getOwner() {
        return owner;
    }

    /**
     * @return true se la coda di messaggi e' vuota; false altrimenti
     */
    public boolean isBoxEmpty() {
        return box.isEmpty();
    }

    /**
     * @return true se la coda di messaggi e' piena; false altrimenti
     */
    public boolean isBoxFull() {
        return maxMessages == box.size();
    }

    /**
     * Inserisce message in coda alla casella di messaggi.
     *
     * @param message il messaggio da inserire in coda
     * @throws JAMMessageBoxException se la coda di messaggi è piena
     */
    protected void writeMessage(Message message) throws JAMMessageBoxException, InterruptedException {
        if (message == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        if (isBoxFull()) {
            throw new JAMMessageBoxException("Errore nella scrittura. Coda di messaggi piena.");
        }
        box.add(message);
    }

    /**
     * Legge e poi cancella il primo messaggio in coda (i.e. il piu' vecchio in attesa).
     *
     * @return il piu' vecchio messaggio in coda
     * @throws JAMMessageBoxException se la coda di messaggi è vuota
     * @throws InterruptedException In realtà non può essere mai lanciata
     */
    protected Message readMessage() throws JAMMessageBoxException, InterruptedException {
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
     * @throws JAMMessageBoxException se non trova nessun messaggio corrispondente ai criteri di ricerca
     *                                o se la coda di messaggi è vuota
     * @throws InterruptedException In realtà non può essere mai lanciata
     */
    protected Message readMessage(AgentID a) throws JAMMessageBoxException, InterruptedException {
        if (a == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        if (box.isEmpty()) {
            throw new JAMMessageBoxException("Errore nella lettura. Coda di messaggi vuota.");
        }
        ListIterator<Message> it = box.listIterator();
        while (it.hasNext()) {
            Message m = it.next();
            if (a.equals(m.getSender())) { // OCCHIO! l'uguaglianza non e' commutativa!!! per via del binding dinamico...
                it.remove();
                return m;
            }
        }
        throw new JAMMessageBoxException("Nessun messaggio corrispondente ai criteri di ricerca.");
    }

    /**
     * Legge e poi cancella il primo (i.e. il piu' vecchio) messaggio in coda corrispondente ad una certa performativa.
     *
     * @param p la performativa che viene usata come criterio di ricerca
     * @return il piu' vecchio messaggio in coda che è caratterizzato dalla performativa p
     * @throws JAMMessageBoxException se non trova nessun messaggio corrispondente ai criteri di ricerca
     *                                o se la coda di messaggi è vuota
     * @throws InterruptedException In realtà non può essere mai lanciata
     */
    protected Message readMessage(Performative p) throws JAMMessageBoxException, InterruptedException {
        if (p == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        if (box.isEmpty()) {
            throw new JAMMessageBoxException("Errore nella lettura. Coda di messaggi vuota.");
        }
        ListIterator<Message> it = box.listIterator();
        while (it.hasNext()) {
            Message m = it.next();
            if (m.getPerformative().toString().equals(p.toString())) {
                it.remove();
                return m;
            }
        }
        throw new JAMMessageBoxException("Nessun messaggio corrispondente ai criteri di ricerca.");
    }

    /**
     * Legge e poi cancella il primo (i.e. il piu' vecchio) messaggio in coda corrispondente ad una certa performativa
     * e inviato da un certo agente (o gruppo di agenti).
     *
     * @param a ID dell'agente (o del gruppo di agenti) mittente del messaggio che si vuole leggere
     * @param p la performativa che viene usata come criterio di ricerca
     * @return il piu' vecchio messaggio in coda corrispondente ad una certa performativa
     * e inviato da un certo agente (o gruppo di agenti)
     * @throws JAMMessageBoxException se non trova nessun messaggio corrispondente ai criteri di ricerca
     *                                o se la coda di messaggi è vuota
     * @throws InterruptedException In realtà non può essere mai lanciata
     */
    protected Message readMessage(AgentID a, Performative p) throws JAMMessageBoxException, InterruptedException {
        if (a == null || p == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        if (box.isEmpty()) {
            throw new JAMMessageBoxException("Errore nella lettura. Coda di messaggi vuota.");
        }
        ListIterator<Message> it = box.listIterator();
        while (it.hasNext()) {
            Message m = it.next();
            if (a.equals(m.getSender()) && m.getPerformative().toString().equals(p.toString())) {
                it.remove();
                return m;
            }
        }
        throw new JAMMessageBoxException("Nessun messaggio corrispondente ai criteri di ricerca.");
    }

    /**
     * @return true se nella coda è presente un qualunque messaggio;
     *         false altrimenti
     */
    protected boolean isThereMessage() {
        return !box.isEmpty();
    }

    /**
     * @param a ID dell'agente (o del gruppo di agenti) mittente del messaggio che si vuole cercare
     * @return true se nella coda è presente almeno un messaggio il cui mittente matcha con a;
     *         false altrimenti
     */
    protected boolean isThereMessage(AgentID a) {
        if (a == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        ListIterator<Message> it = box.listIterator();
        while (it.hasNext()) {
            Message m = it.next();
            if (a.equals(m.getSender())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param p la performativa che viene usata come criterio di ricerca
     * @return true se nella coda è presente almeno un messaggio caratterizzato dalla performativa p;
     *         false altrimenti
     */
    protected boolean isThereMessage(Performative p) {
        if (p == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        ListIterator<Message> it = box.listIterator();
        while (it.hasNext()) {
            Message m = it.next();
            if (m.getPerformative().toString().equals(p.toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param a ID dell'agente (o del gruppo di agenti) mittente del messaggio che si vuole leggere
     * @param p la performativa che viene usata come criterio di ricerca
     * @return true se nella coda è presente almeno un messaggio caratterizzato dalla performativa p, e il cui mittente matcha con a;
     *         false altrimenti
     */
    protected boolean isThereMessage(AgentID a, Performative p) {
        if (a == null || p == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        ListIterator<Message> it = box.listIterator();
        while (it.hasNext()) {
            Message m = it.next();
            if (a.equals(m.getSender()) && m.getPerformative().toString().equals(p.toString())) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        String toRet = "owner: " + owner;
        ListIterator<Message> it = box.listIterator();
        while (it.hasNext()) {
            toRet += "" + it.next();
        }
        return toRet;
    }

    /**
     * Stampa a video questa message box, in forma testuale.
     * Ovvero, stampa l'agente proprietario della message box, seguito da
     * tutti i messaggi al momento presenti in questa message box.
     *
     * @throws RemoteException
     */
    public void print() {
        String toRet = "owner: " + owner;
        ListIterator<Message> it = box.listIterator();
        while (it.hasNext()) {
            toRet += "" + it.next()+"\n";
        }
        System.out.println(toRet);
    }
}
