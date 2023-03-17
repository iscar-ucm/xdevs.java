/*
 * Copyright (C) 2023 José Luis Risco Martín <jlrisco@ucm.es>.
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
 */
package xdevs.core.simulation.dynamic;

import xdevs.core.modeling.Atomic;
import xdevs.core.modeling.Component;
import xdevs.core.simulation.SimulationClock;
import xdevs.core.simulation.Simulator;

/**
 *
 * @author José Luis Risco Martín
 */
public class SimulatorDynamic extends Simulator {

    public SimulatorDynamic(SimulationClock clock, Atomic model) {
        super(clock, model);
    }

    @Override
    public void deltfcn() {
        double t = clock.getTime();
        boolean isInputEmpty = model.isInputEmpty();
        if (!isInputEmpty) {
            double e = t - tL;
            if (t == tN) {
                model.deltcon(e);
            } else {
                model.deltext(e);
            }
        } else if (t == tN)
            model.deltint();
        else
            return;
        model.dynamicTransition();
        tL = t;
        tN = tL + model.ta();
    }
}
