package system;

import api.*;
import java.util.List;
import java.util.ArrayList;

public class Closure implements java.io.Serializable{
    private int counter;
    final private Task<V> task;
    final private ArrayList<Argument<T>> argumentList;
    public static long closureIds = 0;
    private int closureId;

    public Closure(int argc, Task<V> task, List<Argument<T>> list){
        counter = argc;
        this.task = task;
        closureId = Task.closureIds++;
        argumentList = new ArrayList<>(argc);
        if(argc != 0){
            if(list != null){
                for(Argument<T> a : list){
                    if(a != null)
                        this.addArgument(a);
                }
            }
        }
    }

    public int getCounter(){return counter;}

    public Task getTask(){return task;}

    public List<> getList(){return argumentList;}

    public void addArgument(Argument<T> a){
        argumentList.add(a.getIndex(), a);
        counter --;
    }
}
