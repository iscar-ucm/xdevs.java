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

import xdevs.core.modeling.Component;
import xdevs.core.modeling.Coupled;
import xdevs.core.modeling.Port;

/**
 * Coupled model to study the performance using DEVStone.
 */
public abstract class DevStone extends Coupled {
    /**
     * DEVStone input port
     */
    public Port<Integer> iIn = new Port<>("in");
    /**
     * DEVStone output port
     */
    public Port<Integer> oOut = new Port<>("out");

    /**
     * Constructor
     * @param name name of the model
     */
    public DevStone(String name) {
        super(name);
        super.addInPort(iIn);
        super.addOutPort(oOut);
    }

    /**
     * Theoretical number of atomic models in the DEVStone model
     * @param width width of the DEVStone model
     * @param depth depth of the DEVStone model
     * @return theoretical number of atomic models
     */
    public abstract int numAtomicsInTheory(int width, int depth);

    /**
     * Theoretical number of external transitions performed in the simulation of the DEVStone model
     * @param maxEvents maximum number of input events
     * @param width width of the DEVStone model
     * @param depth depth of the DEVStone model
     * @return theoretical number of external transitions performed
     */
    public abstract long numDeltExtsInTheory(int maxEvents, int width, int depth);

    /**
     * Theoretical number of internal transitions performed in the simulation of the DEVStone model
     * @param maxEvents maximum number of input events
     * @param width width of the DEVStone model
     * @param depth depth of the DEVStone model
     * @return theoretical number of internal transitions performed
     */
    public abstract long numDeltIntsInTheory(int maxEvents, int width, int depth);

    /**
     * Theoretical number of events internally received in all the external transition functions.
     * @param maxEvents number of external input events to the DEVStone model
     * @param width width of the DEVStone model
     * @param depth depth of the DEVStone model
     * @return theoretical number of events internally received in all the external transition functions
     */
    public abstract long numEventsInTheory(int maxEvents, int width, int depth);

    /**
     * Number of external transitions performed in the current DEVStone model
     * @return number of external transitions
     */
    public long numDeltExtsInPractice() {
        long numDeltExts = 0;
        for (Component c : super.getComponents()) {
            if (c instanceof DevStoneAtomic) {
                numDeltExts += ((DevStoneAtomic) c).numDeltExts;
            } else if (c instanceof DevStone) {
                numDeltExts += ((DevStone) c).numDeltExtsInPractice();
            }
        }
        return numDeltExts;
    }

    /**
     * Number of internal transitions performed in the current DEVStone model
     * @return number of internal transitions
     */
    public long numDeltIntsInPractice() {
        long numDeltInts = 0;
        for (Component c : super.getComponents()) {
            if (c instanceof DevStoneAtomic) {
                numDeltInts += ((DevStoneAtomic) c).numDeltInts;
            } else if (c instanceof DevStone) {
                numDeltInts += ((DevStone) c).numDeltIntsInPractice();
            }
        }
        return numDeltInts;
    }

    /**
     * Number of events internally received in all the external transition functions.
     * @return number of events internally received
     */
    public long numEventsInPractice() {
        long numOfEvents = 0;
        for (Component c : super.getComponents()) {
            if (c instanceof DevStoneAtomic) {
                numOfEvents += ((DevStoneAtomic) c).numOfEvents;
            } else if (c instanceof DevStone) {
                numOfEvents += ((DevStone) c).numEventsInPractice();
            }
        }
        return numOfEvents;
    }
}
