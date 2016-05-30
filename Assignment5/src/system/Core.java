package system;

import api.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.rmi.RemoteException;


public class Core implements Runnable{

    private BlockingQueue<Task> readyTasks;

    private BlockingQueue<ResultWrapper> resultQ;

    private Computer computer;

    public Core(BlockingQueue<Task> readyTasks, Computer computer){
        this.readyTasks = readyTasks;
        this.computer = computer;
        resultQ = new LinkedBlockingQueue<>();
    }

    public void run(){
        //new Thread(new ResultHandler(resultQ)).start();
        while(true){
            try{
                Task task = null;
                task = readyTasks.take();
                task.computer = this.computer;
                try{
                    task.share = new Share(computer.getShare().getValue());
                }
                catch(RemoteException e){
                    e.printStackTrace();
                }
                ResultWrapper result = task.execute(true);
                if(result != null)
                    resultQ.put(result);
                synchronized (readyTasks){
                    readyTasks.notify();
                }
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}
