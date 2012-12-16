/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jam;

import jam.eccezioni.*;

/**
 * Un comportamento che, una volta avviato, viene eseguito una volta sola.
 *
 * @author st064481
 */
public abstract class JAMSimpleBehaviour extends JAMBehaviour {

    public JAMSimpleBehaviour(JAMAgent agent) {
        super(agent);
    }

    /**
     * L'esecuzione di un comportamento "simple" si divide in tre fasi.
     * <ol>
     * <li>setup: il comportamento viene inizializzato
     * <li>il comportamento viene eseguito (una volta sola)
     * <li>dispose: le ultime operazioni da effettuare prima che il comportamento termini
     * </ol>
     */
    public void run() {
        // una JAMBehaviourInterruptedException lanciata da setup() o da dispose() la considero un errore;
        // una JAMBehaviourInterruptedException lanciata da action() non la considero un errore se e' causata da done()
        try {
            setup();
        } catch (JAMBehaviourInterruptedException e) {
            System.err.println(e + "\nComportamento dell'agente interrotto durante il setup del comportamento.");
            return;
        }
        Thread.interrupted(); // pulisco l'interrupted status del thread
        try {
            action();
        } catch (JAMBehaviourInterruptedException e) {
            // se l'interrupt proviene da done() => tutto ok:
            // semplicemente l'utente ha voluto terminare l'agente
            if (isDone()) { 
                return;
            }
            System.err.println(e + "\nComportamento dell'agente interrotto durante l'esecuzione del comportamento.");
        } finally {
            Thread.interrupted(); // pulisco l'interrupted status del thread
            try {
                dispose();
                done();
            } catch (JAMBehaviourInterruptedException e) {
                System.err.println(e + "\nComportamento dell'agente interrotto durante la dispose del comportamento.");
            }
        }

    }
}
