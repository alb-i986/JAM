package TestFinale1;

import jam.*;
import jam.eccezioni.*;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 *
 * @author as
 */
public class ProvaTimeGUI {

    public static void main(String[] args)
            throws NotBoundException, MalformedURLException, JAMRemoteMessageBoxException, RemoteException {

        AskTimeAgent asktimeagent = new AskTimeAgent("Matteo", "Baldoniaosifgaihgapiwghpiawgtapiwetgagt");
        asktimeagent.addBehaviour(new AskTimeBehaviour(asktimeagent));
        //asktimeagent.addBehaviour(new AskTimeBehaviourRefuse(asktimeagent));
        //asktimeagent.addBehaviour(new AskTimeBehaviourNotUnderstood(asktimeagent));
        JAMAgentMonitor clientGui = new JAMAgentMonitor(asktimeagent);

        TimeProviderAgent berlino = new TimeProviderAgent("Berlino");
        JAMAgentMonitor serverGui1 = new JAMAgentMonitor(berlino);

        TimeProviderAgent tokyo = new TimeProviderAgent("Tokyo");
        JAMAgentMonitor serverGui2 = new JAMAgentMonitor(tokyo);
    }
}
