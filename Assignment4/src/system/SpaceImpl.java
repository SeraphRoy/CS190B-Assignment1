package system;
import api.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ArrayList;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.HashSet;

public class SpaceImpl extends UnicastRemoteObject implements Space{
    private static int computerIds = 0;
    protected BlockingQueue<Closure> readyClosure;
    private ConcurrentHashMap<Long, Closure> waitingClosure;
    private LinkedBlockingQueue<Object> resultQueue;
    private final Map<Computer,ComputerProxy> computerProxies = Collections.synchronizedMap( new HashMap<>() );
    private HashSet<Long> doneTasks;
    private BlockingQueue<SpawnResult> spawnResultQ;

    public static boolean MULTICORE = false;

    public static int preFetchNum = 1;

    public SpaceImpl() throws RemoteException{
        readyClosure = new LinkedBlockingQueue<Closure>();
        waitingClosure = new ConcurrentHashMap<>();
        resultQueue = new LinkedBlockingQueue<Object>();
        doneTasks = new HashSet<>();
        spawnResultQ = new LinkedBlockingQueue<>();
        new Thread(new SpawnResultHandler()).start();
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

    // task's argumentList is ready
    public void putReady(Task task) throws RemoteException, InterruptedException{
        Closure closure = new Closure(task.getArgc(), task);
        readyClosure.put(closure);
    }

    public void putReady(List<Task> tasks) throws RemoteException, InterruptedException{
        for(Task t : tasks){
            if(!doneTasks.contains(t.id)){
                Closure closure = new Closure(t.getArgc(), t);
                readyClosure.put(closure);
            }
        }
    }

    public void putDoneTask(Task task) throws RemoteException, InterruptedException{
        doneTasks.add(task.id);
    }

    public void putSpawnResult(SpawnResult result) throws RemoteException, InterruptedException{
        spawnResultQ.put(result);
    }

    public SpawnResult getSpawnResult() throws RemoteException, InterruptedException{
        return spawnResultQ.take();
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

    private class SpawnResultHandler implements Runnable{
        public SpawnResultHandler(){}

        public void run(){
            while(true){
                if(spawnResultQ.size() != 0){
                    try{
                        SpawnResult result = SpaceImpl.this.getSpawnResult();
                        SpaceImpl.this.putWaiting(result.successor);
                        for(int i = 0; i < result.subTasks.size(); i++){
                            Continuation cont = Task.generateCont(i, result.successor);
                            result.subTasks.get(i).setCont(cont);
                            SpaceImpl.this.putReady(result.subTasks.get(i));
                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
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
            List<Task> taskList = new ArrayList<>();
            try{
                while(true){
                    Task t = null;
                    long startTime = System.nanoTime();
                    try{
                        t = SpaceImpl.this.takeReady();
                        taskList.add(t);
                        computer.Execute(t);
                    }
                    catch (RemoteException e){
                        try{
                            putReady(taskList);
                            //Computer.tasksQ.put(t);
                            computerProxies.remove(computer);
                        }
                        catch(RemoteException ex){
                            ex.printStackTrace();
                        }
                        System.out.println("Computer #" + computerId + " is dead!!!");
                        return;
                    }
                    Logger.getLogger( this.getClass().getCanonicalName() )
                        .log( Level.INFO, "Run time: {0} ms.", ( System.nanoTime() - startTime) / 1000000 );

                }
            }
            catch (InterruptedException ignore) {}
        }

        public void exit() { try { computer.exit(); } catch ( RemoteException ignore ) {} }

    }
}
