package api;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import system.*;

/**
 *
 * @author Peter Cappello
 */
public interface Space extends Remote
{
    public static int PORT = 8001;
    public static String SERVICE_NAME = "Space";

    public void register( Computer computer ) throws RemoteException;

    public <T> void putReady(Task<T> task) throws RemoteException;

    public <T> void putWaiting(Task<T> task) throws RemoteException;

    public <T> void sendArgument(Continuation cont, T result) throws RemoteException;

    public <T> Task<T> takeReady() throws RemoteException;
}
