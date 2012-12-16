package jam;

import java.rmi.RemoteException;

/**
 * Questa classe estende le funzionalità di <code>ADSLImpl</code>,
 * ponendo l'accento sul fatto che, in ogni istante, può esistere uno e un solo
 * oggetto ADSL.
 * Questo vincolo è implementato applicando il pattern Singleton.
 * <br>
 * Per istanziare questa classe non è possibile usare un costruttore, ma bisogna
 * invocare il metodo statico <code>getInstance</code>.
 *
 * @author tosco
 */
public class ADSLImplSingleton extends ADSLImpl {

    private static ADSLImplSingleton instance = null;

    private ADSLImplSingleton() throws RemoteException {
        super("adsl", 1099);
    }

    private ADSLImplSingleton(String nome, int porta) throws RemoteException {
        super(nome, porta);
    }

    public static ADSLImplSingleton getInstance() throws RemoteException {
        if (instance == null) {
            instance = new ADSLImplSingleton();
        }
        return instance;
    }

    /**
     *
     * @param nome
     * @param porta
     * @return
     * @throws IllegalStateException se l'oggetto ADSL è già stato creato, ed ha numero
     *                               di porta e/o nome diversi da quelli specificati come argomenti
     * @throws RemoteException
     */
    public static ADSLImplSingleton getInstance(String nome, int porta) throws RemoteException {
        if (instance != null && (instance.getPort()!=porta || !nome.equals(instance.getName()))) {
            throw new IllegalStateException("Oggetto ADSL già creato, con numero di porta e/o nome diversi da quelli specificati.");
        }
        instance = new ADSLImplSingleton(nome, porta);
        return instance;
    }
}
