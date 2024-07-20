package airplane.g1;

import java.awt.geom.Line2D;
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
    private final int delayTimeThreshold = 30;
    private final int delayTimeThresholdStrict = 10;

    // Plane avoidance parameters
    private final int distanceThreshold = 15;
    private final int destinationThreshold = 5;

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

                // logger.info(String.format("hi %f %f", intersection[0], intersection[1]));

//                if (intersection.length != 0) {
//                    logger.info(String.format("%f %f", intersection[intersection.length - 2], intersection[intersection.length - 1]));
//                    logger.info(String.format("%d %d", i, j));
//                }

//                if (distance(intersection[0], intersection[1], start[i].getX(), start[i].getY()) < 5) {
//                    delay[j] += 2.5;
//                } else if (distance(intersection[0], intersection[1], start[j].getX(), start[j].getY()) < 5) {
//                    delay[j] += 2.5;
//                }

                // share endpoint
                if (sharesEndpoint(start[i].getX(), start[i].getY(), p1.getDestination().getX(), p1.getDestination().getY(),
                        start[j].getX(), start[j].getY(), p2.getDestination().getX(), p2.getDestination().getY())) {
                    double timeToCollision1 = distance(start[i].getX(), start[i].getY(), intersection[0], intersection[1]) + departure[i] + delay[i];
                    double timeToCollision2 = distance(start[j].getX(), start[j].getY(), intersection[0], intersection[1]) + departure[j] + delay[j];
                    logger.info(String.format("%f %f %f %f", intersection[0], intersection[1], timeToCollision1, timeToCollision2));
                    if (Math.abs(timeToCollision1 - timeToCollision2) < delayTimeThreshold) {
                        logger.info("hi");
                        delay[j] += delayTime; // Math.max(timeToCollision1, timeToCollision2);
                        distance[i][j] = -1;
                    }
                } else if (intersection[0] != Double.MAX_VALUE) {
                    double timeToCollision1 = distance(start[i].getX(), start[i].getY(), intersection[0], intersection[1]) + departure[i] + delay[i];
                    double timeToCollision2 = distance(start[j].getX(), start[j].getY(), intersection[0], intersection[1]) + departure[j] + delay[j];
                    // logger.info(String.format("%f %d %d %f %f %f", timeToCollision, i, j, intersection[0], intersection[1], distance(start[i].getX(), start[i].getY(), intersection[0], intersection[1]) + departure[i] + delay[i]));

                    // loggerer.info(String.format("%f %f", timeToCollision1, timeToCollision2));

                    if (Math.abs(timeToCollision1 - timeToCollision2) < delayTimeThreshold) {

                        if (Math.abs(timeToCollision1 - timeToCollision2) < delayTimeThresholdStrict) {
                            logger.info("1");
                            if (timeToCollision1 > timeToCollision2) {
                                delay[i] += delayTime;
                            } else {
                                delay[j] += delayTime;
                            }
                        } else if (timeToCollision2 - timeToCollision1 > delayTimeThresholdStrict) {
                            logger.info("2");
                            continue;
                        } else if (timeToCollision1 - timeToCollision2 > delayTimeThresholdStrict) {
                            logger.info(String.format("3 %d %d", i, j));
                            delay[i] += delayTime;
                        }

                        // logger.info("delay");
                        distance[i][j] = -1;
                    }
                }

                // set initial distance between planes
                double dist = distance(p1.getLocation().getX(), p1.getLocation().getY(),
                        p2.getLocation().getX(), p2.getLocation().getY());

                distance[i][j] = dist;
            }
        }

        for (int i = 0; i < planes.size(); i++) {
            logger.info(String.format("%d %f", i, delay[i]));
        }
    }

    @Override
    public double[] updatePlanes(ArrayList<Plane> planes, int round, double[] bearings) {

        // takes into account potential delays
        for (int i = 0; i < planes.size(); i++) {
            Plane p = planes.get(i);
            if (round >= departure[i] + delay[i] && bearings[i] != -2 && !departed[i]) {
                logger.info(String.format("%b %d",  safeLanding(planes, p), i));
                if (clearSky(planes, p) && safeLanding(planes, p)) {
                    bearings[i] = calculateBearing(p.getLocation(), p.getDestination());
                    departed[i] = true; // prevent sharp changes in bearing
                }
            }
        }

        double[] change = new double[planes.size()];

        // check distances between all pairs of planes
        for (int i = 0; i < planes.size(); i++) {
            for (int j = i + 1; j < planes.size(); j++) {
                Plane p1 = planes.get(i);
                Plane p2 = planes.get(j);

                // logger.info(String.format("start %d %d %f %f", i, j, p1.getBearing(), p2.getBearing()));

                if (round >= departure[i] && round >= departure[j] && (p1.getBearing() >= 0 && p2.getBearing() >= 0)) {

                    // logger.info(String.format("PLS ACTIVATE %d %d %f %f %f %f", i, j, start[i].getX(), start[i].getY(), start[j].getX(), start[j].getY()));
                    double dist = distance(p1.getLocation().getX(), p1.getLocation().getY(),
                            p2.getLocation().getX(), p2.getLocation().getY());
                    double distToDest1 = distance(p1.getLocation().getX(), p1.getLocation().getY(),
                            p1.getDestination().getX(), p1.getDestination().getY());
                    double distToDest2 = distance(p2.getLocation().getX(), p2.getLocation().getY(),
                            p2.getDestination().getX(), p2.getDestination().getY());

                    if (dist > distance[i][j] && distance[i][j] != -1) {
                        pass[i][j] = true;
                    }

                    if (pass[i][j]) {
                        change[i] = calculateBearing(p1.getLocation(), p1.getDestination()) - bearings[i];
                        change[j] = calculateBearing(p2.getLocation(), p2.getDestination()) - bearings[j];
                    }

                    if (distance[i][j] != -1) {
                        if ((dist <= distanceThreshold && !pass[i][j] && distToDest1 >= destinationThreshold && distToDest2 >= destinationThreshold) || dist <= 5) {
                            // logger.info(String.format("%d %d", i, j));
                            double angle = Math.abs(p1.getBearing() - p2.getBearing());
                            logger.info(String.format(" criss cross %d %d %f %b", i, j, angle, (angle <= 135 && angle >= 75) || (angle >= 160 && angle <= 165)));
                            if ((angle <= 135 && angle >= 75) || (angle >= 160 && angle <= 165)) {
                                // logger.info(String.format(" criss cross %d %d", i, j));
                                change[i] = (change[i] - 10) % 360;
                                change[j] = (change[j] - 10) % 360;
                            } else {
                                change[i] = (change[i] + 10) % 360;
                                change[j] = (change[j] + 10) % 360;
                            }
                        }
                    }

                    distance[i][j] = dist;
                }
            }
        }

        logger.info("-----");

        for (int i = 0; i < planes.size(); i++) {
            if (bearings[i] != -1 && bearings[i] != -2) {
                // logger.info(String.format("%f", change[i]));
                bearings[i] = adjustBearing(bearings[i], bearings[i] + change[i], 9.9);
                // logger.info(String.format("hi %f %d %f %f", bearings[i], i, planes.get(i).getX(), planes.get(i).getY()));
            }
            // change[i] = 0;
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
            return round(originalBearing + maxChange - 0.2) % 360;
        } else if (change <= -maxChange) {
            return round(originalBearing - maxChange + 0.2) % 360;
        } else {
            return round(newBearing);
        }
    }

    private double distance(double x1, double y1, double x2, double y2) {
        double xdist = x1 - x2;
        double ydist = y1 - y2;
        return Math.sqrt(xdist * xdist + ydist * ydist);
    }

    // determines whether there are nearby planes to start
    private boolean clearSky(ArrayList<Plane> planes, Plane plane) {
        for (Plane p : planes) {
            if (p != plane) {
                if (p.getX() == plane.getX() && p.getY() == plane.getY()) {
                    if (plane.getDepartureTime() >= p.getDepartureTime()) {
                        return false;
                    }
                }
                if (p.getBearing() == -1 || p.getBearing() == -2) {
                    continue;
                }
                if (distance(plane.getLocation().getX(), plane.getLocation().getY(), p.getLocation().getX(), p.getLocation().getY()) <= 30) {
                    return false;
                }
            }
        }
        return true;
    }

    // determines whether plane is safe to land
    // looks at all other flying planes and determines whether intersection is possible near destination
    private boolean safeLanding(ArrayList<Plane> planes, Plane plane) {
        for (Plane p : planes) {
            if (p != plane && p.getBearing() >= 0) {
//                if (p.getBearing() == -1 || p.getBearing() == -2) {
//                    continue;
//                }
//                double[] intersection = intersects(plane.getLocation().getX(), plane.getLocation().getY(), plane.getDestination().getX(), plane.getDestination().getY(),
//                        p.getLocation().getX(), p.getLocation().getY(), p.getDestination().getX(), p.getDestination().getY());
//                if (p.getDestination().equals(plane.getDestination()) && distance(plane.getDestination().getX(), plane.getDestination().getY(), intersection[0], intersection[1]) < 2.5 && p.getDestination().equals(plane.getDestination())) {
//                    logger.info(String.format("jsdf %f", distance(plane.getDestination().getX(), plane.getDestination().getY(), intersection[0], intersection[1])));
//                    return false;
//                }
                if (p.getDestination().equals(plane.getDestination())) {
                    return false;
                }
            }
        }
        return true;
    }

