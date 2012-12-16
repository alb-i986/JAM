/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jam;

import jam.eccezioni.*;

/**
 * Un comportamento che, una volta avviato, viene eseguito ciclicamente,
 * finché non viene terminato con <code>done()</code>.
 * 
 * @author tosco
 */
public abstract class JAMWhileBehaviour extends JAMBehaviour {

    public JAMWhileBehaviour(JAMAgent agent) {
        super(agent);
    }

    /**
     * L'esecuzione di un comportamento "while" si divide in tre fasi, che vengono
     * ripetute ciclicamente finché il comportamento non viene terminato con
     * <code>done()</code>.
     * <ol>
     * <li>setup: il comportamento viene inizializzato
     * <li>il comportamento viene eseguito ciclicamente, finche' non viene invocato <code>done()</code>
     * <li>dispose: le ultime operazioni da effettuare prima che il comportamento termini
     * </ol>
     */
    public void run() {
        // una JAMBehaviourInterruptedException lanciata da setup() o da dispose() la considero un errore;
        // una JAMBehaviourInterruptedException lanciata da action() non la considero un errore se e' causata da done()
        try {
            setup();
        } catch (JAMBehaviourInterruptedException e) {
            System.err.println("Comportamento dell'agente interrotto durante il setup del comportamento.");
            return;
        }
        Thread.interrupted(); // pulisco l'interrupted status del thread
        try {
            while (!isDone()) {
                action();
            }
        } catch (JAMBehaviourInterruptedException e) {
            if (isDone()) {
                // se l'interrupt proviene da done() => tutto ok:
                // semplicemente l'utente ha voluto terminare questo comportamento
                return;
            }
            System.err.println("Comportamento dell'agente interrotto durante l'esecuzione del comportamento.");
        } finally {
            Thread.interrupted(); // pulisco l'interrupted status del thread
            try {
                dispose();
            } catch (JAMBehaviourInterruptedException e) {
                System.err.println("Comportamento dell'agente interrotto durante la dispose del comportamento.");
            }
        }
    }
}
