package xdevs.lib.numdevs.integrator;

import java.util.ArrayList;

import xdevs.core.modeling.Atomic;
import xdevs.core.modeling.Port;

/**
 * <P>Modelo atómico DEVS que representa un integrador vectorial.</P>
 * <P><B>Puertos de entrada:</B> Un único puerto de entrada. Por este puerto recibe
 * un ArrayList de objetos Double que representa el estado a integrar.</P> 
 * <P><B>Puertos de salida:</B> Un único puerto de salida que arroja el estado
 * integrado.</P>
 * @author José Luis Risco Martín.
 */

public class Integrator extends Atomic {
    /**Input port */
    public Port<ArrayList<Number>> uPort = new Port<ArrayList<Number>>("u");
    /**Output port*/
    public Port<ArrayList<Number>> yPort = new Port<ArrayList<Number>>("y");
    /** Entrada */
    protected ArrayList<Number> u;
    /** Initial state */
    protected ArrayList<Number> x0;
    /** Current state */
    protected ArrayList<Number> x;
    /** Output */
    protected ArrayList<Number> y;
    /** Sample time */
    protected Double sampleTime;
	
    /** Constructor */
    public Integrator(Double sampleTime, ArrayList<Number> x0) {
        super();
        super.addInPort(uPort);
        super.addOutPort(yPort);
        this.sampleTime = sampleTime;
        this.x0 = x0;
    }

    // ---------------------------------------------------------------
    // DEVS PROTOCOL
    // ---------------------------------------------------------------
    public void initialize() {
        x = x0;
        u = new ArrayList<Number>();
        y = new ArrayList<Number>();
        for(int i=0;i<x.size();i++) {
            u.add(0.0);
            y.add(0.0);
        }
        super.activate();
    }
	
    public void deltint() {
        EulerIntegrationStep(super.getSigma());
        if(sampleTime.doubleValue()==0 && this.isZero(u)) super.passivate();
        else {
            if(sampleTime.doubleValue()==0) super.passivate();
            else super.holdIn("active", sampleTime.doubleValue());
        }
    }

    public void deltext(double e) {
        super.resume(e);
        this.EulerIntegrationStep(e);
        u = uPort.getSingleValue();
        if(sampleTime.doubleValue()==0 && this.isZero(u)) super.passivate();
        else {
            if(sampleTime.doubleValue()==0) super.activate();
            else super.holdIn("active", sampleTime.doubleValue());
        }
    }
	
    public void lambda() {
        y = x;
        yPort.addValue(y);
    }
	
    public void exit() { }
    //	 ---------------------------------------------------------------
	
    /**
     * <P>Realiza un paso en el método de integración de Euler.</P>
     * <P>Calcula x=x+u*delta, donde x es el estado actual y u es la última
     * entrada.</P>
     * @param delta Paso de integración. 
     */
    public void EulerIntegrationStep(double delta) {
        double xi,ui;
        for(int i=0;i<x.size();i++) {
            xi = x.get(i).doubleValue();
            ui = u.get(i).doubleValue();
            x.set(i, xi + delta*ui);
        }
    }
	
    /**
     * Función auxiliar que verifica si todos los componentes del array
     * <code>x</code> son cero. 
     * @param x El array.
     * @return Falo si cualquier elemento del array no es cero. Cierto en caso 
     * contrario.
     */
    public boolean isZero(ArrayList<Number> x) {
        for(int i=0;i<x.size();i++)
            if(x.get(i).doubleValue()!=0) return false;
        return true;
    }
}
