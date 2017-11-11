/**
 * @author Sean Tan
 */

import java.io.*;
import java.rmi.Naming;
import java.rmi.*;
import java.rmi.server.*;
import java.math.BigInteger;

/**
 * interface for the client program
 */

public interface FactorClient extends Remote {
    public long computeFactor(Package p, BigInteger n) throws RemoteException;
    public void terminate() throws RemoteException;
    public int keepAlive() throws RemoteException;
}
