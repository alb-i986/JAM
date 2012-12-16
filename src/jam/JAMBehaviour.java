/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jam;

import jam.eccezioni.*;

/**
 * Un comportamento è sempre associato ad un agente, ed è un pezzo di codice
 * che può essere eseguito dall'agente.
 * <p>
 * Un comportamento ha tre stati: avviabile, avviato (aka <i>done</i>), in terminazione/terminato.
 * <ul>
 * <li>è <i>avviabile</i> non appena creato, oppure diventa <i>avviabile</i> con <code>reset()</code>
 * <li>da <i>avviabile</i> passa ad <i>avviato</i> con <code>JAMAgent.start()</code>
 * <li>da <i>avviato</i> passa a <i>in terminazione</i> con <code>done()</code>
 * <li>da <i>in terminazione</i> passa a <i>terminato</i> quando il thread termina
 *     effettivamente l'esecuzione di <code>action()</code>
 * </ul>
 * <p>
 * Un comportamento viene effettivamente eseguito solo quando viene avviato da <code>JAMAgent.start()</code>.
 * Può essere terminato con <code>done()</code>.
 * Può anche essere resettato con <code>reset()</code>: in tal modo
 * può essere rieseguito da <code>JAMAgent.start()</code>.
 * <p>
 * Ogni comportamento concreto deve definire i metodi <code>setup()</code>, <code>action()</code>
 * e <code>dispose()</code> nel seguente modo:
 * ogni InterruptedException deve essere rilanciata in forma di JAMBehaviourInterruptedException.
 *
 * @author tosco
 */
public abstract class JAMBehaviour implements Runnable {

    private boolean done;       // indica se il comportamento e' stato eseguito completamente dall'ultima inizializzazione dell'agente
    private Thread myThread;    // il riferimento all'oggetto thread che esegue questo comportamento
    protected JAMAgent myAgent; // l'agente possessore di questo comportamento

    public JAMBehaviour(JAMAgent agent) {
        myAgent = agent;
        done = false;
        myThread = null;
    }

    /**
     * Porta questo comportamento nello stato <i>terminato</i>.
     * <p>
     * Nello specifico, causa indirettamente la terminazione di questo comportamento,
     * facendo eventualmente uscire dallo stato di wait il thread che lo esegue
     * (a prescindere dal successo o insuccesso della sequenza di operazioni).
     * <br>
     * Serve in particolare per i WhileBehaviour, per terminare il loop di
     * esecuzione del comportamento.
     * <p>
     * Precond.: il thread di questo comportamento deve essere ancora in esecuzione
     * o quantomeno deve essere stato eseguito almeno una volta dall'ultima inizializzazione
     * dell'agente
     *
     * @throws IllegalStateException se questo comportamento, che si vorrebbe terminare, 
     *                               si trova nello stato <i>avviabile</i>
     */
    public void done() {
        if (isAvviabile()) {
            throw new IllegalStateException("Il thread del comportamento che si vuole terminare non è mai stato avviato.");
        }
        done = true;
        // il thread potrebbe essere in stato di wait su una receive() o una send() di JamAgent,
        // allora e' necessario forzare l'uscita dalla wait con interrupt()
        myThread.interrupt();
    }

    /**
     * Resetta questo comportamento, (ri)portandolo nello stato <i>avviabile</i>.
     */
    public void reset() {
        this.done = false;
        myThread=null;
    }

    /**
     * @return true se e solo questo comportamento si trova nello stato <i>in terminazione</i>/<i>terminato</i>,
     *              ovvero se è stato invocato <code>done()</code> dopo l'ultima
     *              inizializzazione con <code>reset()</code>;
     */
    public boolean isDone() {
        return done;
    }

    /**
     * @return true se e solo se questo comportamento si trova nello stato <i>avviabile</i>,
     *              ovvero se non è stato ancora eseguito dall'ultima inizializzazione
     *              con <code>reset()</code>
     */
    public boolean isAvviabile() {
        return myThread == null;
    }

    /**
     * Associa questo comportamento al thread che lo esegue.<p>
     * E' lecito non associare nessun thread, passando null come argomento:
     * in tal caso l'effetto è quello di poter rieseguire il comportamento da capo
     * invocando <code>JAMAgent.start()</start>
     *
     * @throws IllegalArgumentException se il thread da associare a questo comportamento è null
     */
    public void setThread(Thread t) {
        if(t==null)
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        myThread = t;
    }

    /**
     * Mette in sleep il thread che sta eseguendo questo comportamento
     * 
     * @param ms il numero di millisecondi durante i quali il thread rimane in sleep
     * @throws InterruptedException
     */
    public void sleep(long ms) throws InterruptedException {
        Thread.sleep(ms);
    }

    /**
     * Il codice da eseguire prima di lanciare il metodo <code>action()</code>.
     * <p>
     * <b>Nota per gli implementatori</b>: ogni InterruptedException deve essere
     * rilanciata in forma di JAMBehaviourInterruptedException.
     *
     * @throws JAMBehaviourInterruptedException se il thread viene interrotto da un interrupt mentre è in wait
     */
    public abstract void setup() throws JAMBehaviourInterruptedException;

    /**
     * Il codice del comportamento da eseguire una o piu' volte.
     * <p>
     * <b>Nota per gli implementatori</b>: in action() ci deve essere almeno una chiamata a <code>done()</code>
     * in modo da far terminare l'esecuzione del comportamento (questo vale
     * in particolare per i JAMWhileBehaviour).
     * <br>
     * Inoltre, ogni InterruptedException deve essere rilanciata in forma di
     * JAMBehaviourInterruptedException.
     *
     * @throws JAMBehaviourInterruptedException se il thread viene interrotto da un interrupt mentre è in wait
     */
    public abstract void action() throws JAMBehaviourInterruptedException;

    /**
     * Il codice da eseguire prima di terminare l'esecuzione del comportamento.
     * <p>
     * <b>Nota per gli implementatori</b>: ogni InterruptedException deve essere
     * rilanciata in forma di JAMBehaviourInterruptedException.
     *
     * @throws JAMBehaviourInterruptedException se il thread viene interrotto da un interrupt mentre è in wait
     */
    public abstract void dispose() throws JAMBehaviourInterruptedException;

}
