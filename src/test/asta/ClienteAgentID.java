package asta;

import jam.PersonalAgentID;

/**
 * L'ID di un agente cliente che partecipa ad un'asta.
 * @author st064481
 */
public class ClienteAgentID extends PersonalAgentID {

    public ClienteAgentID(String nome) {
        super(nome,"AstaCliente");
    }

}
