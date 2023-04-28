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
import java.util.logging.Level;

import xdevs.core.examples.efp.Generator;
import xdevs.core.examples.efp.Transducer;
import xdevs.core.modeling.Coupled;
import xdevs.core.simulation.dynamic.CoordinatorDynamic;
import xdevs.core.util.DevsLogger;

/**
 *
 * @author José L. Risco-Martín
 */
public class Factory extends Coupled {

    protected ArrayList<Machine> machines = new ArrayList<>();
    protected double processingTime;
    protected Transducer transducer;
    protected int minimumIdleMachines;

    public Factory(String name, double period, double processingTime, double observationTime, int minimumIdleMachines) {
    	super(name);
        this.processingTime = processingTime;
        this.minimumIdleMachines = minimumIdleMachines;
        // Generator
        Generator generator = new Generator("generator", period);
        super.addComponent(generator);
        // Machines
        ArrayList<Machine> machines = new ArrayList<>();
        // At least one machine
        Machine machine = new Machine("machine-0", processingTime);
        machines.add(machine);
        super.addComponent(machine);
        // Transducer
        transducer = new Transducer("transducer", observationTime);
        super.addComponent(transducer);
        // Couplings
        super.addCoupling(generator.oOut, machine.iJob);
        super.addCoupling(generator.oOut, transducer.iArrived);
        super.addCoupling(machine.oJobSolved, transducer.iSolved);
        super.addCoupling(transducer.oOut, generator.iStop);
    }

    @Override
    public void structuralTransition() {
        // CONTINUE HERE:
        // Count idle machines
        int numIdle = 0;
        for(Machine machine : machines) {
            if(machine.phaseIs(Machine.PHASE_IDLE)) {
                numIdle++;
            }
        }
        // 1.- Remove idle machines:        
        for(Machine machine : machines) {
            if(machine.phaseIs(Machine.PHASE_IDLE)) {
                machines.remove(machine);
                super.components.remove(machine);
            }
        }
        if (machines.size()<=1)
            return;
        // If numIdle==0 we must add a new machine:
        if (numIdle>=minimumIdleMachines) {
            Machine machine = new Machine("machine-" + machines.size(), processingTime);
            super.addComponent(machine);
            super.addCoupling(machine.oJobSolved, transducer.iSolved);
            Machine prevMachine = machines.get(machines.size()-1);
            super.addCoupling(prevMachine.oJobIgnored, machine.iJob);
            machines.add(machine);
        }
    }

    public static void main(String args[]) {
        DevsLogger.setup(Level.FINE);
        Factory factory = new Factory("factory", 1.0, 3.0, 100.0, Integer.MAX_VALUE);
        CoordinatorDynamic coordinator = new CoordinatorDynamic(factory);
        coordinator.initialize();
        coordinator.simulate(Long.MAX_VALUE);
        coordinator.exit();
    }
}
