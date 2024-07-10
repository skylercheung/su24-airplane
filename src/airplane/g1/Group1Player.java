package airplane.g1;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import airplane.sim.Plane;
import airplane.sim.Player;

public class Group1Player extends Player {

    private Logger logger = Logger.getLogger(this.getClass());
    private boolean pass = false;
    private double preDist = Double.MAX_VALUE;

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

        double bearing1 = 0;
        double bearing2 = 0;


        // Check if the plane is in the air
        if (round >= departureTime1) {
            bearing1 = calculateBearing(p1.getLocation(), p1.getDestination());
        }
        if (round >= departureTime2) {
            bearing2 = calculateBearing(p2.getLocation(), p2.getDestination());
        }


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

        bearings[0] = bearing1;
        bearings[1] = bearing2;

        return bearings;
    }

    private double adjustBearing(double originalBearing, double newBearing, double maxChange) {
        double change = newBearing - originalBearing;

        if(change > maxChange) {
            return originalBearing + maxChange;
        } else if(change < -maxChange) {
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
}
