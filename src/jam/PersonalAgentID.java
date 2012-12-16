package jam;

/**
 * Rappresenta l'ID di uno specifico agente, caratterizzato da una categoria e da un nome.
 *
 * @author st064481
 */
public class PersonalAgentID extends CategoryAgentID {

    private String name;

    public PersonalAgentID(String name, String category) {
        super(category);
        if (category == null || name == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * @param agentID l'AgentID da confrontare con questo
     * @return true se e solo se agentID ha medesima categoria e medesimo nome di questo oggetto; false altrimenti
     */
    public boolean equals(AgentID agentID) {
        if (agentID == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        return getName().equals(agentID.getName()) && getCategory().equals(agentID.getCategory());
    }

    /**
     * @param agentID l'AgentID da confrontare con questo
     * @return true se e solo se agentID ha medesima categoria e medesimo nome di questo oggetto; false altrimenti
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
        return "(" + getName() + ", " + getCategory() + ")";
    }
}
