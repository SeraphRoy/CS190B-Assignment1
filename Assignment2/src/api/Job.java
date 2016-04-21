package api;

import java.rmi.RemoteException;

public interface Job<T>{
    public void generateTasks(Space space) throws RemoteException, InterruptedException;
    public T collectResults(Space space) throws RemoteException, InterruptedException;
}
