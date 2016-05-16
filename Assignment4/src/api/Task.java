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

    final long serialVersionUID = 255L;

    final protected Space space;

    final protected List<Argument> argumentList;

    protected Continuation cont;

    protected int argc;

    public long id;

    public Task(Space space, List<Argument> list, Continuation cont){
        this.space = space;
        this.argumentList = list;
        this.cont = cont;
        this.id = java.util.UUID.randomUUID().getLeastSignificantBits();
    }

    public Task(Space space, List<Argument> list){
        this.space = space;
        this.argumentList = list;
        this.cont = null;
        this.id = java.util.UUID.randomUUID().getLeastSignificantBits();
    }

    public void execute(){
        if(needToCompute()){
            Object o = generateArgument();
            try{
                space.sendArgument(cont, o);
            }
            catch(Exception e){
                System.err.println("ERROR IN SENDING ARGUMENT");
                e.printStackTrace();
            }
        }
        else{
            try{
                SpawnResult result  = spawn();
                space.pushSpawnResult(result);
                // for(int i = 0; i < result.subTasks.size(); i++){
                //     Continuation cont = generateCont(i, result.successor);
                //     result.subTasks.get(i).cont = cont;
                //     space.putReady(result.subTasks.get(i));
                // }
            }
            catch(RemoteException e){
                e.printStackTrace();
            }
            catch(InterruptedException ex){
                ex.printStackTrace();
            }
        }
    }


    public JLabel viewResult(Object result){
        System.err.println("You shouldn't reach this point: viewResult()");
        return new JLabel();
    }

    public SpawnResult spawn() throws RemoteException, InterruptedException{
        System.err.println("You shouldn't reach this point: spawn()");
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

    public List<Argument> getArgumentList(){return argumentList;}

    public Continuation getCont(){return cont;}

    public void setCont(Continuation cont){this.cont = cont;}

    public Space getSapce(){return space;}

    public int getArgc(){return argc;}

}
