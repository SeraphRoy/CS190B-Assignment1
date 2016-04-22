package computer;

import api.Computer;
import api.Space;
import api.Task;
import api.Result;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.Naming;
import java.util.concurrent.BlockingQueue;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ComputerImpl extends UnicastRemoteObject implements Computer{

    public ComputerImpl() throws RemoteException{};

    public <T> T Execute(Task<T> task) throws RemoteException{
        final long taskStartTime = System.nanoTime();
        T t = task.call();
        final long taskRunTime = ( System.nanoTime() - taskStartTime ) / 1000000;
        Logger.getLogger( ComputerImpl.class.getCanonicalName() )
            .log( Level.INFO, "Computer Side: Task {0}Task time: {1} ms.", new Object[]{ task, taskRunTime } );
        return t;
    }

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException{
        String domainName = "localhost";
        System.setSecurityManager( new SecurityManager() );
        String url = "rmi://" + domainName + ":" + Space.PORT + "/" + Space.SERVICE_NAME;

        Space space = (Space) Naming.lookup(url);
        ComputerImpl computer = new ComputerImpl();
        space.register(computer);
    }
}
