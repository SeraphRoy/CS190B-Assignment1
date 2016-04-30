package applications.fib;
import api.*;
import system.*;
import java.util.List;
import java.util.ArrayList;
import java.rmi.RemoteException;

public class TaskFib extends Task<Integer>{

    public TaskFib(Space space, List<Argument<T>> list, Continuation cont){
        this.space = space;
        this.argumentList = list;
        this.cont = cont;
    }

    @Override
    public void spawnReady(){
        Argument<Integer> a1 = new Argument<>(list.get(0)-1, 0);
        Argument<Integer> a2 = new Argument<>(list.get(0)-2, 0);
        List<Argument<T>> newList1 = new List<>();
        List<Argument<T>> newList2 = new List<>();
        newList1.add(a1);
        newList2.add(a2);
        space.putReady(new TaskFib(space, newList1, cont));
        space.putReady(new TaskFib(space, newList2, cont));
    }

    @Override
    public void spawnWaiting(){
        //space.putWaiting();
    }

    @Override
    public void call(){

    }
}
