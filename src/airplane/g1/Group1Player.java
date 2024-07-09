package airplane.g1;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import airplane.sim.Plane;
import airplane.sim.Player;

public class Group1Player extends Player {

    private Logger logger = Logger.getLogger(this.getClass());

    @Override
    public String getName() {
        return "Group 1 Player";
    }

    @Override
    public void startNewGame(ArrayList<Plane> planes) {
        logger.info("Starting new game!");
    }

    @Override
    public double[] updatePlanes(ArrayList<Plane> planes, int round, double[] bearings) {

        // double case, strictly along x-axis
        Plane p1 = planes.get(0);
        Plane p2 = planes.get(1);

        if (round >= p1.getDepartureTime()) {
            double dist1 = p1.getDestination().distance(p1.getLocation());
            double dist2 = p2.getDestination().distance(p2.getLocation());

            if (p1.getBearing() != -2) {
                double d1 = Math.atan(-Math.sin(2.5 * dist1)) * 180 / Math.PI;
                logger.info(String.format("%.2f", d1));
                if (Math.abs(bearings[0] - d1) > 10) {
                    if (bearings[0] < d1) {
                        bearings[0] += 9.9;
                    } else {
                        bearings[0] -= 9.9;
                    }
                } else {
                    bearings[0] = d1;
                }

                logger.info(String.format("angle: %.2f", bearings[0]));
            }

            if (p2.getBearing() != -2) {
                bearings[1] = Math.cos(dist2 / 2.5) * -2.5 + 270;
            }
        }
        if (bearings[0] < 0) {
            bearings[0] += 360;
        }
        if (bearings[0] > 360) {
            bearings[0] -= 360;
        }
        return bearings;
    }
}
