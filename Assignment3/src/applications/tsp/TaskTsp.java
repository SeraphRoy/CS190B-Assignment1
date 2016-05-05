package applications.tsp;

import api.*;
import system.*;
import util.Permutation;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JLabel;
import java.rmi.RemoteException;

public class TaskTsp extends Task{


    static final public double[][] CITIES =
    {
        { 1, 1 },
        { 8, 1 },
        { 8, 8 },
        { 1, 8 },
        { 2, 2 },
        { 7, 2 },
        { 7, 7 },
        { 2, 7 },
        { 3, 3 },
        { 6, 3 },
        { 6, 6 },
        { 3, 6 }
    };

    static final public double[][] DISTANCES = initializeDistances();
    static final private Integer ONE = 1;
    static final private Integer TWO = 2;

    //final private int secondCity;
    final private List<Integer> partialCityList;

    //list[0] is a list of cities: 1,2,3,....
    //list[1] is number of fixed cities for permutation
    public TaskTsp(Space space, List<Argument> list, Continuation cont){
        super(space, list, cont);
        argc = 2;
    }

    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append( getClass() );
        stringBuilder.append( "\n\tCities: " );
        stringBuilder.append( 0 ).append( " " );
        stringBuilder.append( secondCity ).append( " " );
        partialCityList.stream().forEach(( city ) ->
                                         {
                                             stringBuilder.append( city ).append( " " );
                                         } );
        return stringBuilder.toString();
    }

    /**
     *
     * @param tour
     * @return
     */
    static public double tourDistance( final List<Integer> tour  )
    {
        double cost = DISTANCES[ tour.get( tour.size() - 1 ) ][ tour.get( 0 ) ];
        for ( int city = 0; city < tour.size() - 1; city ++ )
            {
                cost += DISTANCES[ tour.get( city ) ][ tour.get( city + 1 ) ];
            }
        return cost;
    }

    static private double[][] initializeDistances()
    {
        double[][] distances = new double[ CITIES.length][ CITIES.length];
        for ( int i = 0; i < CITIES.length; i++ )
            for ( int j = 0; j < i; j++ )
                {
                    distances[ i ][ j ] = distances[ j ][ i ] = distance( CITIES[ i ], CITIES[ j ] );
                }
        return distances;
    }

    private static double distance( final double[] city1, final double[] city2 )
    {
        final double deltaX = city1[ 0 ] - city2[ 0 ];
        final double deltaY = city1[ 1 ] - city2[ 1 ];
        return Math.sqrt( deltaX * deltaX + deltaY * deltaY );
    }
}
