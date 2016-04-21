package job;

import clients.ClientEuclideanTsp;
import tasks.TaskEuclideanTsp;
import api.Result;
import api.Space;
import api.Job;
import api.Task;
import java.util.List;
import java.util.ArrayList;
import java.rmi.RemoteException;


public class EuclideanTspJob implements Job{
    private TaskEuclideanTsp task;

    public EuclideanTspJob(TaskEuclideanTsp task){
        this.task = task;
    }

    public void generateTasks(Space space) throws RemoteException, InterruptedException{
        List<Task> list = task.splitTasks();
        space.putAll(list);
    }

    public List<Integer> collectResults(Space space) throws RemoteException, InterruptedException{
        List<Result> list = new ArrayList<Result>();
        for(int i = 0; i < task.cityNum-1; i++){
            list.add(space.take());
        }
        double min_distance = Double.MAX_VALUE;
        int current_distance = 0;
        List<Integer> map = new ArrayList<Integer>();
        for(Result r : list){
            List<Integer> temp_map = (List<Integer>) r.getTaskReturnValue();
            current_distance = task.getDistance(temp_map);
            if(min_distance > current_distance){
                min_distance = current_distance;
                map = temp_map;
            }
        }
        System.out.println(min_distance);
        return map;
    }
}
