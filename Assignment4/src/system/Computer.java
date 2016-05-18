package system;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import api.Task;

public interface Computer extends Remote{

    public void Execute(Task task) throws RemoteException;

    public void exit() throws RemoteException;
}
