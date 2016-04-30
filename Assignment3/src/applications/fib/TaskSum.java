package applications.fib;
import api.*;
import system.*;

public class TaskSum extends Task<Integer>{
    @Override
    public void call(){
        space.sendArgument(cont, argumentList.get(0).getValue()+argumentList.get(1).getValue());
    }
}
