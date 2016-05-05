package system;

import api.*;
import java.util.List;
import java.util.ArrayList;

public class Closure implements java.io.Serializable{
    private int counter;
    final private Task task;
    final private List<Argument> argumentList;
    //public static long closureIds = 0;
    private long closureId;
    private final int argc;

    public Closure(int argc, Task task){
        counter = argc - task.getArgumentList().size();
        this.argc = argc;
        this.task = task;
        closureId = task.id;
        //argumentList = new ArrayList<>(this.argc);
        argumentList = task.getArgumentList();
        // if(argc != 0){
        //     if(list != null){
        //         for(Argument a : list){
        //             if(a != null)
        //                 this.addArgument(a);
        //         }
        //     }
        // }
    }

    public int getCounter(){return counter;}

    public Task getTask(){return task;}

    public List<Argument> getList(){return argumentList;}

    public long getClosureId(){return closureId;}

    public synchronized void addArgument(Argument a){
        // System.out.println(a.getIndex() + " yosh");
        // System.out.println(argumentList.size());
        // System.out.println(argc);
        argumentList.add(a);
        counter --;
    }
}
