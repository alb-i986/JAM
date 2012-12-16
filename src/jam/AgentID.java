package jam;

import java.io.Serializable;

/**
 * Rappresenta un ID di agente, caratterizzato da un nome e da una categoria (eventualmente vuoti).
 *
 * @author st064481
 */
public interface AgentID extends Serializable {

    /**
     * Confronta due ID agente.
     *
     * @param agentID l'agente da confrontare
     * @return true se questo agente ha le medesime caratteristiche di agentID; false altrimenti
     */
    public boolean equals(AgentID agentID);

    /**
     * Confronta due ID agente.
     * @param agentID l'agente da confrontare
     * @return true se questo agente ha le medesime caratteristiche di agentID; false altrimenti
     * @throws IllegalArgumentException se agentID non è di tipo AgentID
     */
    @Override
    public boolean equals(Object agentID);

    public String getName();

    public String getCategory();

    public String toString();
}
