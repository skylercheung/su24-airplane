package airplane.g1delay;

import airplane.sim.Plane;
import airplane.sim.Player;
import airplane.sim.SimulationResult;
import org.apache.log4j.Logger;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Group1PlayerDelay extends Player {

    private Logger logger = Logger.getLogger(this.getClass());

    private final int numPlanes = 1000;

    // stores whether planes are safe to depart
    boolean[] safe = new boolean[numPlanes];

    Point2D[] start = new Point2D[numPlanes];
    double[] delay = new double[numPlanes];
    double[] departure = new double[numPlanes];
    double[][] distance = new double[numPlanes][numPlanes];
    boolean[][] pass = new boolean[numPlanes][numPlanes];
    boolean[] departed = new boolean[numPlanes];

    // Delay parameters
    private final double delayTime = 20;
    private final double delayTimeThreshold = 10;

    private final double safeDistance = 10;
    private final double bearingRange = 15;

    //--------

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

        for (int i = 0; i < planes.size(); i++) {
            Plane p = planes.get(i);
            start[i] = p.getLocation();
            departure[i] = p.getDepartureTime();
            logger.info("running");
            SimulationResult sim = startSimulation(planes, 0);
            logger.info("done");
            logger.info(sim.getReason());
            safe[i] = sim.isSuccess();
            logger.info(String.format("asifjads;fh %b", safe[i]));
        }

        /*
        * needs to handle case in which two planes depart at same time
        * */

//        for (int i = 0; i < planes.size(); i++) {
//            for (int j = i + 1; j < planes.size(); j++) {
//                Plane p1 = planes.get(i);
//                Plane p2 = planes.get(j);
//
//                // plane with same departing airports
//                if (start[i].equals(start[j])) {
//                    delay[j] += 10;
//                    logger.info("type 3");
//                } else {
//
//                    // double case
//                    if (sameLine(start[i].getX(), start[i].getY(), p1.getDestination().getX(), p1.getDestination().getY(),
//                            start[j].getX(), start[j].getY(), p2.getDestination().getX(), p2.getDestination().getY())) {
//                        double dist = distance(start[i].getX(), start[i].getY(), p1.getDestination().getX(), p1.getDestination().getY());
//                        delay[i] += dist;
//                        logger.info("type 1");
//                    }
//
//                    // plane arriving at airport when another leaves
//                    if (p1.getDestination().getX() == start[j].getX() && p1.getDestination().getY() == start[j].getY()
//                        && !(sameLine(start[i].getX(), start[i].getY(), p1.getDestination().getX(), p1.getDestination().getY(),
//                            start[j].getX(), start[j].getY(), p2.getDestination().getX(), p2.getDestination().getY()))) {
//                        double timeOfArrival = distance(start[i].getX(), start[i].getY(), p1.getDestination().getX(), p1.getDestination().getY()) + departure[i] + delay[i];
//                        if (departure[j] + delay[j] - timeOfArrival <= 2.5) {
//                            delay[j] += 10;
//                            logger.info("type 2");
//                        }
//                    }
//
//                    // plane flying by airport when another departs
//                    Line2D path1 = new Line2D.Double(start[i], p1.getDestination());
//                    double distFlyBy1 = distance(start[i].getX(), start[i].getY(), start[j].getX(), start[j].getY());
//                    if (path1.ptLineDist(start[j]) <= 10 && (distFlyBy1 + departure[i] + delay[i] - departure[j] - delay[j]) <= 5) {
//                        delay[j] += 10;
//                        logger.info("type 4");
//                    }
//
//                    // plane flying by airport when another lands
//                    Line2D path2 = new Line2D.Double(start[j], p2.getDestination());
//                    double distFlyBy2 = distance(start[j].getX(), start[j].getY(), p1.getDestination().getX(), p1.getDestination().getY());
//                    double addDist = distance(start[i].getX(), start[j].getY(), p1.getDestination().getX(), p1.getDestination().getY());
//                    if (path2.ptLineDist(p1.getDestination()) <= 10 && (addDist + departure[i] + delay[i] - distFlyBy2 - departure[j] - delay[j]) <= 5) {
//                        delay[j] += 10;
//                        logger.info("type 5");
//                    }
//
//                    double[] intersection = intersects(start[i].getX(), start[i].getY(), p1.getDestination().getX(), p1.getDestination().getY(),
//                            start[j].getX(), start[j].getY(), p2.getDestination().getX(), p2.getDestination().getY());
//                    double timeToCollision1 = distance(start[i].getX(), start[i].getY(), intersection[0], intersection[1]) + departure[i] + delay[i];
//                    double timeToCollision2 = distance(start[j].getX(), start[j].getY(), intersection[0], intersection[1]) + departure[j] + delay[j];
//
//                    double bearingDiff = Math.abs(calculateBearing(p1.getLocation(), p1.getDestination()) - calculateBearing(p2.getLocation(), p2.getDestination()));
//                    // need condition within similar distance at similar time
//                    if (Math.abs(timeToCollision1 - timeToCollision2) <= delayTimeThreshold) {
//                        delay[j] += delayTime;
//                        logger.info("type 6");
//                    }
    //                else if ((bearingDiff <= 180 + bearingRange && bearingDiff >= 180 - bearingRange)
    //                    && (Math.abs(start[i].getX() - start[j].getX()) <= 10) || (Math.abs(start[i].getY() - start[j].getY()) <= 10)
    //                    || (Math.abs(p1.getDestination().getX() - p2.getDestination().getX()) <= 10) || (Math.abs(p1.getDestination().getY() - p2.getDestination().getY()) <= 10)
    //                    && distance(start[i].getX(), start[i].getY(), p1.getDestination().getX(), p1.getDestination().getY()) + departure[i] + delay[i] >= departure[j] + delay[j]) {
    //                    // logger.info(String.format("ayoooo %d %d", i, j));
    //                    double timeDist1 = distance(start[i].getX(), start[i].getY(), p1.getDestination().getX(), p1.getDestination().getY()) + departure[i] + delay[i];
    //                    double timeDist2 = distance(start[j].getX(), start[j].getY(), p2.getDestination().getX(), p2.getDestination().getY()) + departure[j] + delay[j];
    //                    if (timeDist1 >= timeDist2) {
    //                        double addDelay = distance(start[i].getX(), start[i].getY(), p1.getDestination().getX(), p1.getDestination().getY()) + Math.abs(delay[i] - delay[j]) + Math.abs(departure[i] - departure[j]);
    //                        delay[j] += addDelay;
    //                    } else {
    //                        double addDelay = distance(start[j].getX(), start[j].getY(), p2.getDestination().getX(), p2.getDestination().getY()) + Math.abs(delay[i] - delay[j]) + Math.abs(departure[i] - departure[j]);
    //                        delay[i] += addDelay;
    //                    }
    //                }

                    // logger.info(String.format("%d %d %f %f", i, j, timeToCollision1, timeToCollision2));
//                }
//            }
//        }

//        for (int i = 0; i < distance.length; i++) {
//            for (int j = 0; j < distance[i].length; j++) {
//                distance[i][j] = Double.MAX_VALUE;
//                pass[i][j] = false;
//                departed[i] = false;
//            }
//        }

        // calculating potential delays between all pairs of planes
//        for (int i = 0; i < planes.size(); i++) {
//            Plane p = planes.get(i);
//            departure[i] = p.getDepartureTime();
//            if (p.getBearing() == -1) {
//                start[i] = p.getLocation();
//            }
//        }

//        for (int i = 0; i < planes.size(); i++) {
//            for (int j = i + 1; j < planes.size(); j++) {
//                Plane p1 = planes.get(i);
//                Plane p2 = planes.get(j);
//
//                double[] intersection = intersects(start[i].getX(), start[i].getY(), p1.getDestination().getX(), p1.getDestination().getY(),
//                        start[j].getX(), start[j].getY(), p2.getDestination().getX(), p2.getDestination().getY());
//
//                // logger.info(String.format("hi %f %f", intersection[0], intersection[1]));
//
////                if (intersection.length != 0) {
////                    logger.info(String.format("%f %f", intersection[intersection.length - 2], intersection[intersection.length - 1]));
////                    logger.info(String.format("%d %d", i, j));
////                }
//
////                if (distance(intersection[0], intersection[1], start[i].getX(), start[i].getY()) < 5) {
////                    delay[j] += 2.5;
////                } else if (distance(intersection[0], intersection[1], start[j].getX(), start[j].getY()) < 5) {
////                    delay[j] += 2.5;
////                }
//
//                // share endpoint
//                if (sharesEndpoint(start[i].getX(), start[i].getY(), p1.getDestination().getX(), p1.getDestination().getY(),
//                        start[j].getX(), start[j].getY(), p2.getDestination().getX(), p2.getDestination().getY())) {
//                    double timeToCollision1 = distance(start[i].getX(), start[i].getY(), intersection[0], intersection[1]) + departure[i] + delay[i];
//                    double timeToCollision2 = distance(start[j].getX(), start[j].getY(), intersection[0], intersection[1]) + departure[j] + delay[j];
//                    logger.info(String.format("%f %f %f %f", intersection[0], intersection[1], timeToCollision1, timeToCollision2));
//                    if (Math.abs(timeToCollision1 - timeToCollision2) < delayTimeThreshold) {
//                        logger.info("hi");
//                        delay[j] += delayTime; // Math.max(timeToCollision1, timeToCollision2);
//                        distance[i][j] = -1;
//                    }
//                } else if (intersection[0] != Double.MAX_VALUE) {
//                    double timeToCollision1 = distance(start[i].getX(), start[i].getY(), intersection[0], intersection[1]) + departure[i] + delay[i];
//                    double timeToCollision2 = distance(start[j].getX(), start[j].getY(), intersection[0], intersection[1]) + departure[j] + delay[j];
//                    // logger.info(String.format("%f %d %d %f %f %f", timeToCollision, i, j, intersection[0], intersection[1], distance(start[i].getX(), start[i].getY(), intersection[0], intersection[1]) + departure[i] + delay[i]));
//
//                    // loggerer.info(String.format("%f %f", timeToCollision1, timeToCollision2));
//
//                    if (Math.abs(timeToCollision1 - timeToCollision2) < delayTimeThreshold) {
//
//                        if (Math.abs(timeToCollision1 - timeToCollision2) < delayTimeThresholdStrict) {
//                            logger.info("1");
//                            if (timeToCollision1 > timeToCollision2) {
//                                delay[i] += delayTime;
//                            } else {
//                                delay[j] += delayTime;
//                            }
//                        } else if (timeToCollision2 - timeToCollision1 > delayTimeThresholdStrict) {
//                            logger.info("2");
//                            continue;
//                        } else if (timeToCollision1 - timeToCollision2 > delayTimeThresholdStrict) {
//                            logger.info(String.format("3 %d %d", i, j));
//                            delay[i] += delayTime;
//                        }
//
//                        // logger.info("delay");
//                        distance[i][j] = -1;
//                    }
//                }
//
//                // set initial distance between planes
//                double dist = distance(p1.getLocation().getX(), p1.getLocation().getY(),
//                        p2.getLocation().getX(), p2.getLocation().getY());
//
//                distance[i][j] = dist;
//            }
//        }
//
//        for (int i = 0; i < planes.size(); i++) {
//            logger.info(String.format("%d %f", i, delay[i]));
//        }
    }

    @Override
    public double[] updatePlanes(ArrayList<Plane> planes, int round, double[] bearings) {

        for (int i = 0; i < planes.size(); i++) {
            Plane p1 = planes.get(i);
            logger.info(safe[i]);
            if (round >= departure[i] && bearings[i] != -2 && safe[i]) {
                bearings[i] = calculateBearing(p1.getLocation(), p1.getDestination());
            }
//            else if (round >= departure[i]) {
//                // simulate sending plane
//                SimulationResult sim = startSimulation(planes, round);
//                if (sim.getReason() == 0) {
//                    safe[i] = true;
//                }
//            }



//            if (p1.getBearing() == -1) {
//                for (int j = i + 1; j < planes.size(); j++) {
//                    if (i != j) {
//                        Plane p2 = planes.get(j);
//
//                        Line2D path1 = new Line2D.Double(start[i], p1.getDestination());
//                        Line2D path2 = new Line2D.Double(start[j], p2.getDestination());
//
//                        // shares endpoint
////                        if (p1.getDestination().equals(p2.getDestination())) {
////                            if
////                        }
//                        if (path1.ptLineDist(start[j]) <= 5) {
//                            safe[i] = true;
//                            safe[j] = false;
//                        }
//                    }
//                }
//            }
        }

//        for (int i = 0; i < planes.size(); i++) {
//            Plane p = planes.get(i);
//            // safe[i] = isSafe(planes, p);
//            // logger.info(String.format("%f %d %f %b", delay[i], i, bearings[i], isSafe(planes, p)));
////            if (i == 28 || i == 45) {
////                // logger.info(String.format("%f %d %f %b", delay[i], i, bearings[i], isSafe(planes, p)));
////            }
//            if (round >= departure[i] + delay[i] && bearings[i] != -2) {
//                bearings[i] = calculateBearing(p.getLocation(), p.getDestination());
//                departed[i] = true;
//            }
//        }

        /*
        * iterate through all planes p
        * for all planes with departure + delay time at least that of p
        * calculate time to intersection
        * if no intersection within 5, then set safe[i] = true
        */

        // takes into account potential delays
//        for (int i = 0; i < planes.size(); i++) {
//            Plane p = planes.get(i);
//            if (round >= departure[i] + delay[i] && bearings[i] != -2 && !departed[i]) {
//                logger.info(String.format("%b %d",  safeLanding(planes, p), i));
//                if (clearSky(planes, p) && safeLanding(planes, p)) {
//                    bearings[i] = calculateBearing(p.getLocation(), p.getDestination());
//                    departed[i] = true; // prevent sharp changes in bearing
//                }
//            }
//        }

        // double[] change = new double[planes.size()];

        // check distances between all pairs of planes
//        for (int i = 0; i < planes.size(); i++) {
//            for (int j = i + 1; j < planes.size(); j++) {
//                Plane p1 = planes.get(i);
//                Plane p2 = planes.get(j);
//
//                // logger.info(String.format("start %d %d %f %f", i, j, p1.getBearing(), p2.getBearing()));
//
//                if (round >= departure[i] && round >= departure[j] && (p1.getBearing() >= 0 && p2.getBearing() >= 0)) {
//
//                    // logger.info(String.format("PLS ACTIVATE %d %d %f %f %f %f", i, j, start[i].getX(), start[i].getY(), start[j].getX(), start[j].getY()));
//                    double dist = distance(p1.getLocation().getX(), p1.getLocation().getY(),
//                            p2.getLocation().getX(), p2.getLocation().getY());
//                    double distToDest1 = distance(p1.getLocation().getX(), p1.getLocation().getY(),
//                            p1.getDestination().getX(), p1.getDestination().getY());
//                    double distToDest2 = distance(p2.getLocation().getX(), p2.getLocation().getY(),
//                            p2.getDestination().getX(), p2.getDestination().getY());
//
//                    if (dist > distance[i][j] && distance[i][j] != -1) {
//                        pass[i][j] = true;
//                    }
//
//                    if (pass[i][j]) {
//                        change[i] = calculateBearing(p1.getLocation(), p1.getDestination()) - bearings[i];
//                        change[j] = calculateBearing(p2.getLocation(), p2.getDestination()) - bearings[j];
//                    }
//
//                    if (distance[i][j] != -1) {
//                        if ((dist <= distanceThreshold && !pass[i][j] && distToDest1 >= destinationThreshold && distToDest2 >= destinationThreshold) || dist <= 5) {
//                            // logger.info(String.format("%d %d", i, j));
//                            double angle = Math.abs(p1.getBearing() - p2.getBearing());
//                            logger.info(String.format(" criss cross %d %d %f %b", i, j, angle, (angle <= 135 && angle >= 75) || (angle >= 160 && angle <= 165)));
//                            if ((angle <= 135 && angle >= 75) || (angle >= 160 && angle <= 165)) {
//                                // logger.info(String.format(" criss cross %d %d", i, j));
//                                change[i] = (change[i] - 10) % 360;
//                                change[j] = (change[j] - 10) % 360;
//                            } else {
//                                change[i] = (change[i] + 10) % 360;
//                                change[j] = (change[j] + 10) % 360;
//                            }
//                        }
//                    }
//
//                    distance[i][j] = dist;
//                }
//            }
//        }

        logger.info("-----");

        for (int i = 0; i < planes.size(); i++) {
            if (bearings[i] >= 0) {
                logger.info(String.format("%f %d", bearings[i], i));
            }
        }

//        for (int i = 0; i < planes.size(); i++) {
//            if (bearings[i] != -1 && bearings[i] != -2) {
//                // logger.info(String.format("%f", change[i]));
//                bearings[i] = adjustBearing(bearings[i], bearings[i] + change[i], 9.9);
//                // logger.info(String.format("hi %f %d %f %f", bearings[i], i, planes.get(i).getX(), planes.get(i).getY()));
//            }
//            // change[i] = 0;
//        }
        return bearings;
    }

