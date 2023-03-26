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
package xdevs.core.examples.dynamic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

import xdevs.core.modeling.Component;
import xdevs.core.modeling.Coupled;
import xdevs.core.simulation.Coordinator;
import xdevs.core.util.DevsLogger;

/**
 *
 * @author jlrisco
 */
public class Factory extends Coupled {

    public Factory(String name, double processingTime) {
    	super(name);
        // Router
        Router router = new Router("router");
        super.addComponent(router);
        // Machines
        ArrayList<Machine> machines = new ArrayList<>();
        // At least one machine
        machines.add(new Machine("machine", processingTime));
        super.addComponent(machines.get(0));
        
        super.addCoupling(generator.oOut, processor.iIn);
        super.addCoupling(generator.oOut, transducer.iArrived);
        super.addCoupling(processor.oOut, transducer.iSolved);
        super.addCoupling(transducer.oOut, generator.iStop);
    }

    @Override
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
    }
}
