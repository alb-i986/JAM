package asta;

import jam.*;
import jam.eccezioni.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Mediante questa GUI e' possibile inizializzare, avviare e terminare l'agente
 * banditore.
 * <br>
 * Quando l'agente viene avviato, parte l'asta, che dura 1 minuto.
 * Scaduto il minuto, l'agente continua a rispondere alle offerte con
 * messaggi di tipo REFUSE.
 * <br>
 * Solo quando l'agente viene terminato, in seguito al click da parte dell'utente
 * sul bottone "Stop", viene inviato l'esito dell'asta ad ogni cliente che vi ha
 * partecipato.
 *
 * @author st064481
 */
public class BanditoreGUI {

    public static void main(String args[]) throws JAMRemoteMessageBoxException {
        int prezzoBase=1;
        int durataAstaMinuti = 1;
        String nomeBanditore="ebay";
        System.out.println("Faccio partire la GUI del banditore con i seguenti parametri:");
        System.out.println("Prezzo di partenza: "+prezzoBase);
        System.out.println("Durata dell'asta: "+durataAstaMinuti+" minuti");
        System.out.println("Nome dell'agente: "+nomeBanditore);
        AgenteBanditore banditore = new AgenteBanditore(prezzoBase, durataAstaMinuti, new BanditoreAgentID(nomeBanditore));
        new JAMAgentMonitor(banditore);
    }
}
