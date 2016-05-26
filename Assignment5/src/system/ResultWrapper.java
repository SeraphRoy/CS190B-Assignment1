package system;

import api.*;

public class ResultWrapper{
    public int type;

    public Continuation cont = null;

    public Object result = null;

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

    public ResultWrapper(int type, Continuation cont, Object result, Space space, Task task){
        this.type = type;
        this.cont = cont;
        this.result = result;
        this.space = space;
        this.task = task;
    }
}
