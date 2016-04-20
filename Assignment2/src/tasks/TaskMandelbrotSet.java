package tasks;

import api.Task;
import java.lang.Math;
import java.lang.Integer;

public class TaskMandelbrotSet implements Task<Integer[][]>, java.io.Serializable{
    private static final long serialVersionUID = 227L;
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

        public void Plus(ComplexNumber n){
            this.real += n.real;
            this.imaginary += n.imaginary;
        }

        public void Square(){
            double real = this.real;
            double imaginary = this.imaginary;

            this.real = Math.pow(real, 2) - Math.pow(imaginary, 2);
            this.imaginary = 2 * real * imaginary;
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
    public Integer[][] call(){
        Integer[][] count = new Integer[nPixels][nPixels];
        int num = 0;
        for(int i = 0; i < nPixels; i ++){
            for(int j = 0; j < nPixels; j++){
                ComplexNumber current = new ComplexNumber(i, j);
                while(current.Size() <= 2 && num < iterationLimit){
                    num += 1;
                    current.Square();
                    current.Plus(new ComplexNumber(i, j));;
                }
                count[i][j] = num;
                //System.out.print(num + " ");
                num = 0;
            }
            //System.out.print("\n");
        }
        return count;
    }
}

