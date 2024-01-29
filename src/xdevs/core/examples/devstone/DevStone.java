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
 * Coupled model to study the performance using DEVStone
 *
 * @author José Luis Risco Martín
 */
public abstract class DevStone extends Coupled {
    public Port<Integer> iIn = new Port<>("in");
    public Port<Integer> oOut = new Port<>("out");

    public DevStone(String name) {
        super(name);
        super.addInPort(iIn);
        super.addOutPort(oOut);
    }

    public abstract int numAtomicsInTheory(int width, int depth);

    public abstract long numDeltExtsInTheory(int maxEvents, int width, int depth);

    public abstract long numDeltIntsInTheory(int maxEvents, int width, int depth);

    public abstract long numEventsInTheory(int maxEvents, int width, int depth);

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
