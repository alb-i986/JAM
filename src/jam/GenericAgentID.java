/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jam;

/**
 * Rappresenta l'ID di un qualunque agente. Non è caratterizzato nè da un nome
 * nè da una categoria.
 *
 * @author st064481
 */
public class GenericAgentID implements AgentID {

    public GenericAgentID() {
    }

    /**
     * @return una stringa vuota
     */
    public String getName() {
        return "";
    }

    /**
     * @return una stringa vuota
     */
    public String getCategory() {
        return "";
    }

    /**
     *
     * @param agentID l'AgentID da confrontare con questo
     * @return true sempre
     */
    public boolean equals(AgentID agentID) {
        if (agentID == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        return true;
    }

    /**
     *
     * @param agentID l'AgentID da confrontare con questo
     * @return true sempre
     * @throws IllegalArgumentException se agentID non è di tipo AgentID
     */
    public boolean equals(Object agentID) {
        try {
            return equals((AgentID) agentID);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Il parametro agentID deve avere tipo AgentID.",e);
        }
    }

    public String toString() {
        return "( , )";
    }
}
