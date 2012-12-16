package jam;

import java.io.Serializable;

/**
 * Rappresenta un messaggio che due o più agenti si possono scambiare tra di loro per comunicare.
 * <p>
 * Ogni messaggio è caratterizzato da:
 * <ul>
 * <li>un mittente, che è unico (un agente)
 * <li>un destinatario, che può comprendere diversi agenti
 * <li>un contenuto
 * <li>una performativa, che specifica il tipo di messaggio (es.: richiesta vs. risposta)
 * </ul>
 * <p>
 * Per una questione di sicurezza, non è possibile istanziare un Message "vuoto".
 *
 * @author st064481
 */
public class Message implements Serializable {

    private PersonalAgentID sender;     // il mittente del messaggio (può essere uno solo, perciò è dichiarato come Personal)
    private AgentID receiver;           // il destinatario del messaggio
    private Performative performative;  // specifica la performativa utilizzata
    private String content;             // memorizza il contenuto del messaggio
    private Object extraArgument;       // permette di memorizzare eventuali ulteriori informazioni in forma di oggetto


//    Bisogna permettere la creazione di un msg vuoto? Se sì, ci vuole il costruttore vuoto
//    Io direi di no, come regola di sicurezza: così chi crea il Message non si può dimenticare di inserire qualche informazione obbligatoria.
//    public Message() {
//    }

    public Message(PersonalAgentID sender, AgentID receiver, Performative performative, String content) {
        if (sender == null || receiver == null || performative == null || content == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        this.sender = sender;
        this.receiver = receiver;
        this.performative = performative;
        this.content = content;
    }

    public Message(PersonalAgentID sender, AgentID receiver, Performative performative, String content, Object extraArgument) {
        if (sender == null || receiver == null || performative == null || content == null || extraArgument == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        this.sender = sender;
        this.receiver = receiver;
        this.performative = performative;
        this.content = content;
        this.extraArgument = extraArgument;
    }

    public void setSender(PersonalAgentID s) {
        if (s == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        sender = s;
    }

    public PersonalAgentID getSender() {
        return sender;
    }

    public void setReceiver(AgentID r) {
        if (r == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        receiver = r;
    }

    public AgentID getReceiver() {
        return receiver;
    }

    public Performative getPerformative() {
        return performative;
    }

    public void setPerfomative(Performative p) {
        if (p == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        performative = p;
    }

    public void setContent(String c) {
        if (c == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        content = c;
    }

    public String getContent() {
        return content;
    }

    public void setExtraArgument(Object e) {
        if (e == null) {
            throw new IllegalArgumentException("Tutti i parametri sono obbligatori e devono essere diversi da null.");
        }
        extraArgument = e;
    }

    public Object getExtraArgument() {
        return extraArgument;
    }

    public String toString() {
        String s = "\tPerformativa: " + getPerformative() + "\n" +
                "\tMittente: " + sender + "\n" +
                "\tDestinatario: " + receiver + "\n" +
                "\tContenuto:\n\t" + getContent() + "\n";
        if (getExtraArgument() != null) {
            s += "\tArgomento Extra:\n\t" + getExtraArgument() + "\n";
        }
        return s;
    }
}
