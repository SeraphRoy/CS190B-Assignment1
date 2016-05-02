package api;
import java.io.Serializable;

public class Argument implements Serializable{
    final private Object value;
    final private int index;

    public Argument(Object value, int index){
        this.value = value;
        this.index = index;
    }

    public Object getValue(){ return value;}

    public int getIndex(){ return index;}
}
