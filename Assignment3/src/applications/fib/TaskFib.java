package applications.fib;
import api.*;
import system.*;
import java.util.List;
import java.util.ArrayList;
import java.rmi.RemoteException;

public class TaskFib extends Task<Integer>{

    public TaskFib(Space space, List<Argument<T>> list, Continuation cont){
        super(space, list, cont);
        argc = 1;
    }

    @Override
    public void spawnReady(){
        Argument<Integer> a1 = new Argument<>(list.get(0)-1, 0);
        Argument<Integer> a2 = new Argument<>(list.get(0)-2, 0);
        List<Argument<Integer>> newList1 = new ArrayList<>();
        List<Argument<Integer>> newList2 = new ArrayList<>();
        newList1.add(a1);
        newList2.add(a2);
        space.putReady(new TaskFib(space, newList1, cont));
        space.putReady(new TaskFib(space, newList2, cont));
    }

    @Override
    public void spawnWaiting(){
        space.putWaiting(new TaskFib(space, new ArrayList<Argument<Integer>>, cont));
    }

    @Override
    public void call(){
        if(argumentList.get(0).getValue() < 2)
            space.sendArgument(cont, argumentList.get(0).getValue());
        else{
            spawnWaiting();
            spawnReady();
        }
    }

}
