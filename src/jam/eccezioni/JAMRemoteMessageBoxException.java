/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jam.eccezioni;

/**
 * Indica un problema relativo ad una RemoteMessageBox.
 * <p>
 * Può trattarsi di un errore nel contattare una message box remota, che
 * potrebbe ad esempio essere dovuto al fatto che l'oggetto remoto non
 * è attivo.
 * <br>
 * Oppure il problema può riguardare la creazione di un oggetto di tipo 
 * RemoteMessageBox.
 *
 * @author tosco
 */
public class JAMRemoteMessageBoxException extends JAMMessageBoxException {

    public JAMRemoteMessageBoxException(String msg) {
        super(msg);
    }

    public JAMRemoteMessageBoxException() {
        super();
    }

    public JAMRemoteMessageBoxException(String msg, Throwable cause) {
        super(msg,cause);
    }

}
