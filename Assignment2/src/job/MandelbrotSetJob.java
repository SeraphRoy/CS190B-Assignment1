package job;

import api.Job;
import api.Result;
import api.Space;
import api.Task;
import tasks.TaskMandelbrotSet;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class MandelbrotSetJob implements Job{
    private double lowerLeftX, lowerLeftY, edgeLength;
    private int nPixels, iterationLimit;

    public MandelbrotSetJob(double lowerLeftX, double lowerLeftY, double edgeLength, int nPixels, int iterationLimit){
        this.lowerLeftX = lowerLeftX;
        this.lowerLeftY = lowerLeftY;
        this.edgeLength = edgeLength;
        this.nPixels = nPixels;
        this.iterationLimit = iterationLimit;
    }

    @Override
    public void generateTasks(Space space) throws RemoteException, InterruptedException{
        List<Task> list = new ArrayList<Task>();

        for(int i = 0; i < nPixels; i++){
            list.add(new TaskMandelbrotSet(lowerLeftX, lowerLeftY, edgeLength, nPixels, iterationLimit, i));
        }
        space.putAll(list);
    }

    @Override
    public Integer[][] collectResults(Space space) throws RemoteException, InterruptedException{
        List<Result> list = new ArrayList<Result>();
        for(int i = 0; i < nPixels; i++){
            list.add(space.take());
        }
        Integer[][] result = new Integer[nPixels][nPixels];
        for(Result r : list){
            Integer[][] current = (Integer[][]) r.getTaskReturnValue();
            for(int i = 0; i < current.length; i++){
                result[i][current[i][1]] = current[i][0];
            }
        }
        return result;
    }
}
