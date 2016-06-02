package system;
import api.*;
import java.rmi.RemoteException;

public class ResultWrapper<T> implements java.io.Serializable{
    public int type;

    public Continuation cont = null;

    public T result = null;

    public SpawnResult spawnResult = null;

    public Task task = null;

    public boolean needToUpdate = false;

    public long T_1 = 0;

    public long T_Inf = 0;

    public ResultWrapper(int type, SpawnResult spawnResult, Task task){
        this.type = type;
        this.spawnResult = spawnResult;
        this.task = task;
    }

    public ResultWrapper(int type, Continuation cont, T result, Task task){
        this.type = type;
        this.cont = cont;
        this.result = result;
        this.task = task;
    }

    public void process(Space space){
        // skip by branch and bound
        if(type == 0){
            try{
                space.sendArgument(cont);
            }
            catch(RemoteException | InterruptedException e){
                System.err.println("error from result type0");
                e.printStackTrace();
            }
        }
        // do actual calculation and send argument to successor
        else if(type == 1){
            try{
                if(!needToUpdate){
                    Argument argument = new Argument(result, cont.getSlot());
                    argument.T_1 += this.T_1;
                    argument.T_Inf += this.T_Inf;
                    space.sendArgument(cont, argument);
                }
                else{
                    Argument argument = new Argument(result, cont.getSlot());
                    argument.T_1 += this.T_1;
                    argument.T_Inf += this.T_Inf;
                    space.sendArgument(cont, argument, new Share(task.generateShareValue(result)));
                }
            }
            catch(RemoteException | InterruptedException e){
                System.err.println("Error in sending arguments");
                e.printStackTrace();
            }
        }
        // divided into sub-tasks
        else{
            try{
                space.putSpawnResult(spawnResult);
            }
            catch(RemoteException | InterruptedException e){
                System.err.println("Error in putting spawn result");
                e.printStackTrace();
            }
        }
    }
}
