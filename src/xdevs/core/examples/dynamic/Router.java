package xdevs.core.examples.dynamic;

import java.util.ArrayList;

import xdevs.core.examples.efp.Job;
import xdevs.core.modeling.Atomic;
import xdevs.core.modeling.Port;
import xdevs.core.util.Constants;

public class Router extends Atomic {

    protected Port<Job> iJob = new Port<>("iJob");
    protected ArrayList<Port<Double>> iServiceTimes = new ArrayList<>();
    protected ArrayList<Double> serviceTimes = new ArrayList<>();
    protected int machineIdx = 0;
    protected ArrayList<Port<Job>> oJobs = new ArrayList<>();
    protected Job currentJob = null;

    public Router(String name) {
        super(name);
        // At least we have one input and output ports in the arrays
        iServiceTimes.add(new Port<>("iServiceTime"));
        serviceTimes.add(Constants.INFINITY);
        oJobs.add(new Port<>("oJob"));
    }

    @Override
    public void initialize() {
        super.passivate();
    }

    @Override
    public void exit() {
    }

    @Override
    public void lambda() {
        oJobs.get(machineIdx).addValue(currentJob);
    }

    @Override
    public void deltint() {
        currentJob = null;
        super.passivate();
    }

    @Override
    public void deltext(double e) {
        resume(e);
        for (int i = 0; i < iServiceTimes.size(); i++) {
            if (!iServiceTimes.get(i).isEmpty()) {
                serviceTimes.set(i, iServiceTimes.get(i).getSingleValue());
            }
        }
    }

}
