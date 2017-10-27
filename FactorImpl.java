import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.*;
import java.time.Duration;
import java.math.BigInteger;

/**
 * @author Sean
 * implementation of factor interface.
 */
public class FactorImpl extends UnicastRemoteObject implements Factor, Runnable {
    
    ArrayList<FactorClient> list;
    Map<FactorClient, Package> clientPackage;
    long upperLimit;
    BigInteger numToFactor;
    int numPackages;
    Set<Package> notChecked;
    Set<Package> checking;
    Set<Package> checked;
    final Duration timeout = Duration.ofSeconds(5);
    ExecutorService executor;
    long startTime;
    boolean done = false;

    /**
     * no parameter constructor
     */
    public FactorImpl() throws RemoteException {
        super();
        list = new ArrayList<>();
        notChecked = new HashSet<>();
        checking = new HashSet<>();
        checked = new HashSet<>();
        clientPackage = new HashMap<>();
        executor = Executors.newSingleThreadExecutor();
        startTime = System.currentTimeMillis();
    }

    /**
     * initializes all attributes and splits parameter numToFactor into
     * numPackages packages.
     */
    public FactorImpl(BigInteger numToFactor, int numPackages) throws RemoteException {
        this();
        this.numPackages = numPackages;
        this.numToFactor = numToFactor;
        this.upperLimit = sqrt(numToFactor);
        long start = 2;
        long end = upperLimit / numPackages;
        for (int i = 0; i < numPackages; i++) {
            notChecked.add(new Package(start, end));
            start = end + 1;
            end += upperLimit / numPackages;
        }
        start();
    }
    
    /**
     * square roots a BigInteger and returns a long
     */
    private long sqrt(BigInteger x) {
        BigInteger div = BigInteger.ZERO.setBit(x.bitLength()/2);
        BigInteger div2 = div;
        // Loop until we hit the same value twice in a row, or wind
        // up alternating.
        for(;;) {
            BigInteger y = div.add(x.divide(div)).shiftRight(1);
            if (y.equals(div) || y.equals(div2))
                return y.longValue();
            div2 = div;
            div = y;
        }
    }

    /**
     * starts the thread and runs
     */
    public void start() {
        new Thread(this).start();
    }

    /**
     * main method to run. Gives each connected FactorClient a package and
     * the number to factor, and provides a new one if the FactorClient is
     * unable to find the factor within the previous package. Removes packages
     * from notChecked set and adds to the checking set when checking, and adds 
     * completed packages to the checked set.
     * 
     */
    public void connect(FactorClient d) throws RemoteException {
        if (notChecked.size() == 0) {
            System.out.println("No packages left to distribute!");
            return;
        }
        list.add(d);
        System.out.println("Adding client #" + (list.indexOf(d) + 1));
        while (!notChecked.isEmpty()) {
            Package next = notChecked.iterator().next();
            notChecked.remove(next);
            checking.add(next);
            clientPackage.put(d, next);
            long result = d.computeFactor(next, numToFactor);
            if (result == -1) {
                checking.remove(next);
                checked.add(next);
                clientPackage.remove(d);
            }
            else {
                close(d, result);
                System.exit(0);
            }
        }
    }

    /**
     * called when the program has found a factor. prints details about clients, run
     * time and search space. terminates all clients and ends the server.
     */
    private void close(FactorClient d, long result) throws RemoteException {
    	done = true;
        System.out.println("CLIENT #" + (list.indexOf(d) + 1) + " FOUND A PAIR OF FACTORS!");
        System.out.println(result + " * " + numToFactor.divide(BigInteger.valueOf(result)) + " = " + numToFactor);
        System.out.println("Number of clients used: " + list.size());
        System.out.println("Percentage of space examined: " + ((checked.size()) * 100 / numPackages));
        System.out.println("Time taken: " + ((new Date()).getTime() - startTime) / 1000 + " seconds");
    }
    
    /**
     * checks each client every 60s. If the client does not respond within
     * 5 seconds, the client is deemed to be down and will be removed from
     * list of clients.
     */
    public void run() {
        while (!done) {
            try { 
            	System.out.println("Keep-alive checker is sleeping for 60 seconds...");
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                System.out.println("Interrupted during sleep!");
            }
            Iterator it = list.iterator();
            while (it.hasNext()) {
                FactorClient current = (FactorClient) it.next();
                try {
                    Future<Integer> future = executor.submit(new Callable() {
                        @Override
                        public Integer call() throws Exception {
                            return current.keepAlive();
                        }
                    });
                    try {
                        future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
                        System.out.println("Client #" + (list.indexOf(current) + 1) + " is still checking...");
                    } catch (TimeoutException | InterruptedException e) {
                        removeClient(current);
                        future.cancel(true);
                        it.remove();
                        continue;
                    }
                } catch (Exception re) {
                    System.out.println("No response from client #" + (list.indexOf(current) + 1) + ", removing it.");
                    removeClient(current);
                    it.remove();
                    continue;
                }
            }
        }
    }

    /**
     * move incomplete package when a client disconnects back
     * to the unchecked set
     */
    private void removeClient(FactorClient current) {
        Package incomplete = clientPackage.get(current);
        checking.remove(incomplete);
        notChecked.add(incomplete);
        clientPackage.remove(current);
    }
}
