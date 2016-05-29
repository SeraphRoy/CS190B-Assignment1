package system;

import api.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.rmi.RemoteException;

public class ResultHandler implements Runnable{

    private BlockingQueue<ResultWrapper> resultQ;

    public ResultHandler(BlockingQueue<ResultWrapper> resultQ){
        this.resultQ = resultQ;
    }

    public void run(){
        while(true){
            // if(resultQ.size() != 0){
            //     BlockingQueue<ResultWrapper> temp = null;
            //     synchronized(resultQ){
            //         temp = resultQ;
            //         resultQ = new LinkedBlockingQueue<>();
            //     }
            //     try{
            //         temp.peek().space.putComputerResults(temp);
            //     }
            //     catch(RemoteException | InterruptedException e){
            //         e.printStackTrace();
            //     }
            // }

            ResultWrapper result = null;
            try{
                result = resultQ.take();
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
            result.process();
            try{
                result.space.putDoneTask(result.task);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
