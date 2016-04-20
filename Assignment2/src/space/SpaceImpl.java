package space;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import system.Computer;
import java.rmi.UnicastRemoteObject;
import api.Space;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

import java.util.concurrent.BlockingQueue;

public class SpaceImpl extends UnicastRemoteObject implements Space{
    private BlockingQueue<Task> task;
    private BlockingQueue<Result> result;

    void putAll(List<Task> taskList){
        for(Task t : taskList){
            task.put(t);
        }
    }

    Result take(){
        return result.take();
    }
    void register(Computer computer){
        if(System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());
        try{
            Registry registry = LocateRegistry.createRegistry(Integer.parseInt(Space.PORT));
            registry.rebind(Space.SERVICE_NAME, computer);
            System.out.println("Space start");
        } catch (Exception e){
            System.err.println("Computer exception:");
            e.printStackTrace();
        }
    }
}
