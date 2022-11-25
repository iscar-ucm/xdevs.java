package xdevs.lib.numdevs.math;

import java.util.ArrayList;

import xdevs.core.modeling.Atomic;
import xdevs.core.modeling.Port;

/**
 * <P>Realiza un producto de la entrada.</P>
 * <P><B>Puertos de entrada:</B> Tantos puertos de entrada como se especifique
 * en la función <code>init</code>.</P> 
 * <P><B>Puertos de salida:</B> Un único puerto de salida que arroja el producto
 * de las entradas.</P>
 * @author José Luis Risco Martín.
 *
 */
public class Multiplier extends Atomic {
    /** Input ports. */
    public ArrayList<Port<Number>> uPort = new ArrayList<>();
    /** Output ports. */
    public Port<Number> yPort = new Port<>("y");
    /** Input values. */
    protected ArrayList<Number> u;		// Inputs
    /** Output value: _y=u(0)*u(1)*...*u(n-1). */
    protected Number y;		// Output->u(0)*u(1)*u(2)*...*u(n-1)
    /** Delay to produce the output. */
    protected double delay;
    /** Contructor. */
    public Multiplier(int n, double delay) {
        super("Multiplier");
        super.addOutPort(yPort);
        this.delay = delay;
        for (int i = 0; i<n; i++) {
            Port<Number> port = new Port<>("u_" + i);
            uPort.add(port);
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
     * <LI>parameters(0): Integer - _n.</LI>
     * </UL></P>
     * */
    public void initialize() {
        u = new ArrayList<Number>(uPort.size());
        for(int i=0; i<u.size(); i++)
            u.set(i, null);
        super.passivate();
    }
	
    public void deltint() {
        super.passivate();
    }
	
    public void deltext(double e) {
        super.resume(e);
        for (int i = 0; i < uPort.size(); i++) {
            if (!uPort.get(i).isEmpty()) {
                u.set(i, uPort.get(i).getSingleValue());
            }
        }
        y = 1;
        for (int i = 0; i < u.size(); i++) {
            if(u.get(i)!=null) {
                y = y.doubleValue()*u.get(i).doubleValue();
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
