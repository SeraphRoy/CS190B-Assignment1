package system;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.List;
import api.Task;

public interface Computer extends Remote{

    public void Execute(Task task) throws RemoteException;

    public void Execute(List<Task> tasks) throws RemoteException;

    public void exit() throws RemoteException;

    public Share getShare();

    public void updateShare(Share share);

    public void setShare(Share share);
}
