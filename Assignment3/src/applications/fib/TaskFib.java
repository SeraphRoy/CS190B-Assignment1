package applications.fib;
import api.*;
import system.*;
import java.util.List;
import java.util.ArrayList;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

public class TaskFib extends Task{

    public TaskFib(Space space, List<Argument> list, Continuation cont){
        super(space, list, cont);
        argc = 1;
    }

    @Override
    public void spawnReady() throws RemoteException, InterruptedException{
        int first = (int)argumentList.get(0).getValue();
        Argument a1 = new Argument(first-1, 0);
        Argument a2 = new Argument(first-2, 0);
        List<Argument> newList1 = new ArrayList<>();
        List<Argument> newList2 = new ArrayList<>();
        newList1.add(a1);
        newList2.add(a2);
        Continuation newCont = new Continuation(nextId, cont.getSlot());
        space.putReady(new TaskFib(space, newList1, newCont));
        space.putReady(new TaskFib(space, newList2, newCont));
    }

    @Override
    public void spawnWaiting() throws RemoteException, InterruptedException{
        space.putWaiting(new TaskSum(space, new ArrayList<Argument>(), cont));
    }

    @Override
    public JLabel viewResult(Object result){
        Logger.getLogger( this.getClass().getCanonicalName() ).log( Level.INFO, "Result is: ", (int) result);
        return new JLabel();
    }

    @Override
    public void call() throws InterruptedException{
        if((int) argumentList.get(0).getValue() < 2){
            try{
                space.sendArgument(cont, argumentList.get(0).getValue());
            }
            catch(RemoteException e){}
        }
        else{
            try{
                spawnWaiting();
                spawnReady();
            }
            catch(RemoteException e){}
        }
    }
}
