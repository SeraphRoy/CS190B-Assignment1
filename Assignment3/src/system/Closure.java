package system;

import api.*;
import java.util.List;
import java.util.ArrayList;

public class Closure implements java.io.Serializable{
    private int counter;
    final private Task task;
    final private ArrayList<Argument> argumentList;
    public static long closureIds = 0;
    private long closureId;

    public Closure(int argc, Task task, List<Argument> list){
        counter = argc;
        this.task = task;
        closureId = Closure.closureIds++;
        argumentList = new ArrayList<>(argc);
        if(argc != 0){
            if(list != null){
                for(Argument a : list){
                    if(a != null)
                        this.addArgument(a);
                }
            }
        }
    }

    public int getCounter(){return counter;}

    public Task getTask(){return task;}

    public List<Argument> getList(){return argumentList;}

    public long getClosureId(){return closureId;}

    public void addArgument(Argument a){
        argumentList.add(a.getIndex(), a);
        counter --;
    }
}
