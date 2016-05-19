package applications.tsp;

import api.*;
import system.*;
import java.util.stream.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;

public class TaskTsp extends Task{

    static final private int NUM_PIXALS = 600;

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

        // { 6, 3 },
        // { 2, 2 },
        // { 5, 8 },
        // { 1, 5 },
        // { 1, 6 },
        // { 2, 7 },
        // { 2, 8 },
        // { 6, 5 },
        // { 1, 3 },
        // { 6, 6 }

        // { 0, 0 },
        // { 1, 1 },
        // { 2, 0 },
        // { 3, 1 },
        // { 4, 0 },
        // { 5, 1 },
        // { 6, 0 },
        // { 7, 1 },
        // { 8, 0 },
        // { 9, 1 },
        // { 10, 0 }
    };

    static final public double[][] DISTANCES = initializeDistances();
    private List<Integer> shortestTour = new ArrayList<Integer>();


    //list[0] is a list of fixed cities
    //list[1] is a list of partial cities
    public TaskTsp(Space space, List<Argument> list, Continuation cont){
        super(space, list, cont);
        argc = 2;
    }

    public TaskTsp(Space space, List<Argument> list){
        super(space, list);
        argc = 2;
    }

    @Override
    public SpawnResult spawn() throws RemoteException, InterruptedException{
        List<Integer> tempList = (List<Integer>)argumentList.get(1).getValue();
        Task t = new TaskCompose(space, new ArrayList<Argument>(), cont, tempList.size());
        List<Task> list = new ArrayList<>();
        for(int i : (List<Integer>)argumentList.get(1).getValue()){
            List<Integer> fixedList = new ArrayList<>();
            List<Integer> partialList = new ArrayList<>();
            for(int j : (List<Integer>)argumentList.get(0).getValue()){
                fixedList.add(j);
            }
            for(int a : (List<Integer>)argumentList.get(1).getValue()){
                if(a != i)
                    partialList.add(a);
            }
            fixedList.add(i);
            Argument argument0 = new Argument(fixedList, 0);
            Argument argument1 = new Argument(partialList, 1);
            List<Argument> newList = new ArrayList<>();
            newList.add(argument0);
            newList.add(argument1);
            list.add(new TaskTsp(space, newList));
        }
        return new SpawnResult(t, list);
    }

    @Override
    public Object generateArgument(){
        List<Integer> partialCityList = (List<Integer>)argumentList.get(1).getValue();
        // initial value for shortestTour and its distance.
        for(int i = 0; i < CITIES.length; i++)
            shortestTour.add(i);

        List<List<Integer>> allPermute = new ArrayList<>();
        iterate(partialCityList, 0, allPermute);
        for(List<Integer> tour : allPermute){
            List<Integer> newTour = new ArrayList<>(tour);
            newTour = addPrefix(newTour);
            double currentDistance = tourDistance(newTour);
            double shortestDistance = tourDistance(shortestTour);
            if(currentDistance < shortestDistance)
                shortestTour = newTour;
        }
        return shortestTour;
    }

    @Override
    public boolean needToCompute(){
        List<Integer> partialCityList = (List<Integer>)argumentList.get(1).getValue();
        return partialCityList.size() < 10;
    }

    private void iterate( List<Integer> permutation, int k, List<List<Integer>> allPermute)
    {
        for( int i = k; i < permutation.size(); i++ )
            {
                java.util.Collections.swap( permutation, i, k );
                iterate( permutation, k + 1, allPermute );
                java.util.Collections.swap( permutation, k, i );
            }
        if ( k == permutation.size() - 1 )
            {
                allPermute.add(new ArrayList(permutation));
            }
    }


    private List<Integer> addPrefix( List<Integer> partialTour )
    {
        for(int i : (List<Integer>)argumentList.get(0).getValue()){
            partialTour.add(0, i);
        }
        return partialTour;
    }

    @Override
    public JLabel viewResult(Object result)
    {
        List<Integer> cityList = (List<Integer>)result;
        Logger.getLogger( this.getClass().getCanonicalName() ).log( Level.INFO, "Tour: {0}", cityList.toString() );
        Integer[] tour = cityList.toArray( new Integer[0] );

        // display the graph graphically, as it were
        // get minX, maxX, minY, maxY, assuming they 0.0 <= mins
        double minX = CITIES[0][0], maxX = CITIES[0][0];
        double minY = CITIES[0][1], maxY = CITIES[0][1];
        for ( double[] cities : CITIES )
            {
                if ( cities[0] < minX )
                    minX = cities[0];
                if ( cities[0] > maxX )
                    maxX = cities[0];
                if ( cities[1] < minY )
                    minY = cities[1];
                if ( cities[1] > maxY )
                    maxY = cities[1];
            }

        // scale points to fit in unit square
        final double side = Math.max( maxX - minX, maxY - minY );
        double[][] scaledCities = new double[CITIES.length][2];
        for ( int i = 0; i < CITIES.length; i++ )
            {
                scaledCities[i][0] = ( CITIES[i][0] - minX ) / side;
                scaledCities[i][1] = ( CITIES[i][1] - minY ) / side;
            }

        final Image image = new BufferedImage( NUM_PIXALS, NUM_PIXALS, BufferedImage.TYPE_INT_ARGB );
        final Graphics graphics = image.getGraphics();

        final int margin = 10;
        final int field = NUM_PIXALS - 2*margin;
        // draw edges
        graphics.setColor( Color.BLUE );
        int x1, y1, x2, y2;
        int city1 = tour[0], city2;
        x1 = margin + (int) ( scaledCities[city1][0]*field );
        y1 = margin + (int) ( scaledCities[city1][1]*field );
        for ( int i = 1; i < CITIES.length; i++ )
            {
                city2 = tour[i];
                x2 = margin + (int) ( scaledCities[city2][0]*field );
                y2 = margin + (int) ( scaledCities[city2][1]*field );
                graphics.drawLine( x1, y1, x2, y2 );
                x1 = x2;
                y1 = y2;
            }
        city2 = tour[0];
        x2 = margin + (int) ( scaledCities[city2][0]*field );
        y2 = margin + (int) ( scaledCities[city2][1]*field );
        graphics.drawLine( x1, y1, x2, y2 );

        // draw vertices
        final int VERTEX_DIAMETER = 6;
        graphics.setColor( Color.RED );
        for ( int i = 0; i < CITIES.length; i++ )
            {
                int x = margin + (int) ( scaledCities[i][0]*field );
                int y = margin + (int) ( scaledCities[i][1]*field );
                graphics.fillOval( x - VERTEX_DIAMETER/2,
                                   y - VERTEX_DIAMETER/2,
                                   VERTEX_DIAMETER, VERTEX_DIAMETER);
            }
        return new JLabel( new ImageIcon( image ) );
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
