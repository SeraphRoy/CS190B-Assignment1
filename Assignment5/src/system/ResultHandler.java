package system;

import api.*;
import java.util.concurrent.BlockingQueue;
import java.rmi.RemoteException;

public class ResultHandler implements Runnable{

    private BlockingQueue<ResultWrapper> resultQ;

    public ResultHandler(BlockingQueue<ResultWrapper> resultQ){
        this.resultQ = resultQ;
    }

    public void run(){
        while(true){
            ResultWrapper result = null;
            try{
                result = resultQ.take();
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
            if(result.type == 0){
                try{
                    result.space.sendArgument(result.cont);
                }
                catch(RemoteException | InterruptedException e){
                    System.err.println("error from result type0");
                    e.printStackTrace();
                }
            }
            else if(result.type == 1){
                try{
                    if(!result.needToUpdate)
                        result.space.sendArgument(result.cont, result.result);
                    else
                        result.space.sendArgument(result.cont, result.result, result.task.computer.getShare());
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
    }
}
