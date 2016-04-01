package tasks;

import api.Task;
import java.lang.Math;
import java.lang.Integer;

public class TaskMandelbrotSet implements Task<Integer[][]>{

    private double lowerLeftX;
    private double lowerLeftY;
    private double edgeLength;
    private int nPixels;
    private int iterationLimit;

    private class ComplexNumber{
        public double real;
        public double imaginary;

        ComplexNumber(){
            real = 0;
            imaginary = 0;
        }

        ComplexNumber(double real, double imaginary){
            this.real = real * (edgeLength/nPixels) - Math.abs(lowerLeftX);
            this.imaginary = imaginary * (edgeLength/nPixels) - Math.abs(lowerLeftY);
        }

        ComplexNumber(ComplexNumber n){
            this.real = n.real;
            this.imaginary = n.imaginary;
        }

        public ComplexNumber Plus(ComplexNumber n){
            return new ComplexNumber(this.real + n.real, this.imaginary + n.imaginary);
        }

        public ComplexNumber Square(){
            return new ComplexNumber(Math.pow(this.real, 2) - Math.pow(this.imaginary, 2), 2 * this.real * this.imaginary);
        }

        public double Size(){
            return Math.hypot(this.real, this.imaginary);
        }

    }


    public TaskMandelbrotSet(double lowerLeftX, double lowerLeftY, double edgeLength, int nPixels, int iterationLimit){
        this.lowerLeftX = lowerLeftX;
        this.lowerLeftY = lowerLeftY;
        this.edgeLength = edgeLength;
        this.nPixels = nPixels;
        this.iterationLimit = iterationLimit;
    }
    public Integer[][] Execute(){ // 
        Integer[][] count = new Integer[nPixels][nPixels];
        int num = 0;

        for(int i = 0; i < nPixels; i ++){
            for(int j = 0; j < nPixels; j++){
                ComplexNumber current = new ComplexNumber(i, j);
                while(current.Size() <= 2 && num < iterationLimit){
                    num++;
                    current = current.Square().Plus(new ComplexNumber(i, j));
                }
                System.out.print(current.Size() + " ");
                count[i][j] = num;
                num = 0;
            }
            System.out.print("\n");
        }
        return count;
    }
}

