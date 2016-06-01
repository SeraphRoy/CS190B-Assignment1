package applications.tsp;

import api.*;
import system.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.rmi.RemoteException;

public class TaskCompose extends Task<List<Integer>> implements SpaceCallable{

    public TaskCompose(List<Argument<List<Integer>>> list, Continuation cont, int argc){
        super(list, cont);
        this.argc = argc;
    }

     @Override
     public List<Integer> generateArgument(){
         List<Integer> tour = new LinkedList<>();
         double shortestTourDistance = Double.MAX_VALUE;
         for(Argument argument : argumentList){
             List<Integer> path = (List<Integer>)argument.getValue();
             double tourDistance = TaskTsp.tourDistance(path);
             if(tourDistance < shortestTourDistance){
                 shortestTourDistance = tourDistance;
                 tour = new ArrayList<>(path);
             }
         }
         if(tour.size() == 0){
             for(int i = 0; i < TaskTsp.CITIES.length; i++){
                 tour.add(i);
             }
         }
         return tour;
     }

    @Override
    public Comparable generateShareValue(List<Integer> o){
        double distance = TaskTsp.tourDistance((List<Integer>)o);
        return distance;
    }
}
