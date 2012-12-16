/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jam.eccezioni;

/**
 * Segnala l'avvenuto invio di un interrupt ad un thread che era in wait mentre
 * stava eseguendo il comportamento ad esso associato.
 * <p>
 * Può essere indirettamente causata dal metodo done() di JAMBehaviour, il quale
 * fa terminare l'esecuzione del comportamento.
 * 
 * @author tosco
 */
public class JAMBehaviourInterruptedException extends JAMBehaviourException {

    public JAMBehaviourInterruptedException(Throwable cause) {
        super(cause);
    }

    public JAMBehaviourInterruptedException(String message) {
        super(message);
    }

    public JAMBehaviourInterruptedException() {
        super();
    }

    public JAMBehaviourInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }
}
