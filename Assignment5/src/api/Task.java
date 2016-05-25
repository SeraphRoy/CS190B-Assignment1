package api;
import system.*;
import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.List;
import java.rmi.RemoteException;
import javax.swing.JLabel;

/**
 *
 * @author Peter Cappello, Yanxi Chen
 * The client should override spawn(), spawnNext(), generateArgument(), and needToCompute() for general tasks,
 * and only the last two for compose tasks.
 */
public abstract class Task implements Serializable{

    final protected Space space;

    public Computer computer;

    final protected List<Argument> argumentList;

    protected Continuation cont;

    protected int argc;

    public long id;

    public Task(Space space, List<Argument> list, Continuation cont, Computer computer){
        this.space = space;
        this.argumentList = list;
        this.cont = cont;
        this.id = java.util.UUID.randomUUID().getLeastSignificantBits();
        this.computer = computer;
    }

    public Task(Space space, List<Argument> list){
        this.space = space;
        this.argumentList = list;
        this.cont = null;
        this.id = java.util.UUID.randomUUID().getLeastSignificantBits();
        this.computer = null;
    }

    public ResultWrapper execute(){
        ResultWrapper result = null;
        if(needToProceed()){
            if(needToCompute()){
                Object o = generateArgument();
                try{
                    result = new ResultWrapper(1, cont, o, space, this);
                    return result;
                }
                catch(Exception e){
                    System.err.println("ERROR IN SENDING ARGUMENT");
                    e.printStackTrace();
                }
            }
            else{
                try{
                    SpawnResult spawnResult  = spawn();
                    result = new ResultWrapper(2, spawnResult, space, this);
                    return result;
                }
                catch(Exception e){
                    e.printStackTrace();
                    System.err.println("ERROR IN PRODUCING SUBTASKS");
                }
            }
        }
        return result;
    }


    public JLabel viewResult(Object result){
        System.err.println("You shouldn't reach this point");
        return new JLabel();
    }

    public SpawnResult spawn() throws RemoteException, InterruptedException{
        System.err.println("You shouldn't reach this point");
        return null;
    }

    public static Continuation generateCont(int slot, Task t){
        return new Continuation(t.id, slot);
    }

    public abstract Object generateArgument();

    //default is true for compose tasks
    //normal tasks NEED override this
    public boolean needToCompute(){
        return true;
    }

    //default is true for compose tasks
    //normal tasks NEED override this
    public boolean needToProceed(){
        return true;
    }

    public List<Argument> getArgumentList(){return argumentList;}

    public Continuation getCont(){return cont;}

    public void setCont(Continuation cont){this.cont = cont;}

    public Space getSapce(){return space;}

    public int getArgc(){return argc;}

}
