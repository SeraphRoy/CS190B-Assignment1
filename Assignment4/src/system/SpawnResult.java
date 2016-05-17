package system;
import api.Task;
import java.util.List;

public class SpawnResult implements java.io.Serializable{

    private static final long serialVersionUID = 227L;

    public Task successor;
    // the tasks should be ordered according to the slot#
    public List<Task> subTasks;

    public SpawnResult(Task successor, List<Task> subTasks){
        this.successor = successor;
        this.subTasks = subTasks;
    }
}
