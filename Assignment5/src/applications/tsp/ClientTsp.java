package applications.tsp;
import api.*;
import system.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ClientTsp extends Client{
    public ClientTsp(Task task, String domainName) throws RemoteException, NotBoundException, MalformedURLException{
        super(task, "Tsp", domainName);
    }

    public static void main(String[] args) throws Exception{
        Client c = new ClientTsp(null, args[0]);
        List<Integer> fixedList = new ArrayList<>();
        List<Integer> partialList = new ArrayList<>();
        fixedList.add(0);
        for(int i = 1; i < TaskTsp.CITIES.length; i++)
            partialList.add(i);
        Argument argument0 = new Argument(fixedList, 0);
        Argument argument1 = new Argument(partialList, 1);
        List<Argument<List<Integer>>> list = new ArrayList<>();
        list.add(argument0);
        list.add(argument1);
        Continuation cont = new Continuation(-1, 0);
        Task t = new TaskTsp(list, cont);
        t.share = new Share(calculateUpperBound());
        c.setTask(t);
        c.run();
    }

    static public double calculateUpperBound(){
        List<Integer> tour = new ArrayList<>();
        HashSet<Integer> set = new HashSet<>();
        tour.add(0);
        set.add(0);
        int index = 0;
        while(set.size() != TaskTsp.CITIES.length){
            double min = Double.MAX_VALUE;
            int point = -1;
            for(int i = 0; i < TaskTsp.CITIES.length; i++){
                if(i != index && !set.contains(i) && TaskTsp.DISTANCES[index][i] < min){
                    min = TaskTsp.DISTANCES[index][i];
                    point = i;
                }
            }
            if(point != -1){
                tour.add(point);
                set.add(point);
                index = point;
            }
        }
        return TaskTsp.tourDistance(tour);
    }
}
