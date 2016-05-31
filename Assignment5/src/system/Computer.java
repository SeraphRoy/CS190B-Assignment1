package system;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.List;
import api.*;

public interface Computer extends Remote{

    public ResultWrapper Execute(Task task) throws RemoteException;

    //public void Execute(List<Task> tasks, Space space) throws RemoteException;

    public void exit() throws RemoteException;

    public Share getShare() throws RemoteException;

    public void updateShare(Share share) throws RemoteException;

    public void setShare(Share share) throws RemoteException;
}