//    private boolean isSafe(ArrayList<Plane> planes, Plane plane) {
//
//        for (int i = 0; i < planes.size(); i++) {
//            Plane p = planes.get(i);
//
//            if (p != plane) {
//                if (p.getX() == plane.getX() && p.getY() == plane.getY()) {
//                    if (plane.getDepartureTime() >= p.getDepartureTime()) {
//                        return false;
//                    }
//                }
//                if (p.getBearing() >= -1 && plane.getDepartureTime() >= p.getDepartureTime()) {
//
//                }
//            }
//        }
//
////        for (int i = 0; i < planes.size(); i++) {
////            Plane p = planes.get(i);
////            if (p!= plane && plane.getBearing() == -1 && departed[i] && plane.getDepartureTime() >= p.getDepartureTime() && p.getBearing() == -1) {
////                // logger.info("pLS BRO ACTIVATE THIS");
////                double bearingDiff = Math.abs(calculateBearing(p.getLocation(), p.getDestination()) - calculateBearing(plane.getLocation(), plane.getDestination()));
////                if (bearingDiff <= 180 + bearingRange && bearingDiff >= 180 - bearingRange) {
////                    // logger.info("what abt this");
////                    safe[i] = false;
////                }
////                // safe[i] = false;
////            }
////        }
////        for (Plane p : planes) {
////            if (p != plane) {
////                if (p.getX() == plane.getX() && p.getY() == plane.getY()) {
////                    if (plane.getDepartureTime() >= p.getDepartureTime()) {
////                        return false;
////                    }
////                }
////                if (p.getBearing() == -1 || p.getBearing() == -2) {
////                    continue;
////                }
////                if (distance(plane.getLocation().getX(), plane.getLocation().getY(), p.getLocation().getX(), p.getLocation().getY()) <= safeDistance) {
////                    return false;
////                }
////                double bearingDiff;
////                if (plane.getBearing() == -1) {
////                    bearingDiff = Math.abs(p.getBearing() - calculateBearing(plane.getLocation(), plane.getDestination()));
////                } else {
////                    bearingDiff = Math.abs(p.getBearing() - plane.getBearing());
////                }
////                logger.info(bearingDiff);
////                if (bearingDiff <= 180 + bearingRange && bearingDiff >= 180 - bearingRange) {
////                    return false;
////                }
////                double[] intersection = intersects(plane.getLocation().getX(), plane.getLocation().getY(), plane.getDestination().getX(), plane.getDestination().getY(),
////                        p.getLocation().getX(), p.getLocation().getY(), p.getDestination().getX(), p.getDestination().getY());
////                if (Math.min(p.getLocation().getX(), p.getDestination().getX()) <= intersection[0] && Math.max(p.getLocation().getX(), p.getDestination().getX()) >= intersection[0]
////                    && Math.min(p.getLocation().getY(), p.getDestination().getY()) <= intersection[1] && Math.max(p.getLocation().getY(), p.getDestination().getY()) >= intersection[1]) {
////                    return false;
////                }
////
////                if (distance(intersection[0], intersection[1], p.getDestination().getX(), p.getDestination().getY()) <= safeDistance) {
////                    return false;
////                }
////            }
////        }
////        return true;
//    }

    // in case of rounding errors
