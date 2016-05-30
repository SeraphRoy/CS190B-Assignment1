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
    private LinkedBlockingQueue<Argument> resultQueue;
    private final Map<Computer,ComputerProxy> computerProxies = Collections.synchronizedMap( new HashMap<>() );
    private HashSet<Long> doneTasks;
    private BlockingQueue<SpawnResult> spawnResultQ;
    private ShareHandler shareHandler;
    public static boolean MULTICORE = false;
    private BlockingQueue<BlockingQueue<ResultWrapper>> computerResults;

    public static int preFetchNum = 1;

    private Share share = null;

    public SpaceImpl(Share share) throws RemoteException{
        readyClosure = new LinkedBlockingQueue<Closure>();
        spaceClosure = new LinkedBlockingQueue<Closure>();
        waitingClosure = new ConcurrentHashMap<>();
        resultQueue = new LinkedBlockingQueue<>();
        computerResults = new LinkedBlockingQueue<>();
        doneTasks = new HashSet<>();
        spawnResultQ = new LinkedBlockingQueue<>();
        this.share = share;
        shareHandler = new ShareHandler(share);
        new Thread(shareHandler).start();
        new Thread(new SpawnResultHandler()).start();
        //new Thread(new ComputerResultHandler()).start();
    }

    // task's argumentList IS already initialized
    public void sendArgument(Continuation cont, Argument argument) throws RemoteException, InterruptedException{

        Closure closure = waitingClosure.get(cont.getClosureId());
        if(closure == null){
            this.resultQueue.put(argument);
            return;
        }

        closure.addArgument(argument);
        if(closure.getCounter() == 0){
            spaceClosure.put(closure);
            waitingClosure.remove(cont.getClosureId());
        }
    }

    public void sendArgument(Continuation cont, Argument argument, Share share) throws RemoteException, InterruptedException{
        sendArgument(cont, argument);
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
    // it actually put closure into spaceClosure so that it could possibly be runned on space
    public void putReady(Task task) throws RemoteException, InterruptedException{
        Closure closure = new Closure(task.getArgc(), task);
        spaceClosure.put(closure);
    }

    public void putReady(List<Task> tasks) throws RemoteException, InterruptedException{
        for(Task t : tasks){
            if(!doneTasks.contains(t.id)){
                Closure closure = new Closure(t.getArgc(), t);
                spaceClosure.put(closure);
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

    public void register(Computer computer, int numProcessors) throws RemoteException, InterruptedException{
        computer.setShare(new Share(this.share.getValue()));
        ComputerProxy c = new ComputerProxy(computer, 2 * numProcessors);
        computerProxies.put( computer, c);
        c.startWorkerProxies();
        System.out.println("Computer #" + c.computerId + " is registered");
    }

    public Task takeReady() throws RemoteException, InterruptedException{
        return readyClosure.take().getTask();
    }

    public Argument getResult() throws RemoteException, InterruptedException{return resultQueue.take();}

    public synchronized void updateShare(Share share) throws RemoteException{
        this.share = share.getBetterOne(this.share);
        System.out.println("The space share is: " + share.getValue());
        computerProxies.keySet().forEach(computer -> {
                try{
                    computer.updateShare(share);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            });
    }

    public void putComputerResults(BlockingQueue<ResultWrapper> resultQ) throws RemoteException, InterruptedException{
        computerResults.put(resultQ);
    }

    @Override
    public void exit() throws RemoteException{
        computerProxies.values().forEach( proxy -> proxy.exit() );
        //System.exit( 0 );
    }

    public SpaceTasksExecuter createExecuter(){
        return new SpaceTasksExecuter();
    }

    public static void main(String[] args){
        if(System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());
        try{
            Registry registry = LocateRegistry.createRegistry(Space.PORT);
            Share share = new Share(Double.MAX_VALUE);
            SpaceImpl space = new SpaceImpl(share);
            new Thread(space.createExecuter()).start();
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
        public SpaceTasksExecuter(){}

        public void run(){
            while(true){
                try{
                    Task task = spaceClosure.take().getTask();
                    if(task.spaceCallable()){
                        ResultWrapper result = task.execute(false);
                        result.process(SpaceImpl.this);
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

    private class ComputerResultHandler implements Runnable{
        public ComputerResultHandler(){}

        public void run(){
            while(true){
                BlockingQueue<ResultWrapper> resultQ = null;
                try{
                    resultQ = computerResults.take();
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
                for(ResultWrapper result : resultQ){
                    result.process(SpaceImpl.this);
                    try{
                        putDoneTask(result.task);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
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

    private class ComputerProxy{
        private Computer computer;

        final protected int computerId = computerIds++;

        final private Map<Integer, WorkerProxy> workerMap = new HashMap<>();

        public ComputerProxy(Computer computer, int numWorkerProxies){
            this.computer = computer;
            //numWorkerProxies = 1;
            for ( int id = 0; id < numWorkerProxies; id++ ){
                WorkerProxy workerProxy = new WorkerProxy(id, computer);
                workerMap.put( id, workerProxy );
            }
        }

        private void startWorkerProxies(){
            workerMap.values().forEach( Thread::start );
        }

        private void unregister( Task task, Computer computer, int workerProxyId ){
            try{
                putReady(task);
            }
            catch(RemoteException | InterruptedException e){
                e.printStackTrace();
            }
            workerMap.remove( workerProxyId );
            Logger.getLogger( this.getClass().getName() )
                .log( Level.WARNING, "Computer {0}: Worker failed.", workerProxyId );
            if ( workerMap.isEmpty() ){
                computerProxies.remove( computer );
                Logger.getLogger( ComputerProxy.class.getCanonicalName() )
                    .log( Level.WARNING, "Computer {0} failed.", computerId );
            }
        }

        // @Override
        // public void run(){
        //     List<Task> taskList = new ArrayList<>();
        //     try{
        //         while(true){
        //             Task t = null;
        //             long startTime = System.nanoTime();
        //             try{
        //                 t = SpaceImpl.this.takeReady();
        //                 taskList.add(t);
        //                 computer.Execute(t);
        //             }
        //             catch (RemoteException e){
        //                 try{
        //                     putReady(taskList);
        //                     //Computer.tasksQ.put(t);
        //                     computerProxies.remove(computer);
        //                 }
        //                 catch(RemoteException ex){
        //                     ex.printStackTrace();
        //                 }
        //                 System.out.println("Computer #" + computerId + " is dead!!!");
        //                 return;
        //             }
        //             // Logger.getLogger( this.getClass().getCanonicalName() )
        //             //     .log( Level.INFO, "Run time: {0} ms.", ( System.nanoTime() - startTime) / 1000000 );

        //         }
        //     }
        //     catch (InterruptedException ignore) {}
        // }

        public void exit() {
            try { computer.exit(); } catch ( RemoteException ignore ) {}
        }

        private class WorkerProxy extends Thread{
            final Integer id;
            Computer computer;

            private WorkerProxy(int id, Computer computer) {
                this.id = id;
                this.computer = computer;
            }

            @Override
            public void run(){
                while (true){
                    Task task = null;
                    try{
                        task = SpaceImpl.this.takeReady();
                        final long taskStartTime = System.nanoTime();
                        computer.Execute(task, SpaceImpl.this);
                        final long taskRunTime = ( System.nanoTime() - taskStartTime ) / 1000000;
                        //Logger.getLogger( ComputerImpl.class.getCanonicalName() )
                        //    .log( Level.INFO, "Worker Proxy Side: Task {0}Task time: {1} ms.", new Object[]{ task, taskRunTime } );
                    }
                    catch(RemoteException ignore){
                        unregister( task, computer, id );
                        ignore.printStackTrace();
                        return;
                    }
                    catch (InterruptedException ex){
                        Logger.getLogger( this.getClass().getName() )
                            .log( Level.INFO, null, ex );
                    }
                }
            }
        }
    }
}

