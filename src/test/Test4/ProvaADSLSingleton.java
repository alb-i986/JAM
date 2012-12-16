/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Test4;

import jam.ADSLImplSingleton;
import java.rmi.Naming;

/**
 *
 * @author tosco
 */
public class ProvaADSLSingleton {

    public static void main(String[] args) {
        ADSLImplSingleton adsl = null;
        try {
            adsl = ADSLImplSingleton.getInstance();
// l a r i g a s eguent e e ' in a l t e r n a t i v a con l ' a t t i v a z i o n e
// a prompt de l rmi r e g i s t r y
            adsl.startRMIRegistry();
            System.out.println("avviato rmi registry");
            adsl.start();
            System.out.println("registrato oggetto ADSL nell' rmi registry");
            System.out.println("In attesa di invocazioni dai client");

            String[] bindings = Naming.list("//localhost:1099");
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
