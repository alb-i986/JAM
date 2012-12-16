/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jam.eccezioni;

/**
 * Indica un errore nella consultazione di un oggetto MessageBoxNoSync.
 * <p>
 * Può essere lanciata perché la coda di messaggi è piena e quindi non è possibile effettuare una scrittura;<br>
 * oppure perché la coda di messaggi è vuota e quindi non è possibile effettuare una lettura;<br>
 * oppure ancora perché nella coda non è presente nessun messaggio che soddisfi i criteri di ricerca
 * 
 * @author tosco
 */
public class JAMMessageBoxException extends JAMException {

    public JAMMessageBoxException() {
        super();
    }

    public JAMMessageBoxException(String msg) {
        super(msg);
    }

    public JAMMessageBoxException(Throwable cause) {
        super(cause);
    }

    public JAMMessageBoxException(String message, Throwable cause) {
        super(message,cause);
    }
}
