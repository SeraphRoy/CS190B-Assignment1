package api;
import system.*;
import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.List;
import java.rmi.RemoteException;
import javax.swing.JLabel;

/**
 *
 * @author Peter Cappello
 * @param <V> the task return type.
 */
public abstract class Task implements Serializable{

    final protected Space space;

    final protected List<Argument> argumentList;

    final protected Continuation cont;

    protected int argc;

    private static long ids = 0;

    //successor's closure id
    public long id;

    public Task(Space space, List<Argument> list, Continuation cont){
        this.space = space;
        this.argumentList = list;
        this.cont = cont;
        //this.id = Task.ids++;
        this.id = java.util.UUID.randomUUID().getLeastSignificantBits();
    }

    public abstract void call() throws InterruptedException;

    public JLabel viewResult(Object result){
        System.err.println("You shouldn't reach this point");
        return new JLabel();
    }

    public void spawn() throws RemoteException, InterruptedException{
        System.err.println("You shouldn't reach this point");
    }

    // public void spawnWaiting() throws RemoteException, InterruptedException{
    //     System.err.println("You shouldn't reach this point");
    // }

    public List<Argument> getArgumentList(){return argumentList;}

    public Continuation getCont(){return cont;}

    public Space getSapce(){return space;}

    public int getArgc(){return argc;}
}
