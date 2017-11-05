import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Alon Groisman
 * 
 */
public class Ex3 {
    public static void main(String[] args) {
        // Question 1
        ArrayList<Point> points = readPersonsFromFile();
        Line bruteForceLine = bruteForce2line(points);
        System.out.println("bruteForce Line mistake: ");
        System.out.println(bruteForceLine.getWeight());
        System.out.println("line: " + bruteForceLine);
        System.out.println("---------------------------");

        // Question 2
        // learn from all points and test all points
        adaBoostTest(5, points, points);
        // learn from half point and test all point
        List<Point>[] testTrain = getTestTrain(points, 0.5, 0.5);
        adaBoostTest(5, testTrain[0], points);
        // learn from half point and test the other
        adaBoostTest(5, testTrain[0], testTrain[1]);
    }

    /**
     * Adaboost run test for precision
     *
     * @param iteration number of iteration to run Adaboost
     */
    public static void adaBoostTest(int iteration, List<Point> train, List<Point> test) {
        // read from sample file
        ArrayList<Line> lines = adaBoost(train, iteration);
        System.out.println("Adaboost Lines:");
        for (Line l : lines) {
            System.out.println(l);
        }

        int mistake = 0;
        for (Point point : test) {
            double x = point.getX();
            double y = point.getY();
            int sign = point.getSign();
            double totalWeight = 0.0;
            for (Line line : lines) {
                double a = line.getA();
                double b = line.getB();
                double eq = y - a * x;
                double predict = (eq > b) ? 1 : -1;
                predict = predict * line.getTag();
                totalWeight += predict * line.getWeight();
            }
            if (totalWeight > 0 && sign == -1 || totalWeight < 0 && sign == 1) {
                mistake++;
            }
        }
        System.out.println("Adaboost Mistake:\n" + mistake);
    }

    /**
     * Adaboost algorithm implementation
     * @param points
     * @param iteration
     * @return
     */
    public static ArrayList<Line> adaBoost(List<Point> points, int iteration) {
        ArrayList<Line> lines = new ArrayList<>();

        for (Point p : points) {
            p.setWeight(p.getWeight() / points.size());
        }

        for (int i = 0; i < iteration; i++) {

            Line line = bruteForce2line(points);
            lines.add(line);

            double eT = line.getWeight();
            double aT = 0.5 * Math.log((1 - eT) / eT);
            line.setWeight(aT);

            reCalculatePointsWeights(points, line);

        }
        return lines;
    }

    /**
     * set new weight to points (consider the line tag)
     * as follow at Adaboost Algorithm
     * @param points
     * @param line
     */
    private static void reCalculatePointsWeights(List<Point> points, Line line) {
        double weights = 0.0;
        for (Point point : points) {
            double eq = point.getY() - line.getA() * point.getX();
            int predict = (eq > line.getB()) ? 1 : -1;
            predict *= line.getTag(); // consider in line sign
            if (predict != point.getSign()) {
                double weight = point.getWeight() * Math.pow(Math.E, line.getWeight());
                point.setWeight(weight);
            } else {
                double weight = point.getWeight() * Math.pow(Math.E, -line.getWeight());
                point.setWeight(weight);
            }
            weights += point.getWeight();
        }

        for (Point point : points) {
            double weight = point.getWeight() / weights;
            point.setWeight(weight);
        }
    }

    /**
     * read from sample file
     * @return
     */
    private static ArrayList<Point> readPersonsFromFile() {
        ArrayList<Point> points = new ArrayList<>();
        try {
            File file = new File("HC_Body_Temperature");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Scanner s = new Scanner(line);
                double x = s.nextDouble();
                int tag = s.nextInt();
                double y = s.nextDouble();
                Point p = new Point(x, y, tag);
                points.add(p);
            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return points;
    }


    /**
     * Question 1 + 2
     * brute force algorithm implementation for free points
     * @param points
     * @return
     */
    public static Line bruteForce2line(List<Point> points) {
        double minMistake = Double.MAX_VALUE;
        Line minLine = null;

        for (int i = 0; i < points.size(); i++) {
            Point p1 = points.get(i);
            for (int j = i + 1; j < points.size(); j++) {
                Point p2 = points.get(j);

                Line line;
                Line line1 = calculateLineBet2Points(p1, p2, 1);
                Line line2 = calculateLineBet2Points(p1, p2, -1);

                double mistake1 = getMistake(line1, points);
                double mistake2 = getMistake(line2, points);

                double mistake = 0.0;

                if (mistake1 < mistake2) {
                    mistake = mistake1;
                    line = line1;
                } else {
                    mistake = mistake2;
                    line = line2;
                }

                if (mistake < minMistake) {
                    minMistake = mistake;
                    minLine = line;
                }
            }
        }
        return new Line(minLine.getA(), minLine.getB(), minMistake, minLine.getTag());
    }


    private static Line calculateLineBet2Points(Point p1, Point p2, int tag) {
        double x1 = p1.getX();
        double y1 = p1.getY();
        double x2 = p2.getX();
        double y2 = p2.getY();
        double a = (y1 - y2) / (x1 - x2);
        double b = -a * x1 + y1;

        return new Line(a, b, 1, tag);
    }

    private static double getMistake(Line line, List<Point> points) {
        double mistake = 0;
        for (Point point : points) {
            double eq = point.getY() - line.getA() * point.getX();
            int predict = (eq > line.getB()) ? 1 : -1;
            predict = predict * line.getTag();
            if (predict != point.getSign()) {
                mistake += point.getWeight();
            }
        }
        return mistake;
    }


    private static int[] randomIndexArray(int range) {
        int[] A = new int[range];
        for (int i = 0; i < range; i++) {
            A[i] = i;
        }
        for (int i = 0; i < range; i++) {
            int random = (int) (Math.random() * range);
            int temp = A[i];
            A[i] = A[random];
            A[random] = temp;
        }
        return A;
    }


    private static List<Point>[] getTestTrain(List<Point> points, double trainParm, double testParm) {
        int n = points.size();
        int trainSize = (int) Math.ceil(n * trainParm);
        int testSize = (int) Math.ceil(n * testParm);
        int[] randomIndexArray = randomIndexArray(points.size());
        List<Point> train = new ArrayList<>();
        List<Point> test = new ArrayList<>();

        int k = 0;
        for (int i = 0; i < trainSize; i++) {
            train.add(points.get(randomIndexArray[k++]));
        }
        for (int i = 0; i < testSize; i++) {
            test.add(points.get(randomIndexArray[k++]));
        }

        return new List[]{train, test};
    }

}
