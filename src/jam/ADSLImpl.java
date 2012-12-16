/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jam;

import jam.eccezioni.*;
import utils.Coppia;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.locks.*;

/**
 * La classe concreta che rappresenta l'oggetto remoto che ha lo scopo di pubblicare
 * i RemoteMessageBox degli agenti presenti nella piattaforma in ogni istante.
 * <p>
 * Questa implementazione dell'ADSL presenta le seguenti features:
 * <ul>
 * <li>è possibile eseguire <code>getRemoteMessageBox</code> da più agenti 
 * contemporaneamente. Infatti, come meccanismo di sincronizzazione, si è scelto
 * di usare il <code>ReadWriteLock</code>, che permette a più lettori di leggere
 * contemporaneamente. 
 * <li>è in grado di cancellare automaticamente, nel regolare svolgimento delle sue
 * operazioni, le RemoteMessageBox registrate che non sono più attive. Infatti,
 * ogni volta che nell'eseguire un metodo scorre la lista delle RemoteMessageBox registrate
 * (che sono oggetti remoti), verifica che quelle che incontra siano ancora attive.
 * </ul>
 *
 * @author tosco
 */
public class ADSLImpl extends UnicastRemoteObject implements ADSL {

    // la lista delle message box remote e dei relativi agenti proprietari
    private List<Coppia<RemoteMessageBox, PersonalAgentID>> messageBoxesAndOwners;
    private int port;
    private String name;
    private String hostname;
    //campi per la sincronizzazione
    private ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private Lock rLock = rwLock.readLock();
    private Lock wLock = rwLock.writeLock();

    /**
     * Costruisce un nuovo oggetto ADSL, usando come URL di default
     * //localhost:1099/adsl
     *
     * @throws RemoteException
     */
    public ADSLImpl() throws RemoteException {
        this("adsl", 1099);
    }

