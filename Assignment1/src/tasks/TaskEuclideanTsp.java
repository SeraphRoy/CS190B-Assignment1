package tasks;

import api.Task;
import java.util.List;
import java.util.ArrayList;

public class TaskEuclideanTsp implements Task<List<Integer>>{
    private double[][] cityCo;

    public TaskEuclideanTsp(double[][] map){
        cityCo = new double[map.length][map[0].length];
        for(int i = 0; i < map.length; i++){
            for(int j = 0; j < map[i].length; j++){
                this.cityCo[i][j] = map[i][j];
            }
        }
    }

    public List<Integer> Execute(){
        ArrayList<Integer> cityNum = new ArrayList<>();
        for(int i = 0; i < cityCo.length; i++)
            cityNum.add(i);
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> solution = new ArrayList<>();
        int[] visited = new int[cityCo.length];

        HelpExecute(cityNum, 0, result, visited, solution);

        double min = Double.MAX_VALUE;
        double current = 0;
        solution.clear();
        for(ArrayList<Integer> item : result){
            for(int i = 0; i < item.size() -1 ; i++){
                current += Math.sqrt(Math.pow(cityCo[i+1][0]-cityCo[i][0], 2) + Math.pow(cityCo[i+1][1] - cityCo[i][1], 2));
            }
            current += Math.sqrt(Math.pow(cityCo[0][0]-cityCo[item.size()-1][0], 2) + Math.pow(cityCo[0][1] - cityCo[item.size()-1][1], 2));
            if (current < min){
                min = current;
                for(int i = 0; i < item.size(); i++){
                    solution.add(i);
                }
            }
            else{
                solution.clear();
            }
            current = 0;
        }
        return solution;
    }

    private void HelpExecute(ArrayList<Integer> cityNum, int step, ArrayList<ArrayList<Integer>> result, int[] visited, ArrayList<Integer> solution){
        if(step == cityNum.size()){
            result.add(solution);
            return;
        }

        for(int i = 0; i < cityNum.size(); i++){
            visited[i] = 1;
            solution.add(cityNum.get(i));
            HelpExecute(cityNum, step+1, result, visited, solution);
            solution.remove(solution.size()-1);
            visited[i] = 0;
        }
    }
}
