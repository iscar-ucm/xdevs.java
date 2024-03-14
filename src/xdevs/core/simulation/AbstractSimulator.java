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
 *  - José Luis Risco Martín <jlrisco@ucm.es>
 *  - Saurabh Mittal <smittal@duniptech.com>
 */
package xdevs.core.simulation;

import xdevs.core.modeling.Component;

/**
 * This class represents the abstraction of a parallel DEVS simulator, in xDEVS style.
 * 
 * The simulator is responsible for the simulation of a DEVS model, and it is composed of
 * a simulation clock and the model to simulate. The simulator is responsible for the
 * initialization, execution and termination of the simulation.
 */
public abstract class AbstractSimulator {

    /**
     * The simulation clock of the simulator.
     */
    protected SimulationClock clock;
    /**
     * The time of the last event.
     */
    protected double tL; // Time of last event
    /**
     * The time of the next event.
     */
    protected double tN; // Time of next event

    /**
     * Creates a new simulator with the given simulation clock.
     * 
     * @param clock the simulation clock of the simulator.
     */
    public AbstractSimulator(SimulationClock clock) {
        this.clock = clock;
    }

    /**
     * Initializes the simulator and the associated model. This is not part of the DEVS
     * formalism, but it is a necessary step to prepare the simulator for the simulation.
     */
    public abstract void initialize();

    /**
     * This is a special function, out of the DEVS formalism, that allows to
     * realize some actions when the simulation ends.
     */
    public abstract void exit();

    /**
     * Executes the time advance mechanism of the DEVS simulation.
     * @return the new DEVS sigma.
     */
    public abstract double ta();

    /**
     * Executes the output function of the DEVS simulation.
     */
    public abstract void lambda();

    /**
     * Executes one of the transition functions of the DEVS simulation, depending on the
     * current state of the model and whether there are new events or not.
     */
    public abstract void deltfcn();

    /**
     * Empties the event list at the ports of the associated model.
     */
    public abstract void clear();

    /**
     * Gets the associated model of the simulator.
     * @return the associated model of the simulator.
     */
    public abstract Component getModel();

    public double getTL() {
        return tL;
    }

    /**
     * Sets the time of the last event.
     * @param tL the time of the last event.
     */
    public void setTL(double tL) {
        this.tL = tL;
    }

    /**
     * Gets the time of the next event.
     * @return the time of the next event.
     */
    public double getTN() {
        return tN;
    }

    /**
     * Sets the time of the next event.
     * @param tN the time of the next event.
     */
    public void setTN(double tN) {
        this.tN = tN;
    }

    /**
     * Gets the simulation clock of the simulator.
     * @return the simulation clock of the simulator.
     */
    public SimulationClock getClock() {
        return clock;
    }
}
