package system;

import api.*;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Core implements Runnable{

    private BlockingQueue<Task> readyTasks;

    public Core(BlockingQueue<Task> readyTasks){
        this.readyTasks = readyTasks;
    }

    public void run(){
        while(true){
            try{
                final long taskStartTime = System.nanoTime();
                Task task = readyTasks.take();
                task.run();
                synchronized (Computer.tasksQ){
                    Computer.tasksQ.notify();
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
