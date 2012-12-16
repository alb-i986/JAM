package jam;

import jam.eccezioni.*;
import java.net.MalformedURLException;
import java.rmi.*;
import java.util.*;

/**
 * Un agente è un oggetto "intelligente" dotato di comportamenti.
 * Con <code>addBehaviour</code> si associa un comportamento all'agente (ma il
 * comportamento non viene automaticamente avviato).
 * <p>
 * Per usare un agente bisogna invocare i seguenti metodi, <i>rigorosamente</i>
 * nell'ordine:
 * <ol>
 * <li>con <code>init()</code> si inizializza l'agente, registrandolo presso l'ADSL
 * <li>con <code>start()</code> si avvia l'esecuzione di tutti i comportamenti in
 *     quel momento associati all'agente e mai avviati dall'ultima inizializzazione dell'agente
 * <li>con <code>destroy()</code> si cancella l'agente dall'ADSL e si termina
 *    l'esecuzione di tutti i comportamenti associati all'agente
 * </ol>
 * Se quest'ordine non viene rispettato, verrà lanciata una IllegalStateException.
 * <br>
 * Eventualmente, dopo avere terminato un agente, è possibile riavviarlo,
 * ripetendo la sequenza di operazioni mostrata sopra.
 * <br>
 * Una volta inizializzato un agente, è possibile invocare <code>start()</code> un
 * numero arbitrario di volte, con l'effetto che ad ogni chiamata verranno avviati
 * i comportamenti che sono stati aggiunti dopo l'ultima chiamata a <code>start()</code>.
 * <br>
 * Non è possibile riavviare dei comportamenti già avviati, a meno che non si termini
 * l'agente con <code>destroy()</code>, e poi lo si riavvii con <code>init()</code>
 * e <code>start()</code>.
 *
 * @author tosco
 */
public abstract class JAMAgent extends Observable {

    private List<JAMBehaviour> myBehaviours;
    private MessageBox myBox;
    private PersonalAgentID myID;
    private ADSL adsl;
    private String adsl_name; // il nome dell'ADSL presso rmiregistry
    private String adsl_ip;   // l'indirizzo IP dello rmiregistry su cui gira l'ADSL
    private int adsl_port;    // il numero della porta su cui è disponibile lo rmiregistry
    // campi per "sincronizzare" i metodi init e start
    private boolean initialized = false;
    private boolean started = false;
    private JAMAgentState stato = JAMAgentState.STOPPED;

    /**
     * Costruisce un nuovo JAMAgent, con i seguenti parametri di default:
     * <ul>
     * <li>IP ADSL: localhost
     * <li>Porta ADSL: 1099
     * <li>Nome ADSL: adsl
     * </ul>
     *
     * @throws JAMRemoteMessageBoxException se la creazione dell'oggetto remoto
     *                                      MessageBox non è andata a buon fine
     */
    public JAMAgent(PersonalAgentID id) throws JAMRemoteMessageBoxException {
        this(id, "localhost", "adsl", 1099);
    }

