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
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

public class SpaceImpl extends UnicastRemoteObject implements Space{
    private static int computerIds = 0;
    private BlockingQueue<Closure> readyClosure;
    private ConcurrentHashMap<Long, Closure> waitingClosure;
    private LinkedBlockingQueue<Object> resultQueue;
    private final Map<Computer,ComputerProxy> computerProxies = Collections.synchronizedMap( new HashMap<>() );
    protected final BlockingDeque<SpawnResult> spawnResultStack = new LinkedBlockingDeque<>();

    public SpaceImpl() throws RemoteException{
        readyClosure = new LinkedBlockingQueue<Closure>();
        waitingClosure = new ConcurrentHashMap<>();
        resultQueue = new LinkedBlockingQueue<Object>();
    }

    // task's argumentList IS already initialized
    public void sendArgument(Continuation cont, Object result) throws RemoteException, InterruptedException{

        Closure closure = waitingClosure.get(cont.getClosureId());
        if(closure == null){
            this.resultQueue.put(result);
            return;
        }

        Argument argument = new Argument(result, cont.getSlot());
        closure.addArgument(argument);
        if(closure.getCounter() == 0){
            readyClosure.put(closure);
            waitingClosure.remove(cont.getClosureId());
        }
    }

    public void pushSpawnResult(SpawnResult result) throws RemoteException, InterruptedException{
        spawnResultStack.putFirst(result);
    }

    public SpawnResult popSpawnResult() throws RemoteException, InterruptedException
    {return spawnResultStack.takeFirst();}

    // task's argumentList is ready
    public void putReady(Task task) throws RemoteException, InterruptedException{
        Closure closure = new Closure(task.getArgc(), task);
        readyClosure.put(closure);
    }

    // task's argumentList IS empty
    public void putWaiting(Task task) throws RemoteException, InterruptedException{
        Closure closure = new Closure(task.getArgc(), task);
        waitingClosure.put(closure.getClosureId(), closure);
    }

    public void register(Computer computer) throws RemoteException, InterruptedException{
        ComputerProxy c = new ComputerProxy(computer);
        computerProxies.put( computer, c);
        new Thread(c).start();
        System.out.println("Computer #" + c.computerId + " is registered");
    }

    public Task takeReady() throws RemoteException, InterruptedException{
        return readyClosure.take().getTask();
    }

    public Object getResult() throws RemoteException, InterruptedException{return resultQueue.take();}

    @Override
    public void exit() throws RemoteException{
        computerProxies.values().forEach( proxy -> proxy.exit() );
        //System.exit( 0 );
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
                    try{
                        t = SpaceImpl.this.takeReady();
                        computer.Execute(t);
                        if(SpaceImpl.this.spawnResultStack.size() != 0){
                            SpawnResult result = SpaceImpl.this.popSpawnResult();
                            SpaceImpl.this.putWaiting(result.successor);
                            for(int i = 0; i < result.subTasks.size(); i++){
                                Continuation cont = Task.generateCont(i, result.successor);
                                result.subTasks.get(i).setCont(cont);
                                SpaceImpl.this.putReady(result.subTasks.get(i));
                            }
                        }
                    }
                    catch (RemoteException e){
                        try{
                            putReady(t);
                            computerProxies.remove(computer);
                        }
                        catch(RemoteException a){
                            System.err.println("ERROR!!");

                        }
                        System.out.println("Computer #" + computerId + " is dead!!!");
                        return;
                    }
                }
            }
            catch (InterruptedException ignore) {}
        }

        public void exit() { try { computer.exit(); } catch ( RemoteException ignore ) {} }

    }
}
