package system;

import api.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Core implements Runnable{

    private BlockingQueue<Task> readyTasks;

    private BlockingQueue<ResultWrapper> resultQ;

    public Core(BlockingQueue<Task> readyTasks){
        this.readyTasks = readyTasks;
        resultQ = new LinkedBlockingQueue<>();
    }

    public void run(){
        new Thread(new ResultHandler(resultQ)).start();
        while(true){
            try{
                final long taskStartTime = System.nanoTime();
                Task task = null;
                task = readyTasks.take();
                ResultWrapper result = task.run();
                resultQ.put(result);
                synchronized (readyTasks){
                    readyTasks.notify();
                }
                final long taskRunTime = ( System.nanoTime() - taskStartTime ) / 1000000;
                Logger.getLogger( ComputerImpl.class.getCanonicalName() )
                    .log( Level.INFO, "Core Side: Task {0}Task time: {1} ms.", new Object[]{ task, taskRunTime } );
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}
