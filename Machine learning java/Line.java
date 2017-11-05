/**
 * Created by moshe on 08/06/2017.
 */
public class Line {
    double a;
    double b;
    double weight;
    int tag;

    public Line(double a, double b, double weight, int tag) {
        this.a = a;
        this.b = b;
        this.weight = weight;
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "Line{" +
                "a=" + a +
                ", b=" + b +
                ", weight=" + weight +
                ", tag=" + tag +
                '}';
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

}
