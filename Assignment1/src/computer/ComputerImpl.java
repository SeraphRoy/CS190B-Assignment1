package computer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import api.Computer;
import api.Task;

public class ComputerImpl extends UnicastRemoteObject implements Computer{
    public ComputerImpl() throws RemoteException{}

    @Override
    public <T> T Execute(Task<T> t){
        return t.Execute();
    }

    public static void main(String[] args){
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());
        try{
            Computer c = new ComputerImpl();
            Registry registry = LocateRegistry.createRegistry(Integer.parseInt(Computer.PORT));
            registry.rebind(Computer.SERVICE_NAME, c);
            System.out.println("Computer start");
        } catch (Exception e){
            System.err.println("Computer exception:");
            e.printStackTrace();
        }
    }
}
