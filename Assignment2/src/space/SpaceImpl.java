package space;

import api.Space;
import api.Task;
import api.Result;
import api.Computer;
import computer.ComputerImpl;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SpaceImpl extends UnicastRemoteObject implements Space{
    private BlockingQueue<Task> task;
    private BlockingQueue<Result> result;

    public SpaceImpl() throws RemoteException{
        task = new LinkedBlockingQueue<Task>();
        result = new LinkedBlockingQueue<Result>();
    }

    public void putAll(List<Task> taskList) throws RemoteException, InterruptedException{
        for(Task t : taskList){
            task.put(t);
        }
    }

    public Result take()throws RemoteException, InterruptedException{
        return result.take();
    }

    public void register(Computer computer) throws RemoteException{
        ComputerProxy c = new ComputerProxy(computer);
        new Thread(c).start();
        System.out.println("Computer is registered");
    }

    public static void main(String[] args){
        if(System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());
        try{
            Registry registry = LocateRegistry.createRegistry(Space.PORT);
            Space space = new SpaceImpl();
            registry.rebind(Space.SERVICE_NAME, space);
            System.out.println("Space start");
        } catch (Exception e){
            System.err.println("Computer exception:");
            e.printStackTrace();
        }


    }

    private class ComputerProxy implements Runnable{
        private Computer computer;
        public ComputerProxy(Computer computer){
            this.computer = computer;
        }

        @Override
        public void run(){
            try{
                while(true){
                    Task t = null;
                    Result r = null;
                    try{
                        t = task.take();
                        r = new Result(computer.Execute(t), -1);
                    }
                    catch (RemoteException e){
                        task.put(t);
                        System.out.println("This computer is dead!!!");
                        return;
                    }
                    try{
                        result.put(r);
                    }
                    catch(Exception ignore){}
                }
            }
            catch (InterruptedException ignore) {}
        }
    }
}
