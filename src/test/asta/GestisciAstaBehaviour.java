package asta;

import jam.eccezioni.*;
import jam.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author tosco
 */
public class GestisciAstaBehaviour extends BanditoreBehaviour {

    public GestisciAstaBehaviour(JAMAgent agent) {
        super(agent);
    }

    /**
     * Fa partire il timer che segnala quando termina l'asta.
     */
    public void setup() {
        System.out.println("Asta iniziata.");
        AgenteBanditore me = (AgenteBanditore) myAgent;
        JAMAgentState previouState = me.getState();
        // calcolo la data in cui deve terminare l'asta
        Calendar termine = new GregorianCalendar();
        termine.add(Calendar.MINUTE, me.getDurataAstaMinuti());
        me.setTermineAsta(termine);
    }

    /**
     * Stampa a video l'esito dell'asta, e lo comunica anche a tutti i clienti
     * che hanno partecipato all'asta con almeno un'offerta.<br>
     * Infine resetta l'asta in modo che sia possibile riavviarla.
     */
    public void dispose() {
        AgenteBanditore me = (AgenteBanditore) myAgent;
        System.out.println("Asta conclusa.");
        if (me.nessunPartecipante()) {
            System.out.println("Nessun vincitore.");
        } else {
            System.out.println("Il vincitore e' " + me.getNomeMigliorOfferente() + " che si e' aggiudicato l'oggetto a " + me.getOffertaCorrente() + " euro.");
            try {
                System.out.println("Comunico il risultato dell'asta ad ogni partecipante.");
                me.sendResults();
            } catch (JAMADSLException ex) {
                System.err.println(ex.getMessage());
            }
        }
        me.resetAsta();
    }

    public void action() throws JAMBehaviourInterruptedException {
        AgenteBanditore me = (AgenteBanditore) myAgent;
        // attendo l'arrivo di un messaggio
        if (me.astaIsOpen()) {
            System.out.println("\nAttendo che arrivi un'offerta.");
        } else {
            System.out.println("\nAttendo che arrivi un messaggio.");
        }
        Message msg;
        try {
            msg = me.receive();
        } catch (InterruptedException e) {
            throw new JAMBehaviourInterruptedException(e.getMessage(), e);
        }
        System.out.println("Ho ricevuto un messaggio:");
        System.out.println(msg);
        System.out.println("Ora elaboro la risposta.");

        try {
            if (msg.getPerformative() == Performative.QUERY_IF) {
                if (!"Valore corrente?".equalsIgnoreCase(msg.getContent())) {
                    // errore: content non riconosciuto
                    Message answer = new Message(me.getMyID(), msg.getSender(), Performative.NOT_UNDERSTOOD, "");
                    me.send(answer);
                    System.out.println("Content non riconosciuto. Inviata NOT_UNDERSTOOD:");
                    System.out.println(answer);
                } else if (!me.astaIsOpen()) { // la domanda del cliente era ben formata ma l'oggetto è già stato aggiudicato
                    Message answer = new Message(me.getMyID(), msg.getSender(), Performative.REFUSE, me.getNomeMigliorOfferente());
                    me.send(answer);
                    System.out.println("Asta gia' conclusa. Inviata REFUSE:");
                    System.out.println(answer);
                } else { // la domanda del cliente era ben formata e l'oggetto non è ancora stato aggiudicato
                    Message answer = new Message(me.getMyID(), msg.getSender(), Performative.INFORM, me.getOffertaCorrente() + "\n" + me.getNomeMigliorOfferente());
                    me.send(answer);
                    System.out.println("Richiesta OK. Inviata INFORM:");
                    System.out.println(answer);
                }
            } else if (msg.getPerformative() == Performative.REQUEST) {
                try {
                    int nuovaOfferta = Integer.parseInt(msg.getContent());
                    if (me.getOffertaCorrente() >= nuovaOfferta) { // offerta inferiore alla miglior offerta corrente
                        Message answer = new Message(me.getMyID(), msg.getSender(), Performative.REFUSE, "");
                        me.send(answer);
                        System.out.println("Offerta non accettata perche' inferiore alla miglior offerta corrente. Inviata REFUSE:");
                        System.out.println(answer);
                    } else { // accetto l'offerta
                        me.addCliente(msg.getSender());
                        me.setOffertaCorrente(nuovaOfferta);
                        me.setMigliorOfferente(msg.getSender());
                        Message answer = new Message(me.getMyID(), msg.getSender(), Performative.INFORM, "");
                        me.send(answer);
                        System.out.println("Offerta accettata. Inviata INFORM:");
                        System.out.println(answer);
                    }
                } catch (NumberFormatException e) { // errore: content non riconosciuto
                    Message answer = new Message(me.getMyID(), msg.getSender(), Performative.NOT_UNDERSTOOD, "");
                    me.send(answer);
                    System.out.println("Content non riconosciuto (doveva essere un intero rappresentante l'offerta). Inviata NOT_UNDERSTOOD:");
                    System.out.println(answer);
                }
            } else { // se non ho ricevuto nè QUERY_IF nè REQUEST
                Message answer = new Message(me.getMyID(), msg.getSender(), Performative.REFUSE, "");
                me.send(answer);
                System.out.println("Performativa non riconosciuta. Inviata REFUSE:");
                System.out.println(answer);
            }
        } catch (InterruptedException e) {
            throw new JAMBehaviourInterruptedException(e.getMessage(), e);
        } catch (JAMADSLException ex) {
            System.err.println(ex.getMessage());
            // se si è verificato qualche problema nel contattare l'ADSL, termino
            done();
        } catch (JAMRemoteMessageBoxException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
