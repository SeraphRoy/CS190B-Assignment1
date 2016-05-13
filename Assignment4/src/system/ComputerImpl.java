package system;

import api.Space;
import api.Task;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.Naming;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.Runtime;

public class ComputerImpl extends UnicastRemoteObject implements Computer{

    public int numTasks = 0;

    private final BlockingQueue<Task> tasksQ;

    public final int coreNum;

    public ComputerImpl() throws RemoteException{
        tasksQ = new LinkedBlockingQueue<>();
        coreNum = Space.MULTICORE ? Runtime.getRuntime().availableProcessors() : 1;
        for(int i = 0; i < coreNum; i++){
            new Thread(new Core(tasksQ)).start();
        }
    }

    public void Execute(Task task) throws RemoteException{
        numTasks++;
        final long taskStartTime = System.nanoTime();
        try{
            //task.run();
            //if(tasksQ.size() == 0)
            tasksQ.put(task);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        final long taskRunTime = ( System.nanoTime() - taskStartTime ) / 1000000;
        Logger.getLogger( ComputerImpl.class.getCanonicalName() )
            .log( Level.INFO, "Computer Side: Task {0}Task time: {1} ms.", new Object[]{ task, taskRunTime } );
        return;
    }

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException{
        final String domainName = "localhost";
        System.setSecurityManager( new SecurityManager() );
        final String url = "rmi://" + domainName + ":" + Space.PORT + "/" + Space.SERVICE_NAME;
        final Space space = (Space) Naming.lookup(url);
        ComputerImpl computer = new ComputerImpl();
        try{
            space.register(computer);
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
        System.out.println( "Computer running." );
    }

    /**
     * Terminate the JVM.
     * @throws RemoteException - always!
     */
        @Override
        public void exit() throws RemoteException{
            Logger.getLogger( this.getClass().getName() )
                .log(Level.INFO, "Computer: on exit, # completed [0] tasks:", numTasks );
            System.exit( 0 );
        }
}
