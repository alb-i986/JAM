/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package asta;

import jam.eccezioni.*;
import jam.*;
import java.util.Random;
import java.util.Scanner;

/**
 * Questo comportamento permette di partecipare ad un'asta, cercando di aggiudicarsi
 * l'oggetto messo all'asta.
 * <p>
 * Un AgenteCliente può avere più comportamenti di questo tipo, uno per ogni asta 
 * a cui vuole partecipare.
 *
 * @author tosco
 */
public class FaiOffertaBehaviour extends ClienteBehaviour {

    private int miaOffertaMax; // la disponibilita' massima per l'asta a cui sto partecipando
    private PersonalAgentID idBanditore; // il banditore che bandisce l'asta a cui voglio partecipare
    private int miaOffertaCorrente = 0; // il prezzo finale a cui mi sono aggiudicato l'oggetto
    private boolean rinunciato = false; // se true => sicuramente non sarò il vincitore dell'asta

    /**
     * Crea un nuovo comportamento per un agente-cliente, che fa un'offerta all'
     * asta bandita da <i>banditoreID</i>
     *
     * @param myAgent
     * @param banditoreID
     */
    public FaiOffertaBehaviour(JAMAgent myAgent, PersonalAgentID banditoreID) {
        super(myAgent);
        idBanditore = banditoreID;
    }

    /**
     * Inizializza l'offerta massima che questo agente puo' fare per quest'asta
     * con una somma di euro a caso compresa tra 0 e la quantità di denaro 
     * presente nel portafoglio dell'agente
     */
    public void setup() {
        AgenteCliente me = (AgenteCliente) myAgent;
        int portafoglio = me.getPortafoglio();
        Random r = new Random();
        miaOffertaMax = r.nextInt(portafoglio + 1);
        System.out.println("Per questa asta la mia offerta massima e' " + miaOffertaMax);
    }

    /**
     * Stampa a video l'esito dell'asta alla quale il cliente che esegue questo
     * comportamento ha partecipato.
     * <br>
     * In particolare, stampa se si è aggiudicato l'oggetto, e a quanto.
     *
     * @throws JAMBehaviourInterruptedException se il thread che esegue questo
     * comportamento riceve un interrupt mentre sta aspettando di ricevere dal
     * banditore il messaggio con l'esito dell'asta
     */
    public void dispose() throws JAMBehaviourInterruptedException {
        System.out.println("\nOfferte terminate.");
        AgenteCliente me = (AgenteCliente) myAgent;
//        if (rinunciato) {
//            System.out.println("Non sono riuscito ad aggiudicarmi l'oggetto.");
//        } else {
        // (se non ho rinunciato a fare offerte), aspetto il msg da parte del
        // banditore che mi comunica se sono il vincitore dell'asta
        System.out.println("Attendo la comunicazione sull'esito dell'asta da parte del banditore.");
        Message m;
        try {
            m = me.receive(idBanditore);
        } catch (InterruptedException ex) {
            throw new JAMBehaviourInterruptedException(ex.getMessage(), ex);
        }
        System.out.println("Arrivato messaggio dal banditore:\n" + m);
        if ("Sei il vincitore.".equals(m.getContent())) {
            System.out.println("Sono riuscito ad aggiudicarmi l'oggetto per " + miaOffertaCorrente + " euro.");
        } else {
            System.out.println("Non sono riuscito ad aggiudicarmi l'oggetto.");
        }
//        }
    }

