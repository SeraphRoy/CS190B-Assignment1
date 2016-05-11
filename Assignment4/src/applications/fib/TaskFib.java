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

    public TaskFib(Space space, List<Argument> list){
        super(space, list);
        argc = 1;
    }

    @Override
    public SpawnResult spawn() throws RemoteException, InterruptedException{
        Task t = new TaskSum(space, new ArrayList<Argument>(), cont);
        int first = (int)argumentList.get(0).getValue();
        Argument a1 = new Argument(first-1, 0);
        Argument a2 = new Argument(first-2, 0);
        List<Argument> newList1 = new ArrayList<>();
        List<Argument> newList2 = new ArrayList<>();
        newList1.add(a1);
        newList2.add(a2);
        Task task1 = new TaskFib(space, newList1);
        Task task2 = new TaskFib(space, newList2);
        List<Task> list = new ArrayList<>();
        list.add(task1);
        list.add(task2);
        return new SpawnResult(t, list);
    }

    @Override
    public JLabel viewResult(Object result){
        System.out.println("Result is: " + result);
        return new JLabel();
    }

    @Override
    public Object generateArgument(){
        return argumentList.get(0).getValue();
    }

    @Override
    public boolean needToCompute(){
        return (int)argumentList.get(0).getValue() < 2;
    }

}
