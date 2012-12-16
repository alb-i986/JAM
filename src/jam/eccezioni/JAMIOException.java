/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jam.eccezioni;

/**
 *
 * @author tosco
 */
public class JAMIOException extends JAMException {

    public JAMIOException(Throwable cause) {
        super(cause);
    }

    public JAMIOException() {
        super();
    }

    public JAMIOException(String message) {
        super(message);
    }
}
