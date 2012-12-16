/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jam.eccezioni;

/**
 * Indica un errore nella consultazione dell'ADSL.
 * <p>
 * Può essere lanciata perché:
 * <ul>
 * <li>si è cercato di registrare nell'ADSL una RemoteMessageBox già registrata;
 * <li>oppure perché si è cercato di cancellare dall'ADSL una RemoteMessageBox non esistente;
 * <li>oppure perché non è stato possibile contattare l'ADSL.
 * </ul>
 *
 * @author tosco
 */
public class JAMADSLException extends JAMException {

    public JAMADSLException(Throwable cause) {
        super(cause);
    }

    public JAMADSLException(String message, Throwable cause) {
        super(message,cause);
    }

    public JAMADSLException(String message) {
        super(message);
    }

    public JAMADSLException() {
        super();
    }
}
