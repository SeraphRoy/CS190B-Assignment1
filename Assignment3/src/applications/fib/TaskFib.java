package applications.fib;
import api.*;
import system.*;
import java.util.List;
import java.util.ArrayList;
import java.rmi.RemoteException;

public class TaskFib extends Task<Integer>{

    public TaskFib(Space space, List<Argument> list, Continuation cont){
        super(space, list, cont);
        argc = 1;
    }

    @Override
    public void spawnReady() throws RemoteException{
        int first = (int)argumentList.get(0).getValue();
        Argument a1 = new Argument(first-1, 0);
        Argument a2 = new Argument(first-2, 0);
        List<Argument> newList1 = new ArrayList<>();
        List<Argument> newList2 = new ArrayList<>();
        newList1.add(a1);
        newList2.add(a2);
        space.putReady(new TaskFib(space, newList1, cont));
        space.putReady(new TaskFib(space, newList2, cont));
    }

    @Override
    public void spawnWaiting() throws RemoteException{
        space.putWaiting(new TaskFib(space, new ArrayList<Argument>(), cont));
    }

    @Override
    public void call(){
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
