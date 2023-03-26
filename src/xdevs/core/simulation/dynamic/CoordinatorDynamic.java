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

import java.util.Collection;
import java.util.logging.Level;

import xdevs.core.examples.efp.Efp;
import xdevs.core.modeling.Atomic;
import xdevs.core.modeling.Component;
import xdevs.core.modeling.Coupled;
import xdevs.core.simulation.AbstractSimulator;
import xdevs.core.simulation.Coordinator;
import xdevs.core.simulation.SimulationClock;
import xdevs.core.util.DevsLogger;

/**
 *
 * @author José Luis Risco Martín
 */
public class CoordinatorDynamic extends Coordinator {

    public CoordinatorDynamic(SimulationClock clock, Coupled model) {
        super(clock, model, false);
    }

    public CoordinatorDynamic(Coupled model) {
        super(model, false);
    }
    
    protected void buildHierarchy() {
          // Build hierarchy
        Collection<Component> components = model.getComponents();
        components.forEach((component) -> {
            if (component instanceof Coupled) {
                CoordinatorDynamic coordinator = new CoordinatorDynamic(clock, (Coupled) component);
                simulators.add(coordinator);
            } else if (component instanceof Atomic) {
                SimulatorDynamic simulator = new SimulatorDynamic(clock, (Atomic) component);
                simulators.add(simulator);
            }
        });
    }

    @Override
    public void deltfcn() {
        propagateInput();
        // TODO: Continue here
        boolean internalDynamicTransition = false;
        for (AbstractSimulator simulator : simulators) {
            simulator.deltfcn();
            if(simulator.getModel().getStructuralTransition()!=Component.StructuralTransition.FALSE)
                internalDynamicTransition = true; 
        }
        model.structuralTransition();
        tL = clock.getTime();
        tN = tL + ta();
    }

    public static void main(String[] args) {
        DevsLogger.setup(Level.INFO);
        Efp efp = new Efp("EFP", 1, 3, 1000000);
        CoordinatorDynamic coordinator = new CoordinatorDynamic(efp);
        coordinator.initialize();
        coordinator.simulate(Long.MAX_VALUE);
        coordinator.exit();
    }
}
