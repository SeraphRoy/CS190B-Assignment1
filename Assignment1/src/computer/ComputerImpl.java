package computer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import api.Computer;
import api.Task;

public class ComputerImpl extends UnicastRemoteObject implements Computer{
    ComputerImpl() throws RemoteException;

    @Override
    <T> T Execute(Task<T> t){
        return t.Execute();
    }

    public static void main(String[] args){
        if (System.getSecurityManger() == null)
            System.setSecurityManger(new SecurityManger());
        try{
            Computer c = new ComputerImpl();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind(Computer.SERVICE_NAME, c);
            System.out.println("Computer bound");
        } catch (Exception e){
            System.err.println("Computer exception:");
            e.printStackTrace();
        }
    }
}
