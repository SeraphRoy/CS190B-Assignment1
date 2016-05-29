package system;

import api.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.rmi.RemoteException;

public class ResultHandler implements Runnable{

    private BlockingQueue<ResultWrapper> resultQ;

    private Space space;

    public ResultHandler(BlockingQueue<ResultWrapper> resultQ, Space space){
        this.resultQ = resultQ;
        this.space = space;
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
            result.process(space);
            try{
                space.putDoneTask(result.task);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
