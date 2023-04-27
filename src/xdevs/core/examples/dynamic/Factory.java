/*
* Copyright (C) 2023 José Luis Risco Martín <jlrisco@ucm.es>
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*
* Contributors:
*  - José Luis Risco Martín
*/

package xdevs.core.examples.dynamic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

import xdevs.core.examples.efp.Generator;
import xdevs.core.examples.efp.Transducer;
import xdevs.core.modeling.Component;
import xdevs.core.modeling.Coupled;
import xdevs.core.simulation.Coordinator;
import xdevs.core.util.DevsLogger;

/**
 *
 * @author José L. Risco-Martín
 */
public class Factory extends Coupled {

    public Factory(String name, double period, double processingTime, double observationTime) {
    	super(name);
        // Generator
        Generator generator = new Generator("generator", period);
        // Machines
        ArrayList<Machine> machines = new ArrayList<>();
        // At least one machine
        machines.add(new Machine("machine", processingTime));
        super.addComponent(machines.get(0));
        // Transducer
        Transducer transducer = new Transducer("transducer", observationTime);
        super.addCoupling(generator.oOut, machines.get(0).iJob);
        super.addCoupling(generator.oOut, transducer.iArrived);
        super.addCoupling(machines.get(0).oJobSolved, transducer.iSolved);
        super.addCoupling(transducer.oOut, generator.iStop);
    }

    /*@Override
    public void structuralTransition() {
        // Remove idle machines
        Iterator<Component> itr = components.iterator();
        while(itr.hasNext()) {
            Component component = itr.next();
            if (component instanceof Machine) {
                Machine machine = (Machine) component;
                if (machine.queue.isEmpty()) {
                    machine.dynamicTransition();
                    itr.remove();
                }
            }
        }
        for (Component component : super.getComponents()) {
            if (component instanceof Machine) {
                Machine machine = (Machine) component;
                if (machine.queue.isEmpty()) {
                    super.getCom
                }
            }
        }
    }*/

    public static void main(String args[]) {
        DevsLogger.setup(Level.FINE);
        Factory factory = new Factory("factory", 1.0, 3.0, 100.0);
        Coordinator coordinator = new Coordinator(factory);
        coordinator.initialize();
        coordinator.simulate(Long.MAX_VALUE);
        coordinator.exit();
    }
}
