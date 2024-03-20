/*
* File: CoordinatorDistributed.java
* Author: José Luis Risco Martín <jlrisco@ucm.es>
* Created: 2024/03/20 (YYYY/MM/DD)
*
* Copyright (C) 2024
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
*/
package xdevs.core.simulation.distributed;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import xdevs.core.modeling.Component;
import xdevs.core.modeling.distributed.CoupledDistributed;
import xdevs.core.simulation.Coordinator;
import xdevs.core.simulation.SimulationClock;
import xdevs.core.util.Constants;

/**
 * Coordinator for distributed simulation
 *
 * This class implements the coordinator for a distributed simulation. It
 * extends the Coordinator class and implements the methods to execute the
 * simulation in a distributed way.
 */
public class CoordinatorDistributed extends Coordinator {

    private static final Logger LOGGER = Logger.getLogger(CoordinatorDistributed.class.getName());

    /**
     * Executor for the distributed simulation.
     */
    private ExecutorService executor;

    /**
     * Constructor for the distributed coordinator.
     *
     * @param clock Simulation clock
     * @param model Coupled model
     */
    public CoordinatorDistributed(SimulationClock clock, CoupledDistributed model) {
        super(clock, model);
        this.executor = Executors.newFixedThreadPool(model.getComponents().size());
        LOGGER.fine("I am: " + this.model.getName());
        LOGGER.fine("Workers: " + this.model.getComponents().toString());
    }

    /**
     * Constructor for the distributed coordinator.
     *
     * @param model Coupled model
     */
    public CoordinatorDistributed(CoupledDistributed model) {
        this(new SimulationClock(), model);
    }

    /**
     * Execute a list of tasks in the distributed simulation.
     *
     * @param command Command to execute
     * @return List of distributed tasks
     */
    public LinkedList<DistributedTask> executeTasksList(int command) {        
        LinkedList<DistributedTask> distributedTasks = new LinkedList<>();
        LOGGER.log(Level.FINE, "Sending task \" + command + \"[c:\" + String.valueOf(clock.getTime()) + \"]\"");
        model.getComponents().forEach(component -> {
            String host = ((CoupledDistributed) model).getHost(component.getName());
            Integer port = ((CoupledDistributed) model).getMainPort(component.getName());
            distributedTasks.add(new DistributedTask(host, port, command, String.valueOf(clock.getTime())));
        });

        return distributedTasks;
    }

    @Override
    public void initialize() {
        try {
            executor.invokeAll(executeTasksList(Constants.DS_INITIALIZE));
        } catch (InterruptedException e) {
            LOGGER.severe(e.getLocalizedMessage());
        }
        tL = clock.getTime();
        tN = tL + ta();
    }

    @Override
    public void exit() {
        MessageDistributed md;
        PingMessage pm;
        for (Component component : model.getComponents()) {
            String host = ((CoupledDistributed) model).getHost(component.getName());
            Integer mainPort = ((CoupledDistributed) model).getMainPort(component.getName());
            // Integer auxPort = ((CoupledDistributed) model).getAuxPort(component.getName());
            md = new MessageDistributed(Constants.DS_EXIT, String.valueOf(clock.getTime()));
            pm = new PingMessage(md, host, mainPort);
            pm.ping();
        }
        executor.shutdown();
    }

    @Override
    public double ta() {
        double tn = Constants.INFINITY;
        try {
            List<Future<String>> tas = executor.invokeAll(executeTasksList(Constants.DS_TA));
            for (Future<String> ta : tas) {
                if (Double.valueOf(ta.get()) < tn) {
                    tn = Double.valueOf(ta.get()); // simulator.getTN();
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.severe(e.getLocalizedMessage());
        }
        return tn - clock.getTime();
    }

    @Override
    public void lambda() {
        try {
            executor.invokeAll(executeTasksList(Constants.DS_LAMBDA));
        } catch (InterruptedException e) {
            LOGGER.severe(e.getLocalizedMessage());
        }
        propagateOutput();
    }

    @Override
    public void propagateOutput() {
        try {
            executor.invokeAll(executeTasksList(Constants.DS_PROPAGATE_OUTPUT));
        } catch (InterruptedException e) {
            LOGGER.severe(e.getLocalizedMessage());
        }
    }

    @Override
    public void deltfcn() {
        try {
            executor.invokeAll(executeTasksList(Constants.DS_DELTFCN));
        } catch (InterruptedException e) {
            LOGGER.severe(e.getLocalizedMessage());
        }
        tL = clock.getTime();
        tN = tL + ta();
    }

    @Override
    public void clear() {
        try {
            executor.invokeAll(executeTasksList(Constants.DS_CLEAR));
        } catch (InterruptedException e) {
            LOGGER.severe(e.getLocalizedMessage());
        }
    }

}
