package system;
import api.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class SpaceImpl extends UnicastRemoteObject implements Space{
    private static int computerIds = 0;
    private BlockingQueue<Closure> readyClosure;
    private ConcurrentHashMap<int, Closure> waitingClosures;

    public SpaceImpl() throws RemoteException{
        readyClosure = new LinkedBlockingQueue<Closure>();
        waitingClosures = new ConcurrentHashMap();
    }

    // task's argumentList IS already initialized
    public void sendArgument(Continuation cont, T result) throws RemoteException{
        Closure closure = waitingClosures.get(cont.getClosureId());
        Argument argument = new Argument(result, con.getSlot());
        closure.addArgument(argument);
    }

    // task's argumentList is ready
    public void putReady(Task<T> task) throws RemoteException{
        Closure closure = new Closure(task.getArgc(), task, task.getArgumentList());
        readyClosure.put(closure);
    }

    // task's argumentList IS empty
    public void putWaiting(Task<T> task) throws RemoteException{
        Closure closure = new Closure(task.getArgc(), task, task.getArgumentList());
        waitingClosure.put(closure.getClosureId, closure);
    }

    public void register(Computer computer) throws RemoteException{
        ComputerProxy c = new ComputerProxy(computer);
        new Thread(c).start();
        System.out.println("Computer #" + c.computerId + " is registered");
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

        final protected int computerId = computerIds++;

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
                        t = takeReady();
                        r = computer.Execute(t);
                    }
                    catch (RemoteException e){
                        putReady(t);
                        System.out.println("Computer #" + computerId + " is dead!!!");
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
