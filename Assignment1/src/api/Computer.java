package api;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Computer extends Remote{
    public static String SERVICE_NAME = "ComputerService";
    public static String PORT = "1078";

    public <T> T Execute(Task<T> t) throws RemoteException;
}
