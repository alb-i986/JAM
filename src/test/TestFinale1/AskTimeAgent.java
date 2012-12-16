package TestFinale1;

import jam.*;
import jam.eccezioni.*;
import java.net.MalformedURLException;
import java.rmi.*;

public class AskTimeAgent extends JAMAgent {

    /**
     *
     * @param category
     * @param name
     * 
     * @throws JAMRemoteMessageBoxException se la creazione dell'oggetto remoto
     *                                      MessageBox non è andata a buon fine
     */
    public AskTimeAgent(String category, String name) throws JAMRemoteMessageBoxException {
        super(new PersonalAgentID(category, name));
    }

    public static void main(String[] args) throws JAMADSLException {
        try {
            AskTimeAgent asktimeagent = new AskTimeAgent("Matteo", "Baldoni");
            asktimeagent.addBehaviour(new AskTimeBehaviour(asktimeagent));
            AskTimeAgent asktimeagent1 = new AskTimeAgent("Mario", "Rossi");
            asktimeagent1.addBehaviour(new AskTimeBehaviourRefuse(asktimeagent1));
            AskTimeAgent asktimeagent2 = new AskTimeAgent("Mercoledi", "Adams");
            asktimeagent2.addBehaviour(new AskTimeBehaviourNotUnderstood(asktimeagent2));
            asktimeagent.init();
            asktimeagent.start();
            asktimeagent1.init();
            asktimeagent1.start();
            asktimeagent2.init();
            asktimeagent2.start();
        } catch (JAMRemoteMessageBoxException ex) {
            System.err.println("Creazione della message box di un'agente non riuscita");
        }
    }
}
