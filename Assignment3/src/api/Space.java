package api;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import computer.ComputerImpl;

/**
 *
 * @author Peter Cappello
 */
public interface Space extends Remote
{
    public static int PORT = 8001;
    public static String SERVICE_NAME = "Space";

    public void register( Computer computer ) throws RemoteException;

    public void putReady(Task<T> task) throws RemoteException;

    public void putWaiting(Task<T> task) throws RemoteException;

    public void sendArgument(Continuation cont, T result) throws RemoteException;

    public Task<T> takeReady() throws RemoteException;
}