//    private boolean safeLanding(ArrayList<Plane> planes, Plane plane) {
//        for (Plane p : planes) {
//            if (p != plane) {
//                if (p.getBearing() >= 0) {
//                    if (distance(plane.getLocation().getX(), plane.getLocation().getY(), p.getLocation().getX(), p.getLocation().getY()) <= 30) {
//                        return false;
//                    }
//                }
//            }
//        }
//    }

    // determines whether coordinate pairs (ax, ay), (bx, by) and (cx, cy), (dx, dy)
    // share an endpoint
    private boolean sharesEndpoint(double ax, double ay, double bx, double by,
                                             double cx, double cy, double dx, double dy) {
        return (ax == cx && ay == cy) || (ax == dx && ay == dy) || (bx == cx && by == cy) || (bx == dx && by == dy);
    }

    // determines whether coordinate pairs (ax, ay), (bx, by) and (cx, cy), (dx, dy)
    // are the same line
    private boolean sameLine(double ax, double ay, double bx, double by,
                             double cx, double cy, double dx, double dy) {
        return ((ax == cx && ay == cy) && (bx == dx && by == dy)) || ((bx == cx && by == cy) && (ax == dx && ay == dy));
    }

    // determines whether two line segments given by two pairs of coordinates would intersect
    // returns point of intersection if so; otherwise, empty double[]
    // considering coordinate pairs (ax, ay), (bx, by) and (cx, cy), (dx, dy)
    private double[] intersects(double ax, double ay, double bx, double by,
                              double cx, double cy, double dx, double dy) {

        Line2D line1 = new Line2D.Double(ax, ay, bx, by);
        Line2D line2 = new Line2D.Double(cx, cy, dx, dy);

        if (line1.intersectsLine(line2)) {
            double a1 = by - ay;
            double b1 = ax - bx;
            double c1 = a1 * ax + b1 * ay;

            double a2 = dy - cy;
            double b2 = cx - dx;
            double c2 = a2 * cx + b2 * cy;

            double determinant = a1 * b2 - a2 * b1;

            if (determinant == 0) {
                if (sameLine(ax, ay, bx, by, cx, cy, dx, dy)) {
                    return new double[]{Double.MAX_VALUE, Double.MAX_VALUE};
                } else if (sharesEndpoint(ax, ay, bx, by, cx, cy, dx, dy)) {
                    if ((ax == cx && ay == cy) || (ax == dy && ay == dy)) {
                        return new double[]{ax, ay};
                    } else {
                        return new double[]{bx, by};
                    }
                }
            } else {
                double x = (b2 * c1 - b1 * c2) / determinant;
                double y = (a1 * c2 - a2 * c1) / determinant;

                double[] point = new double[2];
                point[0] = x;
                point[1] = y;
                return point;
            }
        } else {
            return new double[]{Double.MAX_VALUE, Double.MAX_VALUE};
        }

        return new double[]{Double.MAX_VALUE, Double.MAX_VALUE};

//        if ((ax == cx && ay == cy)) { //  || (ax == dx && ay == dy)
//            return new double[]{-1, ax, ay};
//        }
//        if ((bx == dx && by == dy)) { // (bx == cx && by == cy) ||
//            return new double[]{-1, bx, by};
//        }
//
//        double a1 = by - ay;
//        double b1 = ax - bx;
//        double c1 = a1 * ax + b1 * ay;
//
//        double a2 = dy - cy;
//        double b2 = cx - dx;
//        double c2 = a2 * cx + b2 * cy;
//
//        double determinant = a1 * b2 - a2 * b1;
//
//        if (determinant == 0) {
//            return new double[0];
//        } else {
//            double x = (b2 * c1 - b1 * c2) / determinant;
//            double y = (a1 * c2 - a2 * c1) / determinant;
//
//            // checks whether intersection is on both line segments
//            if (Math.min(ax, bx) <= x && x <= Math.max(ax, bx) &&
//                    Math.min(ay, by) <= y && y <= Math.max(ay, by) &&
//                    Math.min(cx, dx) <= x && x <= Math.max(cx, dx) &&
//                    Math.min(cy, dy) <= y && y <= Math.max(cy, dy)) {
//                double[] point = new double[2];
//                point[0] = x;
//                point[1] = y;
//                return point;
//            } else {
//                return new double[0];
//            }
//        }
    }
}
