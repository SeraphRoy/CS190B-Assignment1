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

    void register( Computer computer ) throws RemoteException;

    void putReady(Task<T> task) throws RemoteException;

    void putWaiting(Task<T> task) throws RemoteException;
}
