/**
 * interface class for Factor, provides the constant SERVICENAME for RMI lookup and the method to connect to it
 */

public interface Factor extends java.rmi.Remote {
    public final static String SERVICENAME="FactorService";
    public void connect(FactorClient d) throws java.rmi.RemoteException;
}
