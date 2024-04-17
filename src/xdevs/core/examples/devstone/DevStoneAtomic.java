/*
 * Copyright (C) 2014-2016 José Luis Risco Martín <jlrisco@ucm.es>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *  - José Luis Risco Martín
 */
package xdevs.core.examples.devstone;

import org.apache.commons.math3.distribution.RealDistribution;
import org.w3c.dom.Element;

import xdevs.core.modeling.Atomic;
import xdevs.core.modeling.Component;
import xdevs.core.modeling.Port;

/**
 * DEVStone atomic model
 */
public class DevStoneAtomic extends Atomic {

    /**
     * Input port
     */
    public Port<Integer> iIn = new Port<>("in");
    /**
     * Output port
     */
    public Port<Integer> oOut = new Port<>("out");

    /**
     * Dhrystone benchmark. This benchmark is executed while the atomic model is in delay mode.
     */
    protected Dhrystone dhrystone;
    
    /**
     * Preparation time. Time to prepare the atomic model before the execution of the output and internal transition functions.
     */
    protected double preparationTime;
    /**
     * Internal delay time. Time to execute the internal transition function.
     */
    protected double intDelayTime;
    /**
     * External delay time. Time to execute the external transition function.
     */
    protected double extDelayTime;
    
    /**
     * Number of internal transitions performed
     */
    public long numDeltInts = 0;
    /**
     * Number of external transitions performed
     */
    public long numDeltExts = 0;
    /**
     * Number of events received
     */
    public long numOfEvents = 0;
    
    /**
     * Constructor
     * @param name name of the model
     * @param preparationTime preparation time
     * @param intDelayTime internal delay time
     * @param extDelayTime external delay time
     */
    public DevStoneAtomic(String name, double preparationTime, double intDelayTime, double extDelayTime) {
        super(name);
        super.addInPort(iIn);
        super.addOutPort(oOut);
        this.preparationTime = preparationTime;
        this.intDelayTime = intDelayTime;
        this.extDelayTime = extDelayTime;
    }
    
    /**
     * Constructor
     * @param name name of the model
     * @param preparationTime preparation time
     * @param distribution distribution to generate the internal and external delay times
     */
    public DevStoneAtomic(String name, double preparationTime, RealDistribution distribution) {
        this(name, preparationTime, distribution.sample(), distribution.sample());
    }

    /**
     * Constructor
     * @param xmlAtomic XML element with the atomic model
     */
    public DevStoneAtomic(Element xmlAtomic) {
        this(xmlAtomic.getAttribute("name"), 
            Double.parseDouble(((Element) (xmlAtomic.getElementsByTagName("constructor-arg").item(0))).getAttribute("value")),
            Double.parseDouble(((Element) (xmlAtomic.getElementsByTagName("constructor-arg").item(1))).getAttribute("value")),
            Double.parseDouble(((Element) (xmlAtomic.getElementsByTagName("constructor-arg").item(2))).getAttribute("value")));
    }

    @Override
    public void initialize() {
        super.passivate();
    }
    
    @Override
    public void exit() { }

    @Override
    public void deltint() {
        numDeltInts++;
        Dhrystone.execute(intDelayTime);
        super.passivate();
    }
    
    @Override
    public void deltext(double e) {
        super.resume(e);
        numDeltExts++;
        Dhrystone.execute(extDelayTime);
        if (!iIn.isEmpty()) {
            numOfEvents += iIn.getValues().size();
        }
        super.holdIn("active", preparationTime);
    }
    
    @Override
    public void lambda() {
        oOut.addValue(0);
    }

    /**
     * Get the preparation time
     * @return preparation time
     */
    public double getPreparationTime() {
        return preparationTime;
    }

    /**
     * Get the internal delay time
     * @return internal delay time
     */
    public double getIntDelayTime() {
        return intDelayTime;
    }

    /**
     * Get the external delay time
     * @return external delay time
     */
    public double getExtDelayTime() {
        return extDelayTime;
    }  
    
    @Override
    public String toXml() {
        StringBuilder builder = new StringBuilder();
        StringBuilder tabs = new StringBuilder();
        Component parent = this.getParent();
        int level = 0;
        while (parent!=null) {
            tabs.append("\t");
            level++;
            parent = parent.getParent();
        }
        builder.append(tabs).append("<atomic name=\"").append(this.getName()).append("\"");
        builder.append(" class=\"").append(this.getClass().getCanonicalName()).append("\"");
        builder.append(" host=\"127.0.0.1\"");
        builder.append(" port=\"").append(5000 + level).append("\"");
        builder.append(">\n");
        builder.append(tabs).append("\t<constructor-arg value=\"").append(this.getPreparationTime()).append("\"/>\n");
        builder.append(tabs).append("\t<constructor-arg value=\"").append(this.getIntDelayTime()).append("\"/>\n");
        builder.append(tabs).append("\t<constructor-arg value=\"").append(this.getExtDelayTime()).append("\"/>\n");
        builder.append(tabs).append("</atomic>\n");
        
        return builder.toString();
    }
}
