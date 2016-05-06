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
        this.id = Task.ids++;
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

    public abstract boolean needToCompute();

    // public void spawnWaiting() throws RemoteException, InterruptedException{
    //     System.err.println("You shouldn't reach this point");
    // }

    public List<Argument> getArgumentList(){return argumentList;}

    public Continuation getCont(){return cont;}

    public Space getSapce(){return space;}

    public int getArgc(){return argc;}
}
