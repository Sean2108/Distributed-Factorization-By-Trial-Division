/**
 * @author Sean Tan
 */

import java.rmi.*;
import java.math.BigInteger;

/**
 * file to run for server. reads in the number to factor as BigInteger
 * and passes it to FactorImpl. binds the FactorImpl to the registry
 */
public class FactorServer {
    public static void main(String[] args) {
        System.setSecurityManager(new RMISecurityManager());
        try {
            System.out.println("Clients can now connect to this server");
            BigInteger x = new BigInteger(args[0]);
            int numPackages = Integer.parseInt(args[1]);
            System.out.println("Number to factorize: " + x  + ", number of packages: " + numPackages);
            FactorImpl fi = new FactorImpl(x, numPackages);
            Naming.rebind(Factor.SERVICENAME, fi);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
