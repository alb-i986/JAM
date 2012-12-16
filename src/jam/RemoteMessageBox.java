/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jam;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * L'interfaccia che descrive una message box <i>remota</i>.
 *
 * @author tosco
 */
public interface RemoteMessageBox extends Remote {

    /**
     * Inserisce il messaggio passato come parametro in coda alla casella di messaggi.
     * 
     * @param message il messaggio da inserire in coda
     * @throws InterruptedException se il thread va in wait sulla writeMessage e viene interrotto da un interrupt
     * @throws RemoteException
     */
    public void writeMessage(Message message) throws RemoteException, InterruptedException;

    /**
     * @return il PersonalAgentID corrispondente al proprietario di questa message box
     * @throws RemoteException
     */
    public PersonalAgentID getOwner() throws RemoteException;

    /**
     * toString()
     * @throws RemoteException 
     */
    //public String printMessageBox() throws RemoteException;
    //public String toString();
    public void print() throws RemoteException;
}
