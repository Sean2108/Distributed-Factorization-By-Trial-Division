/**
 * @author Sean Tan
 */

import java.io.*;
import java.math.BigInteger;
import java.rmi.Naming;
import java.rmi.*;
import java.rmi.server.*;

/**
 * implements FactorClient interface.
 */
public class FactorClientImpl extends UnicastRemoteObject implements FactorClient {

	/**
	 * empty constructor needed for the remote
	 */
    public FactorClientImpl() throws RemoteException {
    }

    /**
     * main will call the compute method
     */
    public static void main(String[] args) throws IOException, NotBoundException {
        new FactorClientImpl().compute();
    }

    /**
     * looks for the server and connects to it
     */
    public void compute() throws IOException, NotBoundException {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
        System.out.println("Client starting");
        System.out.println("Finding server");
        Factor server = (Factor) Naming.lookup(Factor.SERVICENAME);
        System.out.println("Connecting to server");
        try {
            server.connect(this);
        } catch (Exception e) {
            System.out.println("Found number, closing client...");
            System.exit(0);
        }
    }

    /**
     * given a package p and long number n, searches within the package 
     * for a factor of n that is not a multiple of 2, 3 or 5
     */
    public long computeFactor(Package p, BigInteger n) {
        System.out.println("checking from " + p.start + " to " + p.end);
        for (long i = p.start; i <= p.end; i++) {
            if (i % 2 == 0 || i % 3 == 0 || i % 5 == 0) continue;
            if (n.mod(BigInteger.valueOf(i)).equals(BigInteger.ZERO)) {
            	System.out.println("FOUND FACTOR " + i);
            	return i;
            }
        }
        return -1;
    }
    
    public void terminate() {
        System.exit(0);
    }
    
    /**
     * returns a 0 to show that it is still connected. If this method does
     * not respond when server calls it, it will remove the client after a duration.
     */
    public int keepAlive() {
        return 0;
    }
}