    /**
     * @param id l'ID da associare all'agente
     * @param adsl_ip l'indirizzo IP dello rmiregistry su cui gira l'ADSL
     * @param adsl_name il nome dell'ADSL presso rmiregistry
     * @param adsl_port il numero della porta su cui è disponibile lo rmiregistry
     *
     * @throws IllegalArgumentException se il numero della porta specificato non è un intero positivo,
     *                                  o se qualche argomento è null
     * @throws JAMRemoteMessageBoxException se la creazione della MessageBox
     *              (oggetto remoto) di questo agente non è andata a buon fine
     */
    public JAMAgent(PersonalAgentID id, String adsl_ip, String adsl_name, int adsl_port)
            throws JAMRemoteMessageBoxException {
        if (id == null || adsl_ip == null || adsl_name == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        if (adsl_port <= 0) {
            throw new IllegalArgumentException("Il numero della porta deve essere un intero positivo.");
        }
        this.myID = id;
        this.adsl_ip = adsl_ip;
        this.adsl_name = adsl_name;
        this.adsl_port = adsl_port;
        try {
            myBox = new MessageBox(this.myID);
        } catch (RemoteException ex) {
            throw new JAMRemoteMessageBoxException();
        }
        myBehaviours = new LinkedList<JAMBehaviour>();
    }

    /**
     * Associa un nuovo comportamento all'agente.<br>
     * Se si desidera che il nuovo comportamento venga eseguito, occorre invocare
     * <code>start()</code>.
     * 
     * @param behaviour il nuovo comportamento da associare a questo agente
     */
    public void addBehaviour(JAMBehaviour behaviour) {
        myBehaviours.add(behaviour);
    }

    /**
     * Inizializza l'agente.
     * <p>
     * In particolare:
     * <ul>
     * <li>effettua la lookup dell'oggetto ADSL presso il registro RMI;
     * <li>registra la message box dell'agente presso l'ADSL;
     * <li>inizializza ogni comportamento associato a questo agente come non-avviato e non-terminato.
     * </ul>
     *
     * @throws IllegalStateException se questo agente è già stato inizializzato e/o avviato
     * @throws JAMADSLException se non è stato possibile registrare la message box dell'agente presso l'ADSL;<br>
     *                          oppure se il collegamento con l'ADSL non è andato a buon fine
     */
    public void init() throws JAMADSLException {
        if (getState() != JAMAgentState.STOPPED) {
            throw new IllegalStateException("Agente gia' inizializzato.");
        }
        try {
            adsl = (ADSL) Naming.lookup(getUrlADSL());
            adsl.insertRemoteMessageBox(myBox);
            // scorro la lista dei comportamenti associati a questo agente e
            // (re-)inizializzo ogni comportamento come non-avviato e non-terminato
            for (JAMBehaviour b : myBehaviours) {
                b.reset();
            }
            stato = JAMAgentState.INITIALIZED;
        } // se si è verificato qualche problema con l'ADSL,
        // rilanciamo l'eccezione relativa in una forma di più alto livello
        catch (NotBoundException ex) {
            // se la lookup dell'oggetto ADSL presso il registro RMI non è andata a buon fine
            throw new JAMADSLException(ex.getMessage(), ex);
        } catch (MalformedURLException ex) {
            //se l'URL del registro RMI, costruito a partire dai parametri
            // adsl_ip, adsl_name e adsl_port, non è sintatticamente corretto
            throw new JAMADSLException(ex.getMessage(), ex);
        } catch (RemoteException ex) {
            // se il collegamento con il registro RMI non è andato a buon fine
            throw new JAMADSLException(ex.getMessage(), ex);
        }
    }

    /**
     * Avvia un nuovo thread per ogni comportamento, associato a questo agente,
     * che non è ancora stato eseguito dall'ultima inizializzazione dell'agente.<br>
     * <p>
     * N.B.: è bene invocare start() ogni volta che si aggiunge un nuovo
     * comportamento con <code>addBehaviour</code>
     *
     * @throws IllegalStateException se questo agente non è stato precedentemente
     *                               inizializzato
     */
    public void start() {
        if (getState() == JAMAgentState.STOPPED) {
            throw new IllegalStateException("Agente non ancora inizializzato: impossibile avviarlo.");
        } else {
            for (JAMBehaviour b : myBehaviours) {
                // avvio un nuovo thread per il comportamento che sto esaminando 
                // solo se esso non è mai stato eseguito dall'ultima init()
                if (b.isAvviabile()) {
                    Thread t = new Thread(b);
                    b.setThread(t);
                    t.start();
                }
            }
            stato = JAMAgentState.STARTED;
        }
    }

    /**
     * Termina questo agente.
     * <p>
     * In particolare:
     * <ol>
     * <li>rimuove la sua message box dall'ADSL;</li>
     * <li>termina l'esecuzione di ognuno dei comportamenti associati a questo agente</li>
     * </ol>
     * <p>
     * N.B.: non <i>rimuove</i> i comportamenti che sono associati a questo agente.
     *
     * @throws IllegalStateException se questo agente non è stato precedentemente inizializzato
     *                               e/o non è stato avviato con <code>start()</code>
     * @throws JAMADSLException se il collegamento con l'ADSL non è andato a buon fine
     */
    public void destroy() throws JAMADSLException {
        if (getState() == JAMAgentState.STOPPED || getState() == JAMAgentState.INITIALIZED) {
            throw new IllegalStateException("Agente non ancora inizializzato o non ancora avviato: impossibile terminarlo.");
        } else {
            try {
                try {
                    adsl.removeRemoteMessageBox(myID);
                } catch (JAMADSLException ex) {
                    // eccezione ridotta al silenzio!! Ma tanto la post-condizione è soddisfatta,
                    // i.e. la message box di questo agente non e' registrata nell'ADSL!
                }
                // scorro la lista dei comportamenti associati a questo agente e
                // faccio terminare i comportamenti che sono ancora in esecuzione
                for (JAMBehaviour b : myBehaviours) {
                    b.done();
                }
                stato = JAMAgentState.STOPPED;
            } catch (RemoteException ex) {
                // se il collegamento con l'ADSL non è andato a buon fine,
                // rilanciamo la RemoteException in una forma di più alto livello
                throw new JAMADSLException(ex.getMessage(), ex);
            }
        }
    }

    /**
     * Invia un messaggio agli agenti che sono specificati come destinatari nel
     * messaggio stesso.
     * Questo comporta la scrittura del messaggio nella message box remota di ognuno
     * degli agenti destinatari.
     *
     * @param msg il messaggio da inviare agli agenti specificati come destinatari nel messaggio stesso
     * 
     * @throws InterruptedException
     *         se il thread corrente riceve un interrupt mentre è in wait su <code>writeMessage</code>
     * @throws JAMADSLException se il collegamento con l'ADSL non è andato a buon fine
     * @throws JAMRemoteMessageBoxException se si verifica un errore nello scrivere un messaggio su una message box remota
     */
    public void send(Message msg) throws InterruptedException, JAMADSLException, JAMRemoteMessageBoxException {
        if (msg == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        List<RemoteMessageBox> receivers = null;
        try {
            receivers = adsl.getRemoteMessageBox(msg.getReceiver());
        } catch (RemoteException ex) {
            throw new JAMADSLException("Si e' verificato un errore nel contattare l'oggetto ADSL", ex);
        }
        // se nell'ADSL non è registrato nessuno degli agenti specificati come destinatari => eccezione
        if (receivers.isEmpty()) {
            throw new JAMRemoteMessageBoxException("Impossibile inviare il messaggio: in questo momento non e' attivo nessuno degli agenti specificati come destinatari.");
        }

        for (RemoteMessageBox rmb : receivers) {
            try {
                rmb.writeMessage(msg);
            } catch (RemoteException ex) {
                throw new JAMRemoteMessageBoxException("Impossibile inviare il messaggio: si e' verificato un errore nello scrivere un messaggio su una message box remota", ex);
            }
        }
        /* BEGIN codice per GUI */
        setChanged();
        notifyObservers("SEND message " + msg.getPerformative() + " to " + msg.getReceiver() + "\n" + msg);
        /* END codice per GUI */
    }

    /**
     * Legge dalla propria message box il messaggio più vecchio.
     *
     * @return il messaggio più vecchio ricevuto
     *
     * @throws InterruptedException
     *         se il thread corrente riceve un interrupt mentre è in wait su <code>MessageBox.readMessage</code>
     */
    public Message receive() throws InterruptedException {
        return receive(new GenericAgentID());
    }

    /**
     * Legge dalla propria message box il più vecchio messaggio ricevuto da un certo agente (o gruppo di agenti).
     *
     * @param sender l'ID dell'agente (o gruppo di agenti) mittente dei messaggi che si vuole leggere
     * @return il più vecchio messaggio ricevuto da sender
     *
     * @throws InterruptedException
     *         se il thread corrente riceve un interrupt mentre è in wait su <code>MessageBox.readMessage</code>
     */
    public Message receive(AgentID sender) throws InterruptedException {
        if (sender == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        Message received = myBox.readMessage(sender);
        /* BEGIN codice per GUI */
        setChanged();
        notifyObservers("RECEIVE message " + received.getPerformative() + " from " + received.getSender() + "\n" + received);
        /* END codice per GUI */
        return received;
    }

    /**
     * Legge dalla propria message box il più vecchio messaggio ricevuto, di una certa performativa.
     *
     * @param tipo la performativa che deve avere il messaggio da leggere
     * @return il più vecchio messaggio ricevuto, di una certa performativa
     *
     * @throws InterruptedException
     *         se il thread corrente riceve un interrupt mentre è in wait su <code>MessageBox.readMessage</code>
     */
    public Message receive(Performative tipo) throws InterruptedException {
        if (tipo == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        Message received = myBox.readMessage(tipo);
        /* BEGIN codice per GUI */
        setChanged();
        notifyObservers("RECEIVE message " + received.getPerformative() + " from " + received.getSender() + "\n" + received);
        /* END codice per GUI */
        return received;
    }

    /**
     * Legge dalla propria message box il più vecchio messaggio ricevuto da un certo agente (o gruppo di agenti), e
     * di una certa performativa.
     *
     * @param sender l'ID dell'agente (o gruppo di agenti) mittente dei messaggi che si vuole leggere
     * @param tipo la performativa che deve avere il messaggio da leggere
     * @return il più vecchio messaggio ricevuto da <i>sender</i>, e di performativa <i>tipo</i>
     *
     * @throws InterruptedException
     *         se il thread corrente riceve un interrupt mentre è in wait su <code>MessageBox.readMessage</code>
     */
    public Message receive(AgentID sender, Performative tipo) throws InterruptedException {
        Message received = myBox.readMessage(sender, tipo);
        /* BEGIN codice per GUI */
        setChanged();
        notifyObservers("RECEIVE message " + received.getPerformative() + " from " + received.getSender() + "\n" + received);
        /* END codice per GUI */
        return received;
    }

    /**
     * @return true se in myBox è presente un qualunque messaggio;
     *         false altrimenti
     */
    public boolean isThereMessage() {
        return myBox.isThereMessage();
    }

    /**
     * @param sender ID dell'agente (o del gruppo di agenti) mittente del messaggio che si vuole cercare
     * @return true se in myBox è presente almeno un messaggio il cui mittente matcha con sender;
     *         false altrimenti
     */
    public boolean isThereMessage(AgentID sender) {
        return myBox.isThereMessage(sender);
    }

    /**
     * @param tipo la performativa che viene usata come criterio di ricerca
     * @return true se in myBox è presente almeno un messaggio caratterizzato dalla performativa p;
     *         false altrimenti
     */
    public boolean isThereMessage(Performative tipo) {
        return myBox.isThereMessage(tipo);
    }

    /**
     * @param sender ID dell'agente (o del gruppo di agenti) mittente del messaggio che si vuole leggere
     * @param tipo la performativa che viene usata come criterio di ricerca
     * @return true se in myBox è presente almeno un messaggio caratterizzato
     *              dalla performativa p, e il cui mittente matcha con a;
     *         false altrimenti
     */
    public boolean isThereMessage(AgentID sender, Performative tipo) {
        return myBox.isThereMessage(sender, tipo);
    }

    public PersonalAgentID getMyID() {
        return myID;
    }

    public String getAdslIp() {
        return adsl_ip;
    }

    public String getAdslName() {
        return adsl_name;
    }

    public int getAdslPort() {
        return adsl_port;
    }

    public void setAdslIP(String adsl_ip) {
        if (adsl_ip == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        this.adsl_ip = adsl_ip;
    }

    public void setAdslName(String adsl_name) {
        if (adsl_name == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        this.adsl_name = adsl_name;
    }

    public void setAdslPort(int adsl_port) {
        if (adsl_port <= 0) {
            throw new IllegalArgumentException("Il numero della porta deve essere un intero positivo.");
        }
        this.adsl_port = adsl_port;
    }

    /**
     * @return la stringa con l'URL dell'ADSL, costruito a partire dai
     *         parametri inizializzati nel costruttore, che viene usato
     *         per fare la lookup dell'ADSL presso il registro RMI.
     */
    public String getUrlADSL() {
        return "//" + adsl_ip + ":" + adsl_port + "/" + adsl_name;
    }

    /**
     * @return il numero di comportamenti associati a questo agente
     */
    public int getNumBehaviours() {
        return myBehaviours.size();
    }

    /**
     * @return il numero di comportamenti in stato <i>in terminazione</i>/<i>terminato</i>,
     * associati a questo agente.
     */
    public int getNumBehavioursTerminati() {
        int n = 0;
        for (JAMBehaviour b : myBehaviours) {
            if (b.isDone()) {
                n++;
            }
        }
        return n;
    }

    /**
     * @return il numero di comportamenti in stato <i>avviabile</i>,
     * associati a questo agente.
     */
    public int getNumBehavioursAvviabili() {
        int n = 0;
        for (JAMBehaviour b : myBehaviours) {
            if (b.isAvviabile()) {
                n++;
            }
        }
        return n;
    }

    /**
     * @return lo stato in cui si trova questo agente
     *
     * @see JAMAgentState
     */
    public JAMAgentState getState() {
        return stato;
    }
}
