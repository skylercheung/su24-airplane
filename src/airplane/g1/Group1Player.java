package airplane.g1;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import airplane.sim.Plane;
import airplane.sim.Player;

public class Group1Player extends Player {

    private Logger logger = Logger.getLogger(this.getClass());
    private boolean pass = false;
    private double preDist = Double.MAX_VALUE;

    Point2D start1 = null;
    Point2D start2 = null;

    // potential delay
    double delay1 = 0;
    double delay2 = 0;

    @Override
    public String getName() {
        return "Group 1 Player";
    }

    @Override
    public void startNewGame(ArrayList<Plane> planes) {
        logger.info("Starting new game!");
        pass = false;
        preDist = Double.MAX_VALUE;
    }

    @Override
    public double[] updatePlanes(ArrayList<Plane> planes, int round, double[] bearings) {

        Plane p1 = planes.get(0);
        Plane p2 = planes.get(1);

        int departureTime1 = p1.getDepartureTime();
        int departureTime2 = p2.getDepartureTime();

        if (p1.getBearing() == -1) {
            start1 = p1.getLocation();
        }
        if (p2.getBearing() == -1) {
            start2 = p2.getLocation();
        }

        double bearing1 = 0;
        double bearing2 = 0;

        double[] intersection = intersects(start1.getX(), start1.getY(), p1.getDestination().getX(), p1.getDestination().getY(),
                start2.getX(), start2.getY(), p2.getDestination().getX(), p2.getDestination().getY());

        // calculate whether planes collide
        if (intersection[0] != -1) {
            double distanceToCollision = Math.abs(distance(start1.getX(), start1.getY(), intersection[0], intersection[1]) -
                    distance(start2.getX(), start2.getY(), intersection[0], intersection[1]));
            if (distanceToCollision < 10) {
                delay2 = 10;
            }
        }

        // Check if the plane is in the air
        if (round >= departureTime1 + delay1 && bearings[0] != -2) {
            bearing1 = calculateBearing(p1.getLocation(), p1.getDestination());
            bearings[0] = bearing1;
        }
        if (round >= departureTime2 + delay2 && bearings[1] != -2) {
            bearing2 = calculateBearing(p2.getLocation(), p2.getDestination());
            bearings[1] = bearing2;
        }
        /*
        // Check the distance between the two planes when they are both in the air
        if (round >= departureTime1 && round >= departureTime2) {
            double distance = distance(p1.getLocation().getX(), p1.getLocation().getY(),
                    p2.getLocation().getX(), p2.getLocation().getY());

            // If the distance is increasing, then pass the other plane
            if (distance > preDist) {
                pass = true;
            }

            // if they are not passing each other and distance is less than 40, keep increasing the bearing
            // and if they have passed each other and are 5 units apart, do not change the bearing
            if ((distance <= 50 && !pass) || distance <= 5) {
                bearing1 = (bearing1 + 10) % 360;
                bearing2 = (bearing2 + 10) % 360;
            }

            preDist = distance;
        }

        // Adjust the bearing
        if(bearings[0] != -1){
            bearing1 = adjustBearing(bearings[0], bearing1, 10);
        }
        if(bearings[1] != -1){
            bearing2 = adjustBearing(bearings[1], bearing2, 10);
        }
        */

        return bearings;
    }

    private double adjustBearing(double originalBearing, double newBearing, double maxChange) {
        double change = newBearing - originalBearing;

        if (change > maxChange) {
            return originalBearing + maxChange;
        } else if (change < -maxChange) {
            return originalBearing - maxChange;
        } else {
            return newBearing;
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
