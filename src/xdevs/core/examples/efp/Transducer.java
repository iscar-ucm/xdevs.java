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

import java.util.LinkedList;
import java.util.logging.Logger;
import org.w3c.dom.Element;

import xdevs.core.modeling.Atomic;
import xdevs.core.modeling.Port;

/**
 * Transducer
 * 
 * The transducer collects statistics about the jobs.
 * It also finishes the simulation when the observation time is reached.
 */
public class Transducer extends Atomic {

    private static final Logger LOGGER = Logger.getLogger(Transducer.class.getName());

    /**
     * Input port for arrived jobs.
     */
    public Port<Job> iArrived = new Port<>("iArrived");
    /**
     * Input port for solved jobs.
     */
    public Port<Job> iSolved = new Port<>("iSolved");
    /**
     * Output port to finish the simulation.
     */
    public Port<Job> oOut = new Port<>("oOut");
    /**
     * Jobs arrived.
     */
    protected LinkedList<Job> jobsArrived = new LinkedList<>();
    /**
     * Jobs processed.
     */
    protected LinkedList<Job> jobsSolved = new LinkedList<>();
    /**
     * Observation time before finishing the simulation.
     */
    protected double observationTime;
    /**
     * Total time consumed by the jobs.
     */
    protected double totalTa;
    /**
     * Internal clock.
     */
    protected double clock;

    /**
     * Constructor
     * @param name Transducer name
     * @param observationTime Observation time
     */
    public Transducer(String name, double observationTime) {
        super(name);
        super.addInPort(iArrived);
        super.addInPort(iSolved);
        super.addOutPort(oOut);
        totalTa = 0;
        clock = 0;
        this.observationTime = observationTime;
    }

    /**
     * Constructor from XML
     * @param xmlAtomic XML element
     */
    public Transducer(Element xmlAtomic) {
        this(xmlAtomic.getAttribute("name"), 
             Double.parseDouble(((Element)(xmlAtomic.getElementsByTagName("constructor-arg").item(0))).getAttribute("value")));
    }

    @Override
    public void initialize() {
        super.holdIn("active", observationTime);
    }

    @Override
    public void exit() {
    }

    @Override
    public void deltint() {
        clock = clock + getSigma();
        double throughput;
        double avgTaTime;
        if (phaseIs("active")) {
            if (!jobsSolved.isEmpty()) {
                avgTaTime = totalTa / jobsSolved.size();
                if (clock > 0.0) {
                    throughput = jobsSolved.size() / clock;
                } else {
                    throughput = 0.0;
                }
            } else {
                avgTaTime = 0.0;
                throughput = 0.0;
            }
            LOGGER.info("End time: " + clock);
            LOGGER.info("Jobs arrived : " + jobsArrived.size());
            LOGGER.info("Jobs solved : " + jobsSolved.size());
            LOGGER.info("Average TA = " + avgTaTime);
            LOGGER.info("Throughput = " + throughput);
            holdIn("done", 0);
        } else {
            passivate();
        }
        //logger.info("####deltint: "+showState());
    }

    @Override
    public void deltext(double e) {
        super.resume(e);
        clock = clock + e;
        if (phaseIs("active")) {
            if (!iArrived.isEmpty()) {
                Job job = iArrived.getSingleValue();
                LOGGER.fine("Start job " + job.id + " @ t = " + clock);
                job.time = clock;
                jobsArrived.add(job);
            }
            if (!iSolved.isEmpty()) {
                for(Job job : iSolved.getValues()) {
                    totalTa += (clock - job.time);
                    LOGGER.fine("Finish job " + job.id + " @ t = " + clock);
                    job.time = clock;
                    jobsSolved.add(job);
                }
            }
        }
        //logger.info("###Deltext: "+showState());
    }

    @Override
    public void lambda() {
        if (phaseIs("done")) {
            Job job = new Job("null");
            oOut.addValue(job);
        }
    }
}
