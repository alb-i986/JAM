package TestFinale1;

import jam.*;
import jam.eccezioni.*;

public class TimeProviderBehaviourServiOra extends JAMWhileBehaviour {
    private LogFile log;
    public TimeProviderBehaviourServiOra(JAMAgent myAgent) {
        super(myAgent);
        log = new LogFile();
    }
    public void setup() throws JAMBehaviourInterruptedException {
        try {
            log.startLog(myAgent.getMyID().toString().trim(), "Log file per " + myAgent.getMyID());
            log.log("0- Inizializzazione comportamento.");
        } catch(JAMIOException err) {
            System.out.println("Errore: "+ err);
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
        try {
            log.log("1- Start del comportamento, attesa messaggio iniziale.");
            Message message = myAgent.receive();
            log.log("2- Ricevuto messaggio iniziale, inizio sua analisi");
            if(message.getPerformative() != Performative.REQUEST) {
                log.log("3- Performativa diversa da REQUEST, preparo REFUSE.", message);
                Message request = new Message(
                    myAgent.getMyID(), 
                    message.getSender(), 
                    Performative.REFUSE, 
                    "Performativa non accettata", 
                    message
                );
                log.log("4- Invio REFUSE", request);
                myAgent.send(request);
                log.log("5- Inviata REFUSE", request);
            } else if(!message.getContent().equals("Che ora e'?")) {
                log.log("6- Non capisco il content, preparo NOT_UDERSTOOD.", message);
                Message not_understood = new Message(
                    myAgent.getMyID(), 
                    message.getSender(), 
                    Performative.NOT_UNDERSTOOD, 
                    "REQUEST non compresa", 
                    message
                );
                log.log("7- Invio NOT_UNDERSTOOD.", not_understood);
                myAgent.send(not_understood);
                log.log("8- Invita NOT_UNDERSTOOD.", not_understood);
            } else {
                log.log("9- REQUEST accettabile, preparo AGREE.", message);
                AgentID agentid = message.getSender();
                Message agree = new Message(
                    myAgent.getMyID(), 
                    agentid, 
                    Performative.AGREE, 
                    "REQUEST accettata", 
                    message
                );
                log.log("10- Invio AGREE.", agree);
                myAgent.send(agree);
                log.log("11- Inviata AGREE.", agree);
                log.log("12- Mi metto in attesa conferma da " + agentid + ".");
                sleep(1000);
                log.log("13- Mi risveglio e verifico se arrivata conferfa da " + agentid + ".");
                if(!myAgent.isThereMessage(agentid, Performative.INFORM)) {
                    log.log("14- Nessuna conferma da " + agentid + ".");
                } else {
                    Message confirm = myAgent.receive(agentid, Performative.INFORM);
                    log.log("15- Arrivata conferma da " + agentid + ".", confirm);
                    if(confirm.getContent().equals("Aspetto l'ora.")) {
                        log.log("16- Preparo messaggio con ora per " + agentid + ".");
                        Message inform = new Message(
                            myAgent.getMyID(), 
                            agentid, 
                            Performative.INFORM, 
                            "Ora: "+ ((TimeProviderAgent)myAgent).getOra()
                        );
                        log.log("17- Invio ora a " + agentid + ".", inform);
                        myAgent.send(inform);
                        log.log("19- Inviata ora a " + agentid + ".", inform);
                    }
                }
            }
        } catch (JAMRemoteMessageBoxException ex) {
            System.err.println(ex.getMessage());
        } catch (InterruptedException ex) {
            throw new JAMBehaviourInterruptedException(ex.getMessage(), ex);
        } catch (JAMADSLException err1) {
            System.out.println("Errore: " + err1);
            done();
        } catch (JAMIOException err2) {
            System.out.println("Errore: " + err2);
            done();
        }
    }
}
