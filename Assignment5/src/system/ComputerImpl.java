package system;

import api.Space;
import api.Task;
import api.Client;

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

    public final int coreNum;

    private BlockingQueue<ResultWrapper> resultQ;;

    private Share share = null;

    public ComputerImpl() throws RemoteException{
        resultQ = new LinkedBlockingQueue<>();
        coreNum = Runtime.getRuntime().availableProcessors();
        // for(int i = 0; i < coreNum; i++)
        //     new Thread(new ResultHandler(resultQ)).start();
    }

    public void Execute(Task task, Space space) throws RemoteException{
        numTasks++;
        try{
            final long taskStartTime = System.nanoTime();
            task.computer = this;
            task.share = new Share(share.getValue());
            ResultWrapper result = task.execute(true);
            final long taskRunTime = ( System.nanoTime() - taskStartTime ) / 1000000;
            // Logger.getLogger( ComputerImpl.class.getCanonicalName() )
            //     .log( Level.INFO, "Computer Side: Task {0}Task time: {1} ms.", new Object[]{ task, taskRunTime } );
            if(result != null)
                result.process(space);
                //resultQ.put(result);
            //if(tasksQ.size() == 0)
            // tasksQ.put(task);
            // if(tasksQ.size() > SpaceImpl.preFetchNum)
            //     //if(tasksQ.size() > 30)
            //     synchronized(tasksQ){
            //         tasksQ.wait();
            //     }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public synchronized Share getShare(){return share;}

    public synchronized void updateShare(Share share) throws RemoteException{
        this.share = share.getBetterOne(this.share);
    }

    public synchronized void setShare(Share share) throws RemoteException{
        this.share = share;
    }

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException{
        SpaceImpl.MULTICORE = true;
        SpaceImpl.preFetchNum = 10;
        final String domainName = "localhost";
        System.setSecurityManager( new SecurityManager() );
        final String url = "rmi://" + domainName + ":" + Space.PORT + "/" + Space.SERVICE_NAME;
        final Space space = (Space) Naming.lookup(url);
        ComputerImpl computer = new ComputerImpl();
        try{
            space.register(computer, computer.coreNum);
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