    /**
     * Costruisce un nuovo oggetto ADSL, assegnandogli l'URL
     * //localhost:port/name
     *
     * @throws RemoteException
     * @throws IllegalArgumentException se qualche argomento vale null, oppure
     *  se il numero della porta non e' un intero positivo
     */
    public ADSLImpl(String nome, int porta) throws RemoteException {
        super();

        if (nome == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        if (porta < 0) {
            throw new IllegalArgumentException("Il numero della porta deve essere un intero positivo.");
        }
        messageBoxesAndOwners = new LinkedList<Coppia<RemoteMessageBox, PersonalAgentID>>();
        name = nome;
        port = porta;
        hostname = "localhost";
    }

    /**
     * Restituisce una lista di riferimenti ad oggetti (remoti) di tipo RemoteMessageBox
     * i cui proprietari sono uguali a agentID.
     * <p>
     * Nello specifico:
     * <ul>
     * <li>nel caso in cui agentID contenga un riferimento ad un oggetto di tipo PersonalAgentID
     * allora la lista sarà composta dal singolo riferimento al RemoteMessageBox dell'agente di ID specificato.
     * <li>nel caso contenga un riferimento ad una istanza di CategoryAgentID allora la lista sarà
     * composta da tutti i riferimenti ai RemoteMessageBox i cui proprietari hanno stessa categoria
     * di quella specificata mediante il parametro agentID.
     * <li>nel caso in cui agentID contenga un riferimento ad una istanza di GenericAgentID
     * allora la lista restituita conterà i riferimenti a tutti i RemoteMessageBox presenti in quel momento
     * </ul>
     *
     * @param a i(l) proprietari(o) dei(del) RemoteMessageBox di cui si vuole ottenere i(l) riferimenti(o)
     * @return una lista di RemoteMessageBox appartenenti agli agenti specificati in agentID
     * @throws RemoteException
     */
    public List<RemoteMessageBox> getRemoteMessageBox(AgentID a) {
        if (a == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        System.out.println("BEGIN getRemoteMessageBox " + a);

        List<RemoteMessageBox> boxesToRet = new LinkedList<RemoteMessageBox>();

        rLock.lock(); // acquisisco il lock per la lettura
        try {
            ListIterator<Coppia<RemoteMessageBox, PersonalAgentID>> it = messageBoxesAndOwners.listIterator();
            while (it.hasNext()) {
                Coppia<RemoteMessageBox, PersonalAgentID> c = it.next();
                try {
                    if (a.equals(c.getFirst().getOwner())) {
                        boxesToRet.add(c.getFirst());
                    }
                } catch (RemoteException e) {
                    // se, scorrendo la lista, incontro un oggetto remoto non attivo,
                    // cancello la coppia corrispondente dalla lista
                    it.remove();
                }
            }
        } finally {
            rLock.unlock(); // rilascio il lock per la lettura
        }

        System.out.println("getRemoteMessageBox > Stampo quello che restituisco");
        for (RemoteMessageBox b : boxesToRet) {
            try {
                System.out.println("\t" + b.getOwner());
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("END getRemoteMessageBox " + a);

        // siccome l'adsl tiene un solo box per ogni agente,
        // se il parametro a è Personal, allora a deve matchare con 0 o 1 
        // elementi della lista
        if (a instanceof PersonalAgentID) {
            assert boxesToRet.size() <= 1;
        }
        return boxesToRet;
    }

    /**
     * Richiede la registrazione di messageBox presso l'ADSL.
     * Se l'elemento è già presente, non viene effettuata alcuna operazione e viene lanciata un'eccezione.
     * <p>
     * Precondizione: l'oggetto remoto messageBox passato come parametro deve essere attivo
     *
     * @param messageBox la message box remota da registrare presso l'ADSL (deve essere un oggetto remoto attivo)
     * @throws RemoteException
     * @throws JAMADSLException se messageBox non è stata inserita perché era già presente una casella dello stesso proprietario
     * @throws IllegalArgumentException se l'oggetto remoto messageBox non è più attivo
     */
    public void insertRemoteMessageBox(RemoteMessageBox messageBox) throws JAMADSLException {
        if (messageBox == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        // controllo che il parametro-oggetto remoto sia ancora attivo
        PersonalAgentID newOwner;
        try {
            newOwner = messageBox.getOwner();
        } catch (RemoteException e) {
            throw new IllegalArgumentException("Il riferimento all'oggetto remoto, passato come parametro, non e' valido.", e);
        }

        System.out.println("BEGIN insertRemoteMessageBox " + newOwner);
        System.out.println("insertRemoteMessageBox > stampo gli elementi presenti PRIMA del nuovo inserimento");
        print();

        wLock.lock(); // acquisisco il lock per la scrittura
        try {
            ListIterator<Coppia<RemoteMessageBox, PersonalAgentID>> it = messageBoxesAndOwners.listIterator();
            while (it.hasNext()) {
                Coppia<RemoteMessageBox, PersonalAgentID> c = it.next();
                try {
                    // se la nuova box e' "uguale" ad una gia' presente => errore
                    if (c.getFirst().getOwner().equals(newOwner)) {
                        System.out.println("END insertRemoteMessageBox > lancio JAMADSLException");
                        throw new JAMADSLException("RemoteMessageBox non inserita perche' gia' presente");
                    }
                } catch (RemoteException e) {
                    // se, scorrendo la lista, incontro un oggetto remoto non attivo,
                    // cancello la coppia corrispondente dalla lista
                    it.remove();
                }
            }
            // infine, se messageBox non era già presente, la aggiungo alla lista di message box attive
            messageBoxesAndOwners.add(new Coppia<RemoteMessageBox, PersonalAgentID>(messageBox, newOwner));
        } finally {
            wLock.unlock(); // rilascio il lock per la scrittura
        }

        System.out.println("insertRemoteMessageBox > stampo gli elementi presenti DOPO il nuovo inserimento");
        print();

        System.out.println("END insertRemoteMessageBox " + newOwner);
    }

    /**
     * Richiede la cancellazione della MessageBox remota presente presso l'ADSL
     * di proprietà dell'agente agentID.
     * <br>
     * Se l'elemento non è presente, non viene effettuata alcuna operazione e
     * viene lanciata un'eccezione.
     *
     * @param agentID il proprietario della casella da eliminare
     * 
     * @throws RemoteException
     * @throws JAMADSLException se non è stato possibile rimuovere le message box
     * richieste perchè non è stata trovata nessuna message box che ha agentID
     * come proprietario
     */
    public void removeRemoteMessageBox(AgentID agentID) throws JAMADSLException {
        if (agentID == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }

        System.out.println("BEGIN removeRemoteMessageBox " + agentID);
        System.out.println("removeRemoteMessageBox > stampo gli elementi presenti PRIMA della cancellazione");
        print();

        boolean removed_at_least_one;
        wLock.lock(); // acquisisco il lock per la scrittura
        try {
            removed_at_least_one = false;
            ListIterator<Coppia<RemoteMessageBox, PersonalAgentID>> it = messageBoxesAndOwners.listIterator();
            while (it.hasNext()) {
                Coppia<RemoteMessageBox, PersonalAgentID> c = it.next();
                try {
                    if (agentID.equals(c.getSecond())) { // qua non posso usare c.getFirst().getOwner() al posto di c.getSecond()
                        it.remove();                // perche' senno', nel caso in cui l'oggetto remoto non e' attivo, non potrei impostare la var booleana removedAtLeastOne a true
                        removed_at_least_one = true;
                    } // nell'else invoco un metodo sulla RemoteMessageBox corrente solo per vedere se l'oggetto remoto è ancora attivo
                    else {
                        c.getFirst().getOwner();
                    }
                } catch (RemoteException e) {
                    // se, scorrendo la lista, incontro un oggetto remoto non attivo,
                    // cancello la coppia corrispondente dalla lista
                    it.remove();
                }
            }
        } finally {
            wLock.unlock(); // rilascio il lock per la scrittura
        }

        if (!removed_at_least_one) {
            System.out.println("END removeRemoteMessageBox > lancio JAMADSLException");
            throw new JAMADSLException("Cancellazione non eseguita perche' non e' stata trovata nessuna RemoteMessageBox che soddisfacesse i criteri di ricerca.");
        }

        System.out.println("removeRemoteMessageBox > stampo gli elementi presenti DOPO la cancellazione");
        print();
        System.out.println("END removeRemoteMessageBox " + agentID);
    }

    private void print() {
        rLock.lock(); // acquisisco il lock per la lettura
        try {
            int i = 0;
            for (Coppia<RemoteMessageBox, PersonalAgentID> c : messageBoxesAndOwners) {
                try {
                    i++;
                    System.out.println("\t" + i + ". " + c.getFirst().getOwner());
                } catch (RemoteException e) {
                    System.out.println("\t" + i + ". " + c.getSecond() + " -OGGETTO REMOTO NON ATTIVO-");
                }
            }
        } finally {
            rLock.unlock(); // rilascio il lock per la lettura
        }
    }

    /**
     * Avvia il registro RMI, e lo mette in ascolto sulla porta specificata nel costruttore.
     *
     * @throws RemoteException
     */
    public void startRMIRegistry() throws RemoteException {
        java.rmi.registry.LocateRegistry.createRegistry(getPort());
    }

    /**
     * Effettua la registrazione di questo oggetto ADSL presso il registro RMI,
     * rendendolo accessibile ai vari agenti remoti.
     *
     * @throws RemoteException
     * @throws MalformedURLException
     */
    public void start() throws RemoteException, MalformedURLException {
        Naming.rebind("//" + getHostname() + ":" + getPort() + "/" + getName(), this);
        /*
        try {
        Context namingContext;
        namingContext = new InitialContext();
        namingContext.rebind("rmi:servers", this);

        } catch (NamingException ex) {
        Logger.getLogger(ADSLImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
         */
    }

    /**
     * Rimuove questo oggetto ADSL dal registro RMI.
     *
     * @throws RemoteException
     * @throws NotBoundException
     * @throws MalformedURLException
     */
    public void stop() throws RemoteException, NotBoundException, MalformedURLException {
        Naming.unbind("//" + getHostname() + ":" + getPort() + "/" + getName());
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }

    public String getHostname() {
        return hostname;
    }
}
