package system;
import api.*;
import java.util.List;

public class SpawnResult{
    public Task successor;
    // the tasks should be ordered according to the slot#
    public List<Task> subTasks;

    public SpawnResult(Task successor, List<Task> subTasks){
        this.successor = successor;
        this.subTasks = subTasks;
    }
}
