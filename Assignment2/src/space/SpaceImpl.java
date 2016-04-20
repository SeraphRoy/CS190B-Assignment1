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
        new Thread(c).start();
    }

    public void main(String[] args){
        task = new LinkedBlockingQueue<Task>();
        result = new LinkedBlockingQueue<Result>();

        if(System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());
        try{
            Registry registry = LocateRegistry.createRegistry(Integer.parseInt(Space.PORT));
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
                        Logger.getLogger(SpaceImpl.class.getCanonicalName())
                            .log(Level.INFO, "Task {0}Task time: {1} ms.", new Object[]{t, taskRunTime});
                        Result<T> r = new Result<T>(value, taskRunTime);
                        result.put(r);
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
