package tasks;

import api.Task;
import java.lang.Math;

public class TaskMandelbrotSet implements Task{

    private double lowerLeftX;
    private double lowerLeftY;
    private double edgeLength;
    private int nPixels;
    private int iterationLimit;

    TaskMandelbrotSet(double lowerLeftX, double lowerLeftY, double edgeLength, int nPixels, int iterationLimit){
        this.lowerLeftX = lowerLeftX;
        this.lowerLeftY = lowerLeftY;
        this.edgeLength = edgeLength;
        this.nPixels = nPixels;
        this.iterationLimit = iterationLimit;
    }
    Interger[][] Execute(){
        Interger[][] count = new Interger[nPixels][nPixels];
        int num = 0;

        for(int i = 0; i < nPixels; i ++){
            for(int j = 0; j < nPixels; j++){
                ComplexNumber current = new ComplexNumber(i, j);
                while(current.Size() <= 2 && num < iterationLimit){
                    num++;
                    current = current.Square().Plus(current);
                }
                count[i][j] = num;
                num = 0;
            }
        }

    }
}

public class ComplexNumber{
    public double real;
    public double imaginary;

    ComplexNumber(){
        real = 0;
        imaginary = 0;
    }

    ComplexNumber(double real, double imaginary){
        this.real = real;
        this.imaginary = imaginary;
    }

    ComplexNumber(ComplexNumber n){
        this.real = n.real;
        this.imaginary = n.imaginary;
    }

    public ComplexNumber Plus(ComplexNumber n){
        return new ComplexNumber(this.real + n.real, this.imaginary + n.imaginary);
    }

    public ComplexNumber Square(){
        return new ComplexNumber(this.real ^ 2 - this.imaginary ^ 2, 2 * this.real * this.imaginary);
    }

    public double Size(){
        return Math.hypot(this.real, this.imaginary);
    }

}
