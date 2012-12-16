package TestFinale1;

import jam.*;
import jam.eccezioni.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TimeProviderBehaviourCalcola extends JAMWhileBehaviour {
    public TimeProviderBehaviourCalcola(JAMAgent myAgent) {
        super(myAgent);
    }
    public void setup() throws JAMBehaviourInterruptedException {
        ((TimeProviderAgent)myAgent).resetOra();
    }
    public void dispose() throws JAMBehaviourInterruptedException { }
    public void action() throws JAMBehaviourInterruptedException {
        try {
            ((TimeProviderAgent) myAgent).incrementaOra();
            sleep(1000);
        } catch (InterruptedException ex) {
            throw new JAMBehaviourInterruptedException(ex.getMessage(), ex);
        }
    }
}
