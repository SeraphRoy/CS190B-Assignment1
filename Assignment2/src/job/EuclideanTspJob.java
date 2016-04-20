package job;

import clients.ClientEuclideanTsp;
import tasks.TaskEuclideanTsp;
import api.Result;
import api.Space;
import java.util.List;
import java.util.ArrayList;


public class EuclideanTspJob{
    private TaskEuclideanTsp task;
    private Space space;

    public EuclideanTspJob(TaskEuclideanTsp task, Space space){
        this.task = task;
        this.space = space;
    }

    public void generateTasks(){
        List<Task> list = task.splitTasks();
        space.putAll(list);
    }

    public Result collectResults(int numTasks){
        List<Result> list = new ArrayList<Result>;
        for(int i = 0; i < numTasks; i++){
            Result r = space.take();
            list.add(r);
        }
        
    }
}
