package system;

import java.rmi.Remote;
import java.rmi.RemoteException;
import api.Task;

public interface Computer extends Remote{
    public Object Execute(Task task) throws RemoteException;

    public void exit() throws RemoteException;
}
