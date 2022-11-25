package xdevs.lib.numdevs.integrator;

import xdevs.core.modeling.Atomic;
import xdevs.core.modeling.Port;

/**
 * QSS integrator.
 * 
 * @author José Luis Risco Martín.
 */
public class IntegratorQSS extends Atomic {
    public Port<Double> uPort = new Port<>("u");
    public Port<Double> yPort = new Port<>("y");
    
    protected Double epsilon;	// Quantum size
    protected Double x0, x;		// State
    protected Double u;		// Input
    protected Double y;		// Output
    protected Double q;		// Quantum
	
    public IntegratorQSS(Double epsilon, Double x0) {
        super();
        this.epsilon = epsilon;
        this.x0 = x0;
    }
	
    // ---------------------------------------------------------------
    // DEVS PROTOCOL
    // ---------------------------------------------------------------
    @Override
    public void initialize() {
        super.activate();
        x = x0;
        u = 0.0;
        y = 0.0;
        q = x;
    }
	
    public void deltint() {
        x = x + u*super.getSigma();
        q = x;
        if (u==0) super.passivate();
        else super.holdIn("active", epsilon/Math.abs(u));
    }
	
    public void deltext(double e) {
        x = x + u*e;
        u = uPort.getSingleValue();
        if (super.getSigma()==0) return;
        if (u==0) super.passivate();
        else super.holdIn("active", (q-x + Math.signum(u)*epsilon)/u);
    }
	
    public void lambda() {
        y = q + epsilon*Math.signum(u);
        yPort.addValue(y);
    }
	
    public void exit() { }
    //	 ---------------------------------------------------------------

}

