package TestFinale1;

import jam.*;
import jam.eccezioni.*;

public class AskTimeBehaviourRefuse extends JAMWhileBehaviour {
    private LogFile log;
    public AskTimeBehaviourRefuse(JAMAgent myAgent) {
        super(myAgent);
        log = new LogFile();
    }
    public void setup() throws JAMBehaviourInterruptedException {
        try {
            log.startLog(myAgent.getMyID().toString().trim(), "Log file per " + myAgent.getMyID());
            log.log("0- Inizializzazione comportamento.");
        } catch(JAMIOException err) {
            System.out.println("Errore: " + err);
            done();
        }
    }
    public void dispose() throws JAMBehaviourInterruptedException {
        try {
            log.log("END- Esecuguita dispose.");
            log.endLog();
        } catch(JAMIOException err) {
            System.out.println("Errore: " + err);
            done();
        }
    }
    public void action() throws JAMBehaviourInterruptedException {
        try  {
            log.log("1- Start del comportamento, attesa messaggio iniziale.");
            CategoryAgentID timeProviderCategoryID = new CategoryAgentID("Time Provider");
            log.log("2- Preparo REQUEST dell'ora.");
            Message request = new Message(
                myAgent.getMyID(), 
                timeProviderCategoryID, 
                // PERFORMATIVA ERRATA
                Performative.INFORM, 
                "Che ora e'?"
            );
            log.log("3- Invio REQUEST.", request);
            myAgent.send(request);
            log.log("4- Invita REQUEST.", request);
            log.log("5- Attendo risposta.");
            Message answer = myAgent.receive(timeProviderCategoryID);
            log.log("6- Trovata risposta.", answer);
            if(answer.getPerformative() != Performative.AGREE) {
                log.log("7- La risposta non e' una agree eseguo una done.", answer);
                done();
            } else {
                log.log("8- La risposta e' una agree, prepara la conferma.", answer);
                AgentID selectedProvider = answer.getSender();
                Message confirm = new Message(
                    myAgent.getMyID(),
                    selectedProvider, 
                    Performative.INFORM, 
                    "Aspetto l'ora."
                );
                log.log("9- Invio la conferma.", confirm);
                myAgent.send(confirm);
                log.log("10- Inviata la conferma.", confirm);
                log.log("11- Attendo il messaggio con l'ora da " + selectedProvider);
                sleep(2000);
                log.log("12- Controllo se arrivato il messaggio con l'ora da " + selectedProvider);
                if(myAgent.isThereMessage(selectedProvider, Performative.INFORM)) {
                    log.log("13- Leggo il messaggio con l'ora da " + selectedProvider);
                    Message inform = myAgent.receive(selectedProvider, Performative.INFORM);
                    log.log("14- Letto il messaggio con l'ora da " + selectedProvider, inform);
                    System.out.println(inform);
                } else {
                    log.log("15- Nessun messagggio da " + selectedProvider);
                    System.out.println("Nessuna risposta, rinuncio.");
                }
                done();
            }
        } catch (JAMRemoteMessageBoxException ex) {
            System.err.println(ex.getMessage());
        } catch (InterruptedException ex) {
            throw new JAMBehaviourInterruptedException(ex.getMessage(), ex);
        } catch (JAMADSLException err1) {
            System.out.println("Errore: " + err1);
        } catch (JAMIOException err2) {
            System.out.println("Errore: " + err2);
        }
        finally {
            done();
        }
    }
}
