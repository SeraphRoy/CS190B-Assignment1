package applications.tsp;

import api.*;
import system.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.rmi.RemoteException;

public class TaskCompose extends Task{

    public TaskCompose(Space space, List<Argument> list, Continuation cont, int argc){
        super(space, list, cont);
        this.argc = argc;
    }

     @Override
     public Object generateArgument(){
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
         return tour;
     }

}
