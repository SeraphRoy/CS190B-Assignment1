package api;
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

    @Override
    V call();

    public void spawnReady();

    public void spawnWaiting();

    public void sendArgument();
}
