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

    public final int coreNum;

    public ComputerImpl() throws RemoteException{
        coreNum = Space.MULTICORE ? Runtime.getRuntime().availableProcessors() : 1;
        for(int i = 0; i < coreNum; i++){
            new Thread(new Core(Computer.tasksQ)).start();
        }
    }

    public void Execute(Task task) throws RemoteException{
        numTasks++;
        try{
            //task.run();
            //if(tasksQ.size() == 0)
            Computer.tasksQ.put(task);
            if(Computer.tasksQ.size() > 10)
                synchronized(Computer.tasksQ){
                    Computer.tasksQ.wait();
                }

        }
        catch(Exception e){
            e.printStackTrace();
        }
        return;
    }

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException{
        final String domainName = "localhost";
        System.setSecurityManager( new SecurityManager() );
        final String url = "rmi://" + domainName + ":" + Space.PORT + "/" + Space.SERVICE_NAME;
        final Space space = (Space) Naming.lookup(url);
        Computer computer = new ComputerImpl();
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
