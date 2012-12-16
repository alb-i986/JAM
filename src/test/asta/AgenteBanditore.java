/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package asta;

import jam.eccezioni.*;
import jam.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Il banditore è un agente che bandisce un'asta in cui si vende un oggetto.
 * Un'asta è caratterizzata da:
 * <ul>
 * <li>un prezzo di partenza
 * <li>una data di fine
 * <li>una serie di clienti partecipanti
 * <li>un vincitore (noto solo al termine dell'asta)
 * <li>un prezzo finale (noto solo al termine dell'asta)
 * </ul>
 * <p>
 * Ogni agente-banditore puo' avere uno e un solo comportamento, e deve necessariamente
 * essere di tipo <code>BanditoreBehaviour</code>.
 * <br>
 * Per eliminare un agente-banditore con <code>destroy()</code>, è necessario
 * che l'asta sia terminata.
 *
 * @author tosco
 */
public class AgenteBanditore extends JAMAgent {

    private final int baseAsta;   // il prezzo di partenza dell'oggetto
    private int offertaCorrente;   // il valore dell'offerta al momento raggiunto
    private PersonalAgentID migliorOfferente; // il cliente che si sta aggiudicando questa asta
    private List<PersonalAgentID> offerenti; // i clienti che stanno partecipando a questa asta
    private int durataAstaMinuti; // i minuti per cui si vuole che l'asta sia aperta
    private Calendar termineAsta; // la data in cui termina l'asta

    /**
     *
     * @param baseAsta il prezzo di partenza dell'oggetto messo all'asta
     * @param durataAstaMinuti i minuti per cui si vuole che l'asta sia aperta
     * @param agentID
     * @param adsl_ip
     * @param adsl_name
     * @param adsl_port
     *
     * @throws JAMRemoteMessageBoxException se la creazione della MessageBox
     *              (oggetto remoto) di questo agente non è andata a buon fine
     */
    public AgenteBanditore(int baseAsta, int durataAstaMinuti, PersonalAgentID agentID, String adsl_ip, String adsl_name, int adsl_port)
            throws JAMRemoteMessageBoxException {
        super(agentID, adsl_ip, adsl_name, adsl_port);
        this.offertaCorrente = this.baseAsta = baseAsta;
        this.durataAstaMinuti = durataAstaMinuti;
        termineAsta = new GregorianCalendar();
        offerenti = new LinkedList<PersonalAgentID>();
        addBehaviour(new GestisciAstaBehaviour(this));
    }

    /**
     *
     * @param baseAsta il prezzo di partenza dell'oggetto messo all'asta
     * @param durataAstaMinuti i minuti per cui si vuole che l'asta sia aperta
     * @param agentID
     *
     * @throws JAMRemoteMessageBoxException se la creazione della MessageBox
     *              (oggetto remoto) di questo agente non è andata a buon fine
     */
    public AgenteBanditore(int baseAsta, int durataAstaMinuti, PersonalAgentID agentID)
            throws JAMRemoteMessageBoxException {
        super(agentID);
        this.offertaCorrente = this.baseAsta = baseAsta;
        this.durataAstaMinuti = durataAstaMinuti;
        offerenti = new LinkedList<PersonalAgentID>();
        termineAsta = new GregorianCalendar();
        addBehaviour(new GestisciAstaBehaviour(this));
    }

    /**
     * Elimina questo agente solo se l'asta è già terminata.
     *
     * @throws IllegalStateException se l'asta non è ancora terminata
     * @throws JAMADSLException se il collegamento con l'ADSL non è andato a buon fine
     */
    @Override
    public void destroy() throws JAMADSLException {
        if (astaIsOpen()) { // se l'asta non è ancora terminata non si può terminare questo agente
            // calcono quanti minuti mancano al termine dell'asta
            GregorianCalendar now = new GregorianCalendar();
            Calendar rimanenza = (Calendar) termineAsta.clone();
            rimanenza.add(Calendar.MINUTE, -now.get(Calendar.MINUTE));
            int minutiRimanenti = rimanenza.get(Calendar.MINUTE);
            throw new IllegalStateException("Impossibile terminare l'agente banditore: mancano ancora " + minutiRimanenti + " minuti al termine dell'asta");
        } else { // se l'asta è già terminata è lecito terminare l'agente
            super.destroy();
            /* CODICE SBAGLIATO PRESENTE NELLA VERSIONE CONSEGNATA
            try {
            // attendo 2 secondi prima di resettare i campi di questo banditore
            // gli do un po' di tempo per terminare effettivamente i comportamenti
            Thread.sleep(2000);
            } catch (InterruptedException ex) {
            Logger.getLogger(AgenteBanditore.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            offerenti = new LinkedList<PersonalAgentID>(); // resetto la lista dei clienti partecipanti all'asta
            this.offertaCorrente = this.baseAsta;
            migliorOfferente = null;
             */
        }
    }

    /**
     * Associa un nuovo comportamento all'agente.<br>
     * Se si desidera che il comportamento aggiunto venga eseguito, occorre
     * invocare <code>start</code>.
     *
     * @param behaviour il nuovo comportamento da associare a questo agente
     *
     * @throws IllegalStateException se a questo agente è già associato un comportamento
     * @throws IllegalArgumentException se il comportamento da assocciare a questo agente
     *                                  non e' un comportamento di tipo BanditoreBehaviour
     */
    @Override
    public void addBehaviour(JAMBehaviour behaviour) {
        if (!(behaviour instanceof BanditoreBehaviour)) {
            throw new IllegalArgumentException("Ad un agente-banditore e' possibile associare solo comportamenti di tipo BanditoreBehaviour");
        }
        if (getNumBehaviours() == 1) {
            throw new IllegalStateException("Impossibile aggiungere un nuovo comportamento: ogni banditore puo' avere max 1 comportamento");
        } else {
            super.addBehaviour(behaviour);
        }
    }

    /**
     * Aggiunge un cliente alla lista di clienti che stanno partecipando
     * all'asta, solo se non è già presente.
     *
     * @param a il cliente da aggiungere alla lista di clienti che stanno
     *          partecipando all'asta
     */
    public void addCliente(PersonalAgentID a) {
        boolean alreadyPresent = false;
        for (PersonalAgentID offerente : offerenti) {
            if (offerente.equals(a)) {
                alreadyPresent = true;
            }
        }
        // aggiungo il nuovo cliente solo se non è già presente
        // nella lista di clienti partecipanti all'asta
        if (!alreadyPresent) {
            offerenti.add(a);
        }
    }

    /**
     * Comunica il risultato dell'asta ad ogni cliente che vi ha partecipato
     * con almeno un'offerta.
     * <br>
     * Al vincitore invia una INFORM con content "Sei il vincitore."
     * A tutti gli altri invia una INFORM con content "Non sei il vincitore."
     *
     * @throws JAMADSLException se il collegamento con l'ADSL non è andato a buon fine
     * @throws IllegalStateException se l'asta non è ancora terminata
     */
    public void sendResults() throws JAMADSLException {
        if (astaIsOpen()) {
            throw new IllegalStateException("Impossibile inviare il risultato dell'asta ad ogni partecipante: asta non ancora terminata.");
        }
        for (PersonalAgentID offerente : offerenti) {
            Message m = null;
            if (offerente.equals(migliorOfferente)) {
                System.out.println("> Comunico a " + offerente + " che e' il vincitore dell'asta.");
                m = new Message(getMyID(), offerente, Performative.INFORM, "Sei il vincitore.");
            } else {
                System.out.println("> Comunico a " + offerente + " che non e' il vincitore dell'asta.");
                m = new Message(getMyID(), offerente, Performative.INFORM, "Non sei il vincitore.");
            }
            try {
                send(m);
            } catch (InterruptedException ex) {
                System.err.println("Fallito l'invio dell'esito dell'asta a " + m.getReceiver());
            } catch (JAMRemoteMessageBoxException ex) {
                System.err.println("Fallito l'invio dell'esito dell'asta a " + m.getReceiver());
            }
        }
    }

    /**
     * Resetta l'asta, mantenendo però la stessa durata e lo stesso prezzo di
     * partenza stabiliti in fase di creazione.
     */
    public void resetAsta() {
        offerenti = new LinkedList<PersonalAgentID>(); // resetto la lista dei clienti partecipanti all'asta
        this.offertaCorrente = this.baseAsta;
        migliorOfferente = null;
    }

    /**
     * @return true se l'asta è ancora in corso;
     *              false altrimenti, ovvero se l'oggetto all'asta è già stato aggiudicato a qualcuno
     */
    public boolean astaIsOpen() {
        GregorianCalendar now = new GregorianCalendar();
        return termineAsta.after(now);
    }

    /**
     *
     * @return true se c'e' un vincitore, ovvero se all'asta ha partecipato almeno un Cliente; false altrimenti
     */
    public boolean nessunPartecipante() {
        return baseAsta == offertaCorrente;
    }

    public int getOffertaCorrente() {
        return offertaCorrente;
    }

    public void setOffertaCorrente(int maxOfferta) {
        this.offertaCorrente = maxOfferta;
    }

    public void setMigliorOfferente(PersonalAgentID migliorOfferente) {
        this.migliorOfferente = migliorOfferente;
    }

    public PersonalAgentID getMigliorOfferente() {
        return migliorOfferente;
    }

    public String getNomeMigliorOfferente() {
        if (migliorOfferente == null) { // non è stata fatta ancora nessuna offerta
            return "N/A";
        } else {
            return migliorOfferente.getName();
        }
    }

    /**
     * @return il numero di minuti per cui dura l'asta
     */
    public int getDurataAstaMinuti() {
        return durataAstaMinuti;
    }

    public void setTermineAsta(Calendar termineAsta) {
        this.termineAsta = termineAsta;
    }

    public static void main(String args[]) throws JAMADSLException {
        int minuti = 0;
        Scanner s = new Scanner(System.in);
        boolean inputOk = false;
        int i = 0;
        while (!inputOk) {
            try {
                System.out.println("Inserisci il numero di minuti che vuoi che duri l'asta");
                if (i > 0) {
                    s.nextLine();
                }
                minuti = s.nextInt();
                if (minuti <= 0) {
                    System.out.println("Devi inserire un numero intero positivo.");
                } else {
                    inputOk = true;
                }
            } catch (InputMismatchException ex) {
                System.out.println("Input errato: devi inserire un numero intero.");
            } finally {
                i++;
            }
        }
        AgenteBanditore banditore = null;
        try {
            banditore = new AgenteBanditore(1, minuti, new BanditoreAgentID("ebay"));
            banditore.init();
            banditore.start();
            // dormo durante lo svolgimento dell'asta,
            // e per il minuto successivo al termine dell'asta
            try {
                Thread.sleep(1000 * 60 * (minuti + 1));
            } catch (InterruptedException ex) {
                Logger.getLogger(AgenteBanditore.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (JAMRemoteMessageBoxException ex) {
            Logger.getLogger(AgenteBanditore.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // loop finché non riesco ad eliminare l'agente,
            // ovvero finche' l'asta non e' terminata
            boolean eliminato = false;
            while (!eliminato) {
                try {
                    banditore.destroy();
                    eliminato = true;
                    System.out.println("\nAgente-banditore terminato.\n");
                } catch (IllegalStateException ex) {
                    try {
                        // aspetto altri 5 sec e poi riprovo ad eliminare l'agente
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        Logger.getLogger(AgenteBanditore.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
            }
        }
    }
}
