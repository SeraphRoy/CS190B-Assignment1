package tasks;

import api.Task;
import java.util.List;
import java.util.ArrayList;

public class TaskEuclideanTsp implements Task<List<Integer>>, java.io.Serializable{
    private static final long serialVersionUID = 227L;

    private double[][] cityCo;
    public int fixNum = 1;
    public int cityNum;

    public TaskEuclideanTsp(double[][] map){
        cityCo = new double[map.length][map[0].length];
        for(int i = 0; i < map.length; i++){
            for(int j = 0; j < map[i].length; j++){
                this.cityCo[i][j] = map[i][j];
            }
        }
        cityNum = map.length;
    }
    public List<Task> splitTasks(){
        List<Task> list = new ArrayList<Task>();
        double[][] cities = new double[cityCo.length][cityCo[0].length];
        for(int i = 0; i < cities.length; i++){
            for(int j = 0; j < cities[i].length; j++)
                cities[i][j] = cityCo[i][j];
        }
        for(int i = 1; i < cities.length; i++){
            double tempX = cities[1][0];
            double tempY = cities[1][1];
            cities[1][0] = cities[i][0];
            cities[1][1] = cities[i][1];
            cities[i][0] = tempX;
            cities[i][1] = tempY;

            TaskEuclideanTsp task = new TaskEuclideanTsp(cities);
            task.fixNum = 2;
            list.add(task);
        }
        return list;
    }

    public int getDistance(List<Integer> item){
        int current = 0;
        for(int i = 0; i < item.size() -1 ; i++){
            current += Math.sqrt(Math.pow(cityCo[item.get(i+1)][0]-cityCo[item.get(i)][0], 2) + Math.pow(cityCo[item.get(i+1)][1] - cityCo[item.get(i)][1], 2));
        }
        current += Math.sqrt(
                             Math.pow(cityCo[item.get(0)][0]-cityCo[item.get(item.size()-1)][0], 2)
                             + Math.pow(cityCo[item.get(0)][1] - cityCo[item.get(item.size()-1)][1], 2)
                             );
        return current;
    }
    public List<Integer> call(){
        int[] cityNum = new int[cityCo.length];
        for(int i = 0; i < cityCo.length; i++)
            cityNum[i] = i;
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();

        permute(cityNum, fixNum, result);
        double min = Double.MAX_VALUE;
        double current = 0;

        ArrayList<Integer> solution = new ArrayList<>();

        for(ArrayList<Integer> item : result){
            current = getDistance(item);
            if (current < min){
                min = current;
                solution.clear();
                for(int i = 0; i < item.size(); i++){
                    solution.add(item.get(i));
                }
            }
            current = 0;
        }
        return (List<Integer>) solution;
    }

    private void permute(int[] num, int start, ArrayList<ArrayList<Integer>> result) {

        if (start >= num.length) {
            ArrayList<Integer> item = convertArrayToList(num);
            result.add(item);

        }

        for (int j = start; j <= num.length - 1; j++) {
            swap(num, start, j);
            permute(num, start + 1, result);
            swap(num, start, j);

        }

    }

    private ArrayList<Integer> convertArrayToList(int[] num) {
        ArrayList<Integer> item = new ArrayList<Integer>();
        for (int h = 0; h < num.length; h++) {
            item.add(num[h]);

        }
        return item;

    }

    private void swap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;

    }
}
