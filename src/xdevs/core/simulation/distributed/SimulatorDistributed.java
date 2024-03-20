/*
* File: SimulatorDistributed.java
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;

import xdevs.core.modeling.Atomic;
import xdevs.core.modeling.Coupling;
import xdevs.core.modeling.distributed.CoupledDistributed;
import xdevs.core.simulation.SimulationClock;
import xdevs.core.simulation.Simulator;
import xdevs.core.util.Constants;

/**
 * Simulator for distributed simulation.
 * 
 * This class implements a simulator for distributed simulation. It contains a
 * coupled parent and a boolean to indicate if the simulation has finished.
 */
public class SimulatorDistributed extends Simulator {
    private static final Logger LOGGER = Logger.getLogger(SimulatorDistributed.class.getName());

    /**
     * Parent coupled model for the simulator.
     */
    protected CoupledDistributed parent;
    /**
     * Boolean to indicate if the simulation has finished.
     */
    protected boolean getOut = false;

    /**
     * Constructor for the distributed simulator.
     *
     * @param parent Parent coupled model for the simulator.
     * @param name Name of the model to simulate, child of this parent.
     */
    public SimulatorDistributed(CoupledDistributed parent, String name) {
        super(new SimulationClock(), (Atomic) parent.getComponentByName(name));
        this.parent = parent;
        this.model = (Atomic) parent.getComponentByName(name);
        System.out.println("I am: " + model.getName());
        System.out.println("Parent: " + parent.getName());
        this.run();
    }

    /**
     * Gets the boolean to indicate if the simulation has finished.
     * @return Boolean to indicate if the simulation has finished.
     */
    public boolean isGetOut() {
        return getOut;
    }

    /**
     * Propagates the output of the model to the input of the connected models.
     */
    public void propagateOutput() {
        MessageDistributed md;
        String nameModel = this.model.getName();
        for (Coupling c : parent.getIC()) {
            if (c.getPortFrom().getParent().getName().equals(nameModel) && c.getPortFrom().getValues().size() > 0) {
                md = new MessageDistributed(Constants.DS_PROPAGATE_OUTPUT_N2N, c.getPortTo().getName(), c.getPortFrom().getValues());
                PingMessage pm = new PingMessage(md, parent.getHost(c.getPortTo().getParent().getName()), parent.getAuxPort(c.getPortTo().getParent().getName()));
                pm.ping();
            }
        }
    }

    /**
     * Propagates the output of the model to the input of the connected models.
     * @param valuesPort Collection of values for the message.
     * @param portName Name of the port for the message.
     */
    public void propagateOutputN2N(Collection valuesPort, String portName) {
        this.model.getInPort(portName).addValues(valuesPort);
    }

    /**
     * This function interprets the message and executes the command.
     * @param md Message to interpret.
     * @return Message with the response.
     */
    public MessageDistributed interpreter(MessageDistributed md) {
        MessageDistributed response = null;
        Date date = new Date();
        DateFormat now = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.");
        try {
            switch (md.getCommand()) {
                case Constants.DS_NONE:
                    response = new MessageDistributed("NONE");
                    break;
                case Constants.DS_INITIALIZE:
                    clock.setTime(Double.parseDouble(md.getMessage()));
                    this.initialize();
                    response = new MessageDistributed("INITIALIZE: OK At " + now.format(date));
                    break;
                case Constants.DS_TA:
                    clock.setTime(Double.parseDouble(md.getMessage()));
                    response = new MessageDistributed(String.valueOf(this.getTN()));
                    break;
                case Constants.DS_LAMBDA:
                    clock.setTime(Double.parseDouble(md.getMessage()));
                    this.lambda();
                    response = new MessageDistributed("LAMBDA: OK At " + now.format(date));
                    break;
                case Constants.DS_PROPAGATE_OUTPUT:
                    clock.setTime(Double.parseDouble(md.getMessage()));
                    this.propagateOutput();
                    response = new MessageDistributed("PROPAGATE_OUTPUT: OK At " + now.format(date));
                    break;
                case Constants.DS_PROPAGATE_OUTPUT_N2N:
                    this.propagateOutputN2N(md.getValuesPort(), md.getMessage());
                    response = new MessageDistributed("PROPAGATE_OUTPUT_N2N: OK At " + now.format(date));
                    break;
                case Constants.DS_DELTFCN:
                    clock.setTime(Double.parseDouble(md.getMessage()));
                    this.deltfcn();
                    response = new MessageDistributed("DELTFCN: OK At " + now.format(date));
                    break;
                case Constants.DS_CLEAR:
                    clock.setTime(Double.parseDouble(md.getMessage()));
                    this.clear();
                    response = new MessageDistributed("CLEAR: OK At " + now.format(date));
                    break;
                case Constants.DS_EXIT:
                    clock.setTime(Double.parseDouble(md.getMessage()));
                    this.exit();
                    response = new MessageDistributed("EXIT: OK At " + now.format(date));
                    this.getOut = true;
                    break;
                default:
                    response = new MessageDistributed("BAD_COMMAND");
                    break;
            }

        } catch (NumberFormatException ex) {
            LOGGER.severe(ex.getLocalizedMessage());
        }
        return response;
    }

    /**
     * Run the simulator.
     */
    public void run() {
        // For to attend the communication with the coordinator
        DistributedDaemon mainDaemon = new DistributedDaemon(parent.getMainPort(model.getName()), this);
        mainDaemon.start();
        // For to attend the communication with the workers (At this case to propagate)
        DistributedDaemon auxDaemon = new DistributedDaemon(parent.getAuxPort(model.getName()), this);
        auxDaemon.start();
    }

}
