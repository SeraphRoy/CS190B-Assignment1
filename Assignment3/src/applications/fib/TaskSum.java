package applications.fib;
import api.*;
import system.*;
import java.util.List;
import java.rmi.RemoteException;

public class TaskSum extends Task{

    public TaskSum(Space space, List<Argument> list, Continuation cont){
        super(space, list, cont);
        argc = 2;
    }

    @Override
    public Object generateArgument(){
        int first = (int) argumentList.get(0).getValue();
        int second = (int) argumentList.get(1).getValue();
        return first+second;
    }

}
