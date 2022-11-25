package xdevs.lib.numdevs.math;

import java.util.ArrayList;

import xdevs.core.modeling.Atomic;
import xdevs.core.modeling.Port;

/**
 * <P>Comparador. Comprueba los dos valores de la entrada (u(0) y u(1)). Si
 * u(0)<=u(1) se devuelve el valor de _vL. Si u(0)>u(1) se devuelve
 * el valor de _vH.</P>
 * <P><B>Puertos de entrada:</B> Dos puertos de entada. El puerto 0 espera u(0)
 * mientras que el puerto 1 espera u(1).</P> 
 * <P><B>Puertos de salida:</B> Un único puerto de salida que arroja el resultado 
 * de la comparación.</P>
 * @author José Luis Risco Martín.
 *
 */
public class Comparator extends Atomic {
    /** Puertos de entrada. */
    public Port<Number> uPort_0 = new Port<>("u_0");
    public Port<Number> uPort_1 = new Port<>("u_1");
    /** Puertos de salida.*/
    public Port<Number> yPort = new Port<>("y");
    /** Input values. */
    protected Number u0, u1;
    /** Output values. */
    protected Number y;		// Inputs
    /** y value if u(0)<=u(1). */
    protected Number vL;		// output if u(0)<=u(1)
    /** y value if u(0)>u(1). */
    protected Number vH;
    /** Delay of the comparator in seconds. */
    protected double delay;

    /** Constructor. */
    public Comparator(Number vL, Number vH, double delay) {
        super();
        super.addInPort(uPort_0);
        super.addInPort(uPort_1);
        super.addOutPort(yPort);
        this.vL = vL;
        this.vH = vH;
        this.delay = delay;
    }

    // ---------------------------------------------------------------
    // DEVS PROTOCOL
    // ---------------------------------------------------------------
    /** 
     * Ininicializa los atributos del modelo atómico
     * @param parameters Parametros necesarios para configurar el átomo. 
     * <P>Los elementos de <code>parameters</code> deben ser los siguientes
     * <UL>
     * <LI>parameters(0): Double - _vL.</LI>
     * <LI>parameters(1): Double - _vH.</LI>
     * </UL></P>
     * */
    public void initialize() {
        u0 = null;
        u1 = null;
        y = null;
    }

    public void deltint() {
        super.passivate();
    }
	
    public void deltext(double e) {
        super.resume(e);
        if (!this.uPort_0.isEmpty()) {
            this.u0 = uPort_0.getSingleValue();
        }
        if (!this.uPort_1.isEmpty()) {
            this.u1 = uPort_1.getSingleValue();
        }
        if (u0!=null && u1!=null) {
            if (u0.doubleValue() > u1.doubleValue())
                y = vH;
            else
                y = vL;
            super.holdIn("active", delay);
        }
    }
	
    public void lambda() {
        yPort.addValue(y);
    }
	
    public void exit() { }
    //	 ---------------------------------------------------------------

}
