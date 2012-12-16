package jam;

/**
 * Rappresenta l'ID di una certa categoria di agenti. 
 * E' caratterizzato solamente da una categoria, non dal nome.
 *
 * @author st064481
 */
public class CategoryAgentID extends GenericAgentID {

    private String category;

    public CategoryAgentID(String category) {
        super();
        if (category == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    /**
     *
     * @param agentID l'AgentID da confrontare con questo
     * @return true se e solo se agentID ha medesima categoria di questo oggetto; false altrimenti
     */
    public boolean equals(AgentID agentID) {
        if (agentID == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        return getCategory().equals(agentID.getCategory());
    }

    /**
     *
     * @param agentID l'AgentID da confrontare con questo
     * @return true se e solo se agentID ha medesima categoria di questo oggetto; false altrimenti
     * @throws IllegalArgumentException se agentID non è di tipo AgentID
     */
    public boolean equals(Object agentID) {
        try {
            return equals((AgentID) agentID);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Il parametro agentID deve avere tipo AgentID.");
        }
    }

    public String toString() {
        return "( , " + getCategory() + ")";
    }
}
