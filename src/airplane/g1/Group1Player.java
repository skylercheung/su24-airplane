package airplane.g1;

import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import airplane.sim.Plane;
import airplane.sim.Player;

public class Group1Player extends Player {

    private Logger logger = Logger.getLogger(this.getClass());
//    // private boolean pass = false;
//    private double preDist = Double.MAX_VALUE;

    private final int numPlanes = 100;

    Point2D[] start = new Point2D[numPlanes];
    double[] delay = new double[numPlanes];
    double[] departure = new double[numPlanes];
    double[][] distance = new double[numPlanes][numPlanes];
    boolean[][] pass = new boolean[numPlanes][numPlanes];
    boolean[] departed = new boolean[numPlanes];

    // Delay parameters
    private final int delayTime = 15;
    private final int delayTimeThreshold = 15;

    // Plane avoidance parameters
    private final int distanceThreshold = 15;
    private final int destinationThreshold = 25;

    @Override
    public String getName() {
        return "Group 1 Player";
    }

    @Override
    public void startNewGame(ArrayList<Plane> planes) {
        logger.info("Starting new game!");

        for (int i = 0; i < distance.length; i++) {
            for (int j = 0; j < distance[i].length; j++) {
                distance[i][j] = Double.MAX_VALUE;
                pass[i][j] = false;
                departed[i] = false;
            }
        }

        // calculating potential delays between all pairs of planes
        for (int i = 0; i < planes.size(); i++) {
            Plane p = planes.get(i);
            departure[i] = p.getDepartureTime();
            if (p.getBearing() == -1) {
                start[i] = p.getLocation();
            }
        }

        for (int i = 0; i < planes.size(); i++) {
            for (int j = i + 1; j < planes.size(); j++) {
                Plane p1 = planes.get(i);
                Plane p2 = planes.get(j);

                double[] intersection = intersects(start[i].getX(), start[i].getY(), p1.getDestination().getX(), p1.getDestination().getY(),
                        start[j].getX(), start[j].getY(), p2.getDestination().getX(), p2.getDestination().getY());

                if (intersection.length != 0) {
                    double timeToCollision = Math.abs(distance(start[i].getX(), start[i].getY(), intersection[0], intersection[1]) -
                            distance(start[j].getX(), start[j].getY(), intersection[0], intersection[1])) + Math.abs(departure[i] - departure[j]);
                    if (timeToCollision < delayTimeThreshold) {
                        delay[j] += delayTime;
                        distance[i][j] = -1;
                    }
                }
            }
        }
    }

    @Override
    public double[] updatePlanes(ArrayList<Plane> planes, int round, double[] bearings) {

        // takes into account potential delays
        for (int i = 0; i < planes.size(); i++) {
            Plane p = planes.get(i);
            if (round >= departure[i] + delay[i] && bearings[i] != -2 && !departed[i]) {
                bearings[i] = calculateBearing(p.getLocation(), p.getDestination());
                departed[i] = true; // prevent sharp changes in bearing
            }
        }

        double[] change = new double[planes.size()];

        // check distances between all pairs of planes
        for (int i = 0; i < planes.size(); i++) {
            for (int j = i + 1; j < planes.size(); j++) {
                Plane p1 = planes.get(i);
                Plane p2 = planes.get(j);

                if (round >= departure[i] && round >= departure[j] && (p1.getBearing() >= 0 && p2.getBearing() >= 0)) {
                    double dist = distance(p1.getLocation().getX(), p1.getLocation().getY(),
                            p2.getLocation().getX(), p2.getLocation().getY());
                    double distToDest = distance(p1.getLocation().getX(), p1.getLocation().getY(),
                            p1.getDestination().getX(), p1.getDestination().getY());

                    if (dist > distance[i][j]) {
                        pass[i][j] = true;
                    }

                    if (pass[i][j]) {
                        change[i] = calculateBearing(p1.getLocation(), p1.getDestination()) - bearings[i];
                        change[j] = calculateBearing(p2.getLocation(), p2.getDestination()) - bearings[j];
                    }

                    if (distance[i][j] != -1) {
                        if ((dist <= distanceThreshold && !pass[i][j] && distToDest >= destinationThreshold) || dist <= 5) {
                            change[i] = (change[i] + 10) % 360;
                            change[j] = (change[j] + 10) % 360;
                        }
                    }

                    distance[i][j] = dist;
                }
            }
        }

        for (int i = 0; i < planes.size(); i++) {
            if (bearings[i] != -1 && bearings[i] != -2) {
                bearings[i] = adjustBearing(bearings[i], bearings[i] + change[i], 10);
            }
        }
        return bearings;
    }

    // in case of rounding errors
    private double round(double input) {
        return Math.round(input * 100.0) / 100.0;
    }

    private double adjustBearing(double originalBearing, double newBearing, double maxChange) {
        double change = newBearing - originalBearing;

        if (change >= maxChange) {
            return round(originalBearing + maxChange - 0.1) % 360;
        } else if (change <= -maxChange) {
            return round(originalBearing - maxChange + 0.1) % 360;
        } else {
            return round(newBearing);
        }
    }

    private double distance(double x1, double y1, double x2, double y2) {
        double xdist = x1 - x2;
        double ydist = y1 - y2;
        return Math.sqrt(xdist * xdist + ydist * ydist);
    }

    // determines whether two line segments given by two pairs of coordinates would intersect
    // returns point of intersection if so; otherwise, empty double[]
    private double[] intersects(double ax, double ay, double bx, double by,
                              double cx, double cy, double dx, double dy) {

        if ((ax == cx && ay == cy) || (ax == dx && ay == dy)) {
            return new double[]{ax, ay};
        }
        if ((bx == cx && by == cy) || (bx == dx && by == dy)) {
            return new double[]{bx, by};
        }

        double a1 = by - ay;
        double b1 = ax - bx;
        double c1 = a1 * ax + b1 * ay;

        double a2 = dy - cy;
        double b2 = cx - dx;
        double c2 = a2 * cx + b2 * cy;

        double determinant = a1 * b2 - a2 * b1;

        if (determinant == 0) {
            return new double[0];
        } else {
            double x = (b2 * c1 - b1 * c2) / determinant;
            double y = (a1 * c2 - a2 * c1) / determinant;

            // checks whether intersection is on both line segments
            if (Math.min(ax, bx) <= x && x <= Math.max(ax, bx) &&
                    Math.min(ay, by) <= y && y <= Math.max(ay, by) &&
                    Math.min(cx, dx) <= x && x <= Math.max(cx, dx) &&
                    Math.min(cy, dy) <= y && y <= Math.max(cy, dy)) {
                double[] point = new double[2];
                point[0] = x;
                point[1] = y;
                return point;
            } else {
                return new double[0];
            }
        }
    }
}
