/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jam.eccezioni;

/**
 * Un'eccezione lanciata da un qualche componente del JAM.
 *
 * @author tosco
 */
public class JAMException extends Exception {

    public JAMException(String message, Throwable cause) {
        super(message, cause);
    }

    public JAMException(Throwable cause) {
        super(cause);
    }

    public JAMException(String message) {
        super(message);
    }

    public JAMException() {
        super();
    }
}
