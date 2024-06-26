/*
 * Copyright (C) 2014-2015 José Luis Risco Martín <jlrisco@ucm.es> and 
 * Saurabh Mittal <smittal@duniptech.com>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, see
 * http://www.gnu.org/licenses/
 *
 * Contributors:
 *  - José Luis Risco Martín
 */
package xdevs.core.examples.efp;

import org.w3c.dom.Element;

import xdevs.core.modeling.Atomic;
import xdevs.core.modeling.Port;

/**
 * Generator of jobs.
 * 
 * The generator produces jobs with a given period.
 */
public class Generator extends Atomic {
    public Port<Job> iStop = new Port<>("iStop");
    public Port<Job> oOut = new Port<>("oOut");
    protected int jobCounter;
    protected double period;

    /**
     * Constructor
     * @param name Generator name
     * @param period Generator period
     */
    public Generator(String name, double period) {
        super(name);
        super.addInPort(iStop);
        super.addOutPort(oOut);
        this.period = period;
    }

    /**
     * Constructor from XML
     * @param xmlAtomic XML element
     */
    public Generator(Element xmlAtomic) {
        this(xmlAtomic.getAttribute("name"), Double.parseDouble(
                ((Element) (xmlAtomic.getElementsByTagName("constructor-arg").item(0))).getAttribute("value")));
    }

    @Override
    public void initialize() {
        jobCounter = 1;
        this.holdIn("active", period);
    }

    @Override
    public void exit() {
    }

    @Override
    public void deltint() {
        jobCounter++;
        this.holdIn("active", period);
    }

    @Override
    public void deltext(double e) {
        super.resume(e);
        super.passivate();
    }

    @Override
    public void lambda() {
        Job job = new Job("" + jobCounter + "");
        oOut.addValue(job);
    }
}
