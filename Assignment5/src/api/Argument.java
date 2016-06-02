package api;
import java.io.Serializable;

public class Argument<T> implements Serializable{
    final private T value;
    final private int index;

    public long T_1 = 0;
    public long T_Inf = 0;

    public Argument(T value, int index){
        this.value = value;
        this.index = index;
    }
    public T getValue(){ return value;}

    public int getIndex(){ return index;}
}
