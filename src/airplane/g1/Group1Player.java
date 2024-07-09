package airplane.g1;

import airplane.sim.Plane;
import airplane.sim.Player;

import java.util.ArrayList;

public class Group1Player extends Player {
    @Override
    public String getName() {
        return "";
    }

    @Override
    public void startNewGame(ArrayList<Plane> planes) {

    }

    @Override
    public double[] updatePlanes(ArrayList<Plane> planes, int round, double[] bearings) {
        return new double[0];
    }
}
