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

import org.w3c.dom.Element;

import xdevs.core.modeling.Atomic;
import xdevs.core.modeling.Component;
import xdevs.core.modeling.Port;

/**
 * Events generator for the DEVStone benchmark
 */
public class DevStoneGenerator extends Atomic {

    /**
     * Output port
     */
    public Port<Integer> oOut = new Port<>("out");
    /**
     * Preparation time. Time to prepare the atomic model before the execution of the output and internal transition functions.
     */
    protected double preparationTime;
    /**
     * Period. Time between consecutive output events.
     */
    protected double period;
    /**
     * Counter of events
     */
    protected int counter = 1;
    /**
     * Maximum number of events to generate
     */
    protected int maxEvents = Integer.MAX_VALUE;

    /**
     * Constructor
     * @param name name of the model
     * @param preparationTime preparation time
     * @param period period
     * @param maxEvents maximum number of events to generate
     */
    public DevStoneGenerator(String name, double preparationTime, double period, int maxEvents) {
        super(name);
        super.addOutPort(oOut);
        this.preparationTime = preparationTime;
        this.period = period;
        this.maxEvents = maxEvents;
    }

    /**
     * Constructor
     * @param xmlAtomic XML element with the description of the atomic model
     */
    public DevStoneGenerator(Element xmlAtomic) {
        this(xmlAtomic.getAttribute("name"),
                Double.parseDouble(
                        ((Element) (xmlAtomic.getElementsByTagName("constructor-arg").item(0))).getAttribute("value")),
                Double.parseDouble(
                        ((Element) (xmlAtomic.getElementsByTagName("constructor-arg").item(1))).getAttribute("value")),
                Integer.parseInt(
                        ((Element) (xmlAtomic.getElementsByTagName("constructor-arg").item(2))).getAttribute("value")));
    }

    @Override
    public void initialize() {
        counter = 1;
        this.holdIn("active", preparationTime);
    }

    @Override
    public void exit() {
    }

    @Override
    public void deltint() {
        counter++;
        if (counter > maxEvents) {
            super.passivate();
        } else {
            this.holdIn("active", period);
        }
    }

    @Override
    public void deltext(double e) {
        super.resume(e);
        super.passivate();
    }

    @Override
    public void lambda() {
        oOut.addValue(counter);
    }

    /**
     * Get the period
     * @return period
     */
    public double getPeriod() {
        return period;
    }

    /**
     * Get the maximum number of events to generate
     * @return maximum number of events
     */
    public int getMaxEvents() {
        return maxEvents;
    }

    /**
     * Get the preparation time
     * @return preparation time
     */
    public double getPreparationTime() {
        return preparationTime;
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
        builder.append(tabs).append("\t<constructor-arg value=\"").append(this.getPeriod()).append("\"/>\n");
        builder.append(tabs).append("\t<constructor-arg value=\"").append(this.getMaxEvents()).append("\"/>\n");
        builder.append(tabs).append("</atomic>\n");
        
        return builder.toString();
    }
}
