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
    public void spawn() throws RemoteException, InterruptedException{
        Task t = new TaskSum(space, new ArrayList<Argument>(), cont);
        System.out.println("haha " + t.nextId);
        space.putWaiting(t);
        System.out.println("yosh " + t.nextId);
        int first = (int)argumentList.get(0).getValue();
        Argument a1 = new Argument(first-1, 0);
        Argument a2 = new Argument(first-2, 0);
        List<Argument> newList1 = new ArrayList<>();
        List<Argument> newList2 = new ArrayList<>();
        newList1.add(a1);
        newList2.add(a2);
        Continuation newCont0 = new Continuation(t.nextId, 0);
        Continuation newCont1 = new Continuation(t.nextId, 1);
        space.putReady(new TaskFib(space, newList1, newCont0));
        space.putReady(new TaskFib(space, newList2, newCont1));
    }

    // @Override
    // public void spawnWaiting() throws RemoteException, InterruptedException{
    //     space.putWaiting(new TaskSum(space, new ArrayList<Argument>(), cont));
    // }

    @Override
    public JLabel viewResult(Object result){
        Logger.getLogger( this.getClass().getCanonicalName() ).log( Level.INFO, "Result is: ", result);
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
                spawn();
            }
            catch(RemoteException e){}
        }
    }
}
