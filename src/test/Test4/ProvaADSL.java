/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Test4;

import jam.ADSLImpl;
import java.rmi.Naming;

/**
 *
 * @author tosco
 */
public class ProvaADSL {

    public static void main(String[] args) {
        ADSLImpl adsl = null;
        try {
            adsl = new ADSLImpl();
            adsl.startRMIRegistry();
            System.out.println("avviato rmi registry");
            adsl.start();
            System.out.println("registrato oggetto ADSL nell' rmi registry");
            System.out.println("In attesa di invocazioni dai client");

            String[] bindings = Naming.list("//localhost:"+adsl.getPort());
            System.out.println("stampo lista bindings nel rmiregistry");
            for (String b : bindings) {
                System.out.println(b);
            }
            System.out.println();
        } catch (Exception e) {
            System.err.println("Failed to bind to RMI Registry ");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
