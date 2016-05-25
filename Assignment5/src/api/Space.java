package api;

import system.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.lang.InterruptedException;
/**
 *
 * @author Peter Cappello
 */
public interface Space extends Remote
{
    public static int PORT = 8001;
    public static String SERVICE_NAME = "Space";

    public void register( Computer computer ) throws RemoteException, InterruptedException;

    public void putDoneTask(Task task) throws RemoteException, InterruptedException;

    public void putReady(Task task) throws RemoteException, InterruptedException;

    public void putReady(List<Task> tasks) throws RemoteException, InterruptedException;

    public void putWaiting(Task task) throws RemoteException, InterruptedException;

    public void sendArgument(Continuation cont, Object result) throws RemoteException, InterruptedException;

    public Task takeReady() throws RemoteException, InterruptedException;

    public Object getResult() throws RemoteException, InterruptedException;

    public void exit() throws RemoteException;

    public void putSpawnResult(SpawnResult result) throws RemoteException, InterruptedException;

    public SpawnResult getSpawnResult() throws RemoteException, InterruptedException;

}
