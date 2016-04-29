package api;
import java.io.Serializable;

public class Argument<T> implements Serializable{
    final private T value;
    final private int index;

    public Argument(T value, int index){
        this.value = value;
        this.index = index;
    }

    public T getValue(){ return value;}

    public int getIndex(){ return index;}
}
