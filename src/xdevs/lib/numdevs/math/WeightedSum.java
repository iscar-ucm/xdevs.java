package xdevs.lib.numdevs.math;

import java.util.ArrayList;

import xdevs.core.modeling.Atomic;
import xdevs.core.modeling.Port;

/**
 * <P>Realiza una suma con pesos.</P>
 * <P><B>Puertos de entrada:</B> Tantos puertos de entrada como se especifique
 * en la función <code>init</code>.</P> 
 * <P><B>Puertos de salida:</B> Un único puerto de salida que arroja la suma con
 * pesos de las entradas.</P>
 * @author José Luis Risco Martín.
 *
 */
public class WeightedSum extends Atomic {
    /** Input ports. */
    protected ArrayList<Port<Number>> uPorts = new ArrayList<>();
    /** Output port. */
    public Port<Number> yPort = new Port<>("y");
    /** Weights. */
    protected ArrayList<Number> w; 	// Weights
    /** Input values. */
    protected ArrayList<Number> u;	// Inputs
    /** Output value: _y=sum{i=0;n-1}(_w(i)*_u(i)). */
    protected Number y;		// Sum
    /** Delay to produce the output. */
    protected double delay;
    /** Constructor. */
    public WeightedSum(ArrayList<Number> weights, double delay) {
        super("WeightedSum");
        super.addOutPort(yPort);
        this.delay = delay;
        this.w = weights;
        for (int i = 0; i < w.size(); i++) {
            Port<Number> port = new Port<>("u_" + i);
            uPorts.add(port);
            super.addInPort(port);
        }
    }

    // ---------------------------------------------------------------
    // DEVS PROTOCOL
    // ---------------------------------------------------------------
    /** 
     * Inicializa los atributos del modelo atómico
     * @param parameters Parametros necesarios para configurar el átomo. 
     * <P>Los elementos de <code>parameters</code> deben ser los siguientes
     * <UL>
     * <LI>parameters(0): ArrayList - _w. ArrayList de Double que representan
     * los pesos a utilizar en la suma.</LI>
     * </UL></P>
     * */
    public void initialize() {
        u = new ArrayList<Number>(uPorts.size());
        for(int i=0; i<u.size(); i++)
            u.set(i, null);
        super.passivate();
    }
	
    public void deltint() {
        super.passivate();
    }
	
    public void deltext(double e) {
        super.resume(e);
        for (int i = 0; i < uPorts.size(); i++) {
            if (!uPorts.get(i).isEmpty()) {
                u.set(i, uPorts.get(i).getSingleValue());
            }
        }
        y = 0;
        for (int i = 0; i < u.size(); i++) {
            if(u.get(i)!=null) {
                y = y.doubleValue() + u.get(i).doubleValue()*w.get(i).doubleValue();
            }
            else {
                y = null;
                super.passivate();
                return;
            }
        }
        super.holdIn("active", delay);
    }
	
    public void lambda() {
        yPort.addValue(y);
    }
	
    public void exit() { }
    //	 ---------------------------------------------------------------

}
