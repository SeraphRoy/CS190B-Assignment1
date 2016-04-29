package applications.fib;
import api.*;
import system.Closure;
import java.util.List;
import java.util.ArrayList;
import java.rmi.RemoteException;

public class TaskFib extends Task<Integer>{

    private int n;

    public TaskFib(Space space, List<Argument<T>> list, int n){
        this.space = space;
        this.argumentList = list;
        this.n = n;
    }

    @Override
    public void spawnReady(){
        space.putReady(new TaskFib(space, list, newN));
    }

    @Override
    public void spawnWaiting(){
    }
}
