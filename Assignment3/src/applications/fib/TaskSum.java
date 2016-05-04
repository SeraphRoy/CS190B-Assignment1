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
    public void call() throws InterruptedException{
        //System.out.println("haha " + argumentList + " " + argumentList.size());
        // try{
        //     System.out.println(space.getClosures());
        //     System.out.println(space.getClosure());
        // }
        // catch(Exception e){}
        int first = (int) argumentList.get(0).getValue();
        int second = (int) argumentList.get(1).getValue();
        try{
            space.sendArgument(cont, first + second);
        }
        catch(RemoteException e){}
    }
}