//    private double round(double input) {
//        return Math.round(input * 100.0) / 100.0;
//    }

//    private double adjustBearing(double originalBearing, double newBearing, double maxChange) {
//        double change = newBearing - originalBearing;
//
//        if (change >= maxChange) {
//            return round(originalBearing + maxChange - 0.2) % 360;
//        } else if (change <= -maxChange) {
//            return round(originalBearing - maxChange + 0.2) % 360;
//        } else {
//            return round(newBearing);
//        }
//    }

    private double distance(double x1, double y1, double x2, double y2) {
        double xdist = x1 - x2;
        double ydist = y1 - y2;
        return Math.sqrt(xdist * xdist + ydist * ydist);
    }

    // determines whether there are nearby planes to start
//    private boolean clearSky(ArrayList<Plane> planes, Plane plane) {
//        for (Plane p : planes) {
//            if (p != plane) {
//                if (p.getX() == plane.getX() && p.getY() == plane.getY()) {
//                    if (plane.getDepartureTime() >= p.getDepartureTime()) {
//                        return false;
//                    }
//                }
//                if (p.getBearing() == -1 || p.getBearing() == -2) {
//                    continue;
//                }
//                if (distance(plane.getLocation().getX(), plane.getLocation().getY(), p.getLocation().getX(), p.getLocation().getY()) <= 30) {
//                    return false;
//                }
//            }
//        }
//        return true;
//    }

    // determines whether plane is safe to land
    // looks at all other flying planes and determines whether intersection is possible near destination
//    private boolean safeLanding(ArrayList<Plane> planes, Plane plane) {
//        for (Plane p : planes) {
//            if (p != plane && p.getBearing() >= 0) {
////                if (p.getBearing() == -1 || p.getBearing() == -2) {
////                    continue;
////                }
////                double[] intersection = intersects(plane.getLocation().getX(), plane.getLocation().getY(), plane.getDestination().getX(), plane.getDestination().getY(),
////                        p.getLocation().getX(), p.getLocation().getY(), p.getDestination().getX(), p.getDestination().getY());
////                if (p.getDestination().equals(plane.getDestination()) && distance(plane.getDestination().getX(), plane.getDestination().getY(), intersection[0], intersection[1]) < 2.5 && p.getDestination().equals(plane.getDestination())) {
////                    logger.info(String.format("jsdf %f", distance(plane.getDestination().getX(), plane.getDestination().getY(), intersection[0], intersection[1])));
////                    return false;
////                }
//                if (p.getDestination().equals(plane.getDestination())) {
//                    return false;
//                }
//            }
//        }
//        return true;
//    }

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
