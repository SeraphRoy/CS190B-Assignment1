package applications.fib;
import api.*;
import system.*;

public class TaskSum extends Task<Integer>{

    public TaskFib(Space space, List<Argument<T>> list, Continuation cont){
        super(space, list, cont);
        argc = 2;
    }

    @Override
    public void call(){
        space.sendArgument(cont, argumentList.get(0).getValue()+argumentList.get(1).getValue());
    }
}
