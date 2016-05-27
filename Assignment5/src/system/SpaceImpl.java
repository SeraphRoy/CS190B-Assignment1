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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.HashSet;

public class SpaceImpl extends UnicastRemoteObject implements Space{
    private static int computerIds = 0;
    protected BlockingQueue<Closure> readyClosure;
    protected BlockingQueue<Closure> spaceClosure;
    private ConcurrentHashMap<Long, Closure> waitingClosure;
    private LinkedBlockingQueue<Object> resultQueue;
    private final Map<Computer,ComputerProxy> computerProxies = Collections.synchronizedMap( new HashMap<>() );
    private HashSet<Long> doneTasks;
    private BlockingQueue<SpawnResult> spawnResultQ;
    private ShareHandler shareHandler;
    public static boolean MULTICORE = false;

    public static int preFetchNum = 1;

    private Share share = null;

    public SpaceImpl(Share share) throws RemoteException{
        readyClosure = new LinkedBlockingQueue<Closure>();
        spaceClosure = new LinkedBlockingQueue<Closure>();
        waitingClosure = new ConcurrentHashMap<>();
        resultQueue = new LinkedBlockingQueue<Object>();
        doneTasks = new HashSet<>();
        spawnResultQ = new LinkedBlockingQueue<>();
        this.share = share;
        shareHandler = new ShareHandler(share);
        new Thread(shareHandler).start();
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
            spaceClosure.put(closure);
            waitingClosure.remove(cont.getClosureId());
        }
    }

    public void sendArgument(Continuation cont, Object result, Share share) throws RemoteException, InterruptedException{
        sendArgument(cont, result);
        shareHandler.updateShare(share);
    }

    // is called when task needn't compute
    public void sendArgument(Continuation cont) throws RemoteException, InterruptedException{
        Closure closure = waitingClosure.get(cont.getClosureId());
        closure.decrementCounter();
        if(closure.getCounter() == 0){
            spaceClosure.put(closure);
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
        computer.setShare(new Share(this.share.getValue()));
        ComputerProxy c = new ComputerProxy(computer);
        computerProxies.put( computer, c);
        new Thread(c).start();
        System.out.println("Computer #" + c.computerId + " is registered");
    }

    public Task takeReady() throws RemoteException, InterruptedException{
        return readyClosure.take().getTask();
    }

    public Object getResult() throws RemoteException, InterruptedException{return resultQueue.take();}

    public synchronized void updateShare(Share share) throws RemoteException{
        this.share = share.getBetterOne(this.share);
        System.out.println("The space share is: " + share.getValue());
        computerProxies.keySet().forEach(computer -> {
                try{
                    computer.updateShare(share);
                }
                catch(RemoteException e){
                    e.printStackTrace();
                }
            });
    }

    @Override
    public void exit() throws RemoteException{
        computerProxies.values().forEach( proxy -> proxy.exit() );
        //System.exit( 0 );
    }

    public SpaceTasksExecuter createExecuter(int preFetchNum){
        return new SpaceTasksExecuter(preFetchNum);
    }

    public static void main(String[] args){
        if(System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());
        try{
            Registry registry = LocateRegistry.createRegistry(Space.PORT);
            Share share = new Share(Double.MAX_VALUE);
            SpaceImpl space = new SpaceImpl(share);
            new Thread(space.createExecuter(Integer.parseInt(args[1]))).start();
            registry.rebind(Space.SERVICE_NAME, space);
            System.out.println("Space start");
        } catch (Exception e){
            System.err.println("Computer exception:");
            e.printStackTrace();
        }
    }

    public class ShareHandler implements Runnable{
        private Share share;
        private AtomicBoolean needToUpdate = new AtomicBoolean(false);

        public ShareHandler(Share share){
            this.share = share;
        }

        public synchronized void updateShare(Share share){
            this.share = share;
            needToUpdate.set(true);
        }

        public void run(){
            while(true){
                if(needToUpdate.get()){
                    if(share.isBetterThan(SpaceImpl.this.share)){
                        try{
                            SpaceImpl.this.updateShare(share);
                        }
                        catch(RemoteException e){
                            e.printStackTrace();
                        }
                    }
                    needToUpdate.set(false);
                }
            }
        }
    }

    public class SpaceTasksExecuter implements Runnable{
        private boolean turnedOn;

        public SpaceTasksExecuter(int preFetchNum){
            turnedOn = preFetchNum > 1;
            turnedOn = false;
        }

        public void run(){
            while(true){
                try{
                    Task task = spaceClosure.take().getTask();
                    if(turnedOn){
                        ResultWrapper result = task.execute();
                        if(result.type == 0){
                            try{
                                sendArgument(result.cont);
                            }
                            catch(RemoteException | InterruptedException e){
                                System.err.println("error from result type0");
                                e.printStackTrace();
                            }
                        }
                        else if(result.type == 1){
                            try{
                                if(!result.needToUpdate)
                                    sendArgument(result.cont, result.result);
                                else
                                    sendArgument(result.cont, result.result, new Share(result.task.generateShareValue(result.result)));
                            }
                            catch(RemoteException | InterruptedException e){
                                System.err.println("Error in sending arguments");
                                e.printStackTrace();
                            }
                        }
                        else{
                            try{
                                result.space.putSpawnResult(result.spawnResult);
                            }
                            catch(RemoteException | InterruptedException e){
                                System.err.println("Error in putting spawn result");
                                e.printStackTrace();
                            }
                        }
                        try{
                            result.space.putDoneTask(result.task);
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    else{
                        Closure closure = new Closure(task.getArgc(), task);
                        readyClosure.put(closure);
                    }
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
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
                    // Logger.getLogger( this.getClass().getCanonicalName() )
                    //     .log( Level.INFO, "Run time: {0} ms.", ( System.nanoTime() - startTime) / 1000000 );

                }
            }
            catch (InterruptedException ignore) {}
        }

        public void exit() {
            try { computer.exit(); } catch ( RemoteException ignore ) {}
        }
    }
}

