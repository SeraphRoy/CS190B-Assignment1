package system;

import api.*;

public class ResultWrapper<T> implements java.io.Serializable{
    public int type;

    public Continuation cont = null;

    public T result = null;

    public SpawnResult spawnResult = null;

    public Space space = null;

    public Task task = null;

    public boolean needToUpdate = false;

    public ResultWrapper(int type, SpawnResult spawnResult, Space space, Task task){
        this.type = type;
        this.spawnResult = spawnResult;
        this.space = space;
        this.task = task;
    }

    public ResultWrapper(int type, Continuation cont, T result, Space space, Task task){
        this.type = type;
        this.cont = cont;
        this.result = result;
        this.space = space;
        this.task = task;
    }

}
