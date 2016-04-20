package space;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import computer.ComputerImpl;
import java.rmi.server.UnicastRemoteObject;
import api.Space;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import computer.ComputerImpl;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.concurrent.BlockingQueue;

public class SpaceImpl extends UnicastRemoteObject implements Space{
    private BlockingQueue<Task> task;
    private BlockingQueue<Result> result;

    public void putAll(List<Task> taskList){
        for(Task t : taskList){
            task.put(t);
        }
    }

    public Result take(){
        return result.take();
    }

    public void register(Computer computer){
        ComputerProxy c = new ComputerProxy(computer);

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

    public void main(String[] args){
        this.register
    }

    private class ComputerProxy implements Runnable{
        private Computer computer;
        private Task<T> t;
        public ComputerProxy(Computer computer){
            this.computer = computer;
        }

        @Override
        public void run(){
            try{
                while(true){
                    t = task.take();
                    try{
                        final long taskStartTime = System.nanoTime();
                        final T value = computer.Execute(t);
                        final long taskRunTime = (System.nanoTime() - taskStartTime) / 1000000;
                        Logger.getLogger(SpaceImpl.class.getCannonicalName())
                            .log(Level.INFO, "Task {0}Task time: {1} ms.", new Object[]{t, taskRunTime});
                    }
                    catch (RemoteException e){
                        task.put(t);
                        return;
                    }
                }
            }
            catch (InterruptedException ignore) {}
        }
    }
}
