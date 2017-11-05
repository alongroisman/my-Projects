/**
 * Created by Alon on 08/06/2017.
 */
public class Point {
    private double X,Y;
    private double weight;
    private int sign;

    public Point(double x, double y, int sign) {
        X = x;
        Y = y;
        this.weight = 1.0;
        this.sign = sign;
        if(sign != 1){
            this.sign= -1;
        }
    }

    @Override
    public String toString() {
        return "Point{" +
                "X=" + X +
                ", Y=" + Y +
                ", weight=" + weight +
                ", sign=" + sign +
                '}';
    }

    public double getX() {
        return X;
    }

    public void setX(double x) {
        X = x;
    }

    public double getY() {
        return Y;
    }

    public void setY(double y) {
        Y = y;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getSign() {
        return sign;
    }

    public void setSign(int sign) {
        this.sign = sign;
    }
}
