package asta;

import jam.*;
import jam.eccezioni.*;
import java.util.Scanner;

/**
 * Mediante questa GUI e' possibile inizializzare, avviare e terminare l'agente
 * cliente.
 * <br>
 * Quando viene avviato, l'agente continua a fare offerte al banditore, finche'
 * ha disponibilita' economica, e finche' l'asta e' aperta.
 * <br>
 * Se rinuncia a fare offerte, ad es. perche' l'asta e' terminata, rimane in attesa
 * della comunicazione da parte del banditore riguardo l'esito dell'asta.
 *
 * @author st064481
 */
public class ClienteGUI {

    public static void main(String args[]) throws JAMRemoteMessageBoxException {
        Scanner s = new Scanner(System.in);
        System.out.println("Inserisci il nome dell'agente cliente.");
        String nomeCliente = s.nextLine();
        int portafoglio=100;
        String nomeBanditore="ebay";
        System.out.println("Faccio partire la GUI del cliente con i seguenti parametri:");
        System.out.println("Portafoglio: "+portafoglio);
        System.out.println("Nome di questo agente cliente: "+nomeCliente);
        System.out.println("Nome dell'agente banditore: "+nomeBanditore);
        AgenteCliente clt = new AgenteCliente(portafoglio, new ClienteAgentID(nomeCliente), new BanditoreAgentID(nomeBanditore));
        new JAMAgentMonitor(clt);
    }
}