    /**
     * Se il cliente ha una disponibilità finanziaria sufficiente, e se l'asta
     * non è ancora terminata, fa un'offerta al banditore per aggiudicarsi
     * l'oggetto all'asta.
     *
     * @throws JAMBehaviourInterruptedException
     */
    public void action() throws JAMBehaviourInterruptedException {
        AgenteCliente me = (AgenteCliente) myAgent;
        // se la mia offerta massima disponibile è 0, termino
        if (miaOffertaMax == 0) {
            System.out.println("Non posso fare nessuna offerta: la mia offerta massima disponibile e' pari a 0.");
            rinunciato = true;
            done();
        } else {
            assert miaOffertaMax > 0;
            try {
                // chiedo il valore dell'offerta corrente
                System.out.println("\nChiedo il valore dell'offerta corrente.");
                Message query = new Message(me.getMyID(), idBanditore, Performative.QUERY_IF, "Valore corrente?");
                me.send(query);
                System.out.println(query);
                Message answer = me.receive(idBanditore);
                System.out.println("Ricevuta risposta dal banditore:");
                System.out.println(answer);
                // se l'oggetto è già stato aggiudicato, termino
                if (answer.getPerformative() == Performative.REFUSE) {
                    System.out.println("Rinuncio: l'asta e' terminata.");
                    // se non sono il vincitore, rinuncio
                    if (!answer.getContent().equals(me.getMyID().getName())) {
                        rinunciato = true;
                    }
                    done();
                } // se l'oggetto non è stato ancora aggiudicato, provo a fare un'offerta
                else if (answer.getPerformative() == Performative.INFORM) {
                    try {
                        // spacchetto il content della answer in: valore dell'offerta corrente + nome del miglior offerente
                        Scanner s = new Scanner((String) answer.getContent());
                        int offertaCorrente = Integer.parseInt(s.nextLine());
                        String nomeMigliorOfferente = s.nextLine();
                        // se sono io il miglior offerente non faccio niente
                        if (me.getMyID().getName().equals(nomeMigliorOfferente)) {
                            System.out.println("Sono ancora io il miglior offerente! :-)");
                            sleep(5000);
                        } // se l'offerta corrente supera la mia offerta massima, rinuncio
                        else if (miaOffertaMax <= offertaCorrente) {
                            System.out.println("Rinuncio: non mi posso permettere l'oggetto all'asta (valore corrente: " + offertaCorrente + " euro; mia offerta max: " + miaOffertaMax + ").");
                            rinunciato = true;
                            done();
                        } // faccio un'offerta
                        else {
                            System.out.println("Faccio un'offerta.");
                            Random r = new Random();
                            int rilancio = r.nextInt(miaOffertaMax - offertaCorrente) + 1; // il +1 garantisce un rilancio minimo di 1 euro
                            miaOffertaCorrente = miaOffertaCorrente + rilancio;
                            System.out.println("Offro " + miaOffertaCorrente + " euro.");
                            Message miaOfferta = new Message(me.getMyID(), idBanditore, Performative.REQUEST, String.valueOf(miaOffertaCorrente));
                            me.send(miaOfferta);
                            answer = myAgent.receive(idBanditore);
                            if (answer.getPerformative() == Performative.REFUSE) {
                                System.out.println("La mia offerta di " + miaOffertaCorrente + " euro non e' stata accettata.");
                            } else if (answer.getPerformative() == Performative.INFORM) {
                                System.out.println("La mia offerta di " + miaOffertaCorrente + " euro e' stata accettata. Al momento sono il miglior offerente! :)");
                                sleep(5000);
                            } else {
                                System.out.println("Risposta del banditore non riconosciuta.");
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Risposta del banditore non riconosciuta (formato del content non valido).");
                    }
                } else {
                    System.out.println("Risposta del banditore non riconosciuta (performativa sconosciuta).");
                }
            } catch (JAMADSLException ex) {
                System.err.println(ex.getMessage());
                done();
            } catch (JAMRemoteMessageBoxException ex) {
                System.err.println(ex.getMessage());
                done();
            } catch (InterruptedException e) {
                throw new JAMBehaviourInterruptedException(e.getMessage(), e);
            }
        }
    }

    public int getOffertaMax() {
        return miaOffertaMax;
    }

    public PersonalAgentID getIdBanditore() {
        return idBanditore;
    }

    public void setIdBanditore(PersonalAgentID idBanditore) {
        this.idBanditore = idBanditore;
    }
}
