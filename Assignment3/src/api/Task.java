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

    final protected List<Argument> argumentList;

    final protected Continuation cont;

    protected int argc;

    public long id;

    public Task(Space space, List<Argument> list, Continuation cont){
        this.space = space;
        this.argumentList = list;
        this.cont = cont;
        this.id = java.util.UUID.randomUUID().getLeastSignificantBits();
    }

    public void call() throws InterruptedException{
        if(needToCompute()){
            Object o = generateArgument();
            try{
                space.sendArgument(cont, o);
            }
            catch(RemoteException e){
                System.err.println("ERROR");
            }
        }
        else{
            try{
                spawn(spawnNext());
            }
            catch(RemoteException e){
                System.err.println("ERROR");
            }
        }
    }


    public JLabel viewResult(Object result){
        System.err.println("You shouldn't reach this point");
        return new JLabel();
    }

    public void spawn(Task t) throws RemoteException, InterruptedException{
        System.err.println("You shouldn't reach this point");
    }

    //return the task that has been put into the space
    public Task spawnNext() throws RemoteException, InterruptedException{
        System.err.println("You shouldn't reach this point");
        return null;
    }

    public Continuation generateCont(int slot, Task t){
        return new Continuation(t.id, slot);
    }

    public abstract Object generateArgument();

    //default is true for compose tasks
    //normal tasks NEED override this
    public boolean needToCompute(){
        return true;
    }

    public List<Argument> getArgumentList(){return argumentList;}

    public Continuation getCont(){return cont;}

    public Space getSapce(){return space;}

    public int getArgc(){return argc;}
}
