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

/**
 * This class represents the simulation clock.
 * 
 * The simulation clock is a double value that represents the current time of the simulation.
 */
public class SimulationClock {

    /**
     * The current time of the simulation.
     */
    protected double time;

    /**
     * Creates a new simulation clock with the given initial time.
     * 
     * @param time the initial time of the simulation.
     */
    public SimulationClock(double time) {
        this.time = time;
    }

    /**
     * Creates a new simulation clock with initial time 0.
     */
    public SimulationClock() {
        this(0);
    }

    /**
     * Gets the current simulation time.
     * @return the current simulation time.
     */
    public double getTime() {
        return time;
    }

    /**
     * Sets the current simulation time.
     * @param time the new simulation time.
     */
    public void setTime(double time) {
        this.time = time;
    }
}
