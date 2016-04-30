package api;
import system.*;
import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 *
 * @author Peter Cappello
 * @param <V> the task return type.
 */
public abstract class Task<V> extends Serializable, Callable<V>
{
    private T value;

    final private Space space;

    final private List<Argument<T>> argumentList;

    final private Continuation cont;

    @Override
    public void call();

    public void spawnReady(){
        System.err.println("You shouldn't reach this point");
    }

    public void spawnWaiting(){
        System.err.println("You shouldn't reach this point");
    }

    public void sendArgument(){
        System.err.println("You shouldn't reach this point");
    }
}
