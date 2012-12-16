/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jam;

import jam.eccezioni.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * ADSL (Agent Directory Service Layer) è l'interfaccia che rappresenta l'oggetto
 * remoto che ha lo scopo di pubblicare i RemoteMessageBox degli agenti presenti
 * nella piattaforma in ogni istante.
 *
 * @author tosco
 */
public interface ADSL extends Remote {

    /**
     * Restituisce una lista di riferimenti ad oggetti (remoti) di tipo RemoteMessageBox
     * i cui proprietari sono uguali a agentID.
     * <p>
     * Nello specifico:
     * <ul>
     * <li>nel caso in cui agentID contenga un riferimento ad un oggetto di tipo PersonalAgentID
     * allora la lista sarà composta dal singolo riferimento al RemoteMessageBox dell'agente di ID specificato.
     * <li>nel caso contenga contenga un riferimento ad una istanza di CategoryAgentID allora la lista sarà
     * composta da tutti i riferimenti ai RemoteMessageBox i cui proprietari hanno stessa categoria
     * di quella specificata mediante il parametro agentID. 
     * <li>nel caso che agentID contenga un riferimento ad una istanza di GenericAgentID
     * allora la lista restituita contiene i riferimenti a tutti i RemoteMessageBox presenti in quel dato momento
     * </ul>
     *
     * @param agentID i(l) proprietari(o) dei(del) RemoteMessageBox di cui si vuole ottenere i(l) riferimenti(o)
     * @return una lista (eventualmente vuota) di RemoteMessageBox appartenenti agli agenti specificati in agentID
     * @throws RemoteException
     */
    public List<RemoteMessageBox> getRemoteMessageBox(AgentID agentID) throws RemoteException;

    /**
     * Richiede l'inserimento di messageBox presso l'ADSL.
     * Se l'elemento è già presente, non viene effettuata alcuna operazione e viene lanciata un'eccezione
     * 
     * @param messageBox la message box remota da registrare presso l'ADSL
     * @throws RemoteException 
     * @throws JAMADSLException se la casella non è stata inserita perché era già presente una casella dello stesso proprietario
     */
    public void insertRemoteMessageBox(RemoteMessageBox messageBox) throws RemoteException, JAMADSLException;

    /**
     * Richiede la cancellazione della MessageBox remota presente presso l'ADSL
     * di proprietà dell'agente agentID.
     * Se l'elemento non è presente non viene effettuata alcuna operazione e viene lanciata un'eccezione
     *
     * @param agentID il proprietario della casella da eliminare
     * @throws RemoteException
     * @throws JAMADSLException se non è stato possibile rimuovere la casella (o le casselle)
     *      perchè non è stata trovata nessuna casella che ha agentID come proprietario
     */
    public void removeRemoteMessageBox(AgentID agentID) throws RemoteException, JAMADSLException;
}
