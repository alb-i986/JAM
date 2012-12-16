/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jam.eccezioni;

/**
 * Indica un errore nell'esecuzione di un comportamento.
 * 
 * @author tosco
 */
public class JAMBehaviourException extends JAMException {

    public JAMBehaviourException(String message, Throwable cause) {
        super(message, cause);
    }

    public JAMBehaviourException(Throwable cause) {
        super(cause);
    }

    public JAMBehaviourException(String message) {
        super(message);
    }

    public JAMBehaviourException() {
        super();
    }
}
