/*
* File: RunDistributedSimulation.java
* Author: José Luis Risco Martín <jlrisco@ucm.es>
* Created: 2024/04/23 (YYYY/MM/DD)
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
package xdevs.core.examples.distributed.gpt;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import xdevs.core.simulation.distributed.CoordinatorDistributed;
import xdevs.core.simulation.distributed.SimulatorDistributed;
import xdevs.core.modeling.distributed.CoupledDistributed;
import xdevs.core.util.DevsLogger;

/**
 * Node class to run a distributed simulation.
 * 
 * The first argument is always the file name of the complete XML model.
 * If there is a second argument, it is the name of the atomic model to simulate. Node is executed as a stand-alone DEVS simulator.
 * On the other hand, if there is only one argument, the coordinator is executed.
 * 
 * The coordinator is responsible for initializing the simulation and running it until the end. 
 * The coordinator is also responsible for the finalization of the simulation.
 * The simulator is responsible for running a single atomic model.
 * 
 * All the simulation is orchestrated by passing messages between the coordinator and the simulators.
 * 
 * Example:
 * $ java -cp xdevs.jar xdevs.core.examples.distributed.gpt.RunDistributedSimulation gpt.xml
 * $ java -cp xdevs.jar xdevs.core.examples.distributed.gpt.RunDistributedSimulation gpt.xml Generator
 * $ java -cp xdevs.jar xdevs.core.examples.distributed.gpt.RunDistributedSimulation gpt.xml Processor
 * $ java -cp xdevs.jar xdevs.core.examples.distributed.gpt.RunDistributedSimulation gpt.xml Transducer
 */
public class RunDistributedSimulation {
    
    private static final Logger LOGGER = Logger.getLogger(RunDistributedSimulation.class.getName());
    
    public static void main(String[] args) {
        if(args.length>0) {
            String fileName = args[0];
            Element xmlCoupled;
            try {
                Document xmlCoupledModel = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(fileName));
                xmlCoupled = (Element) xmlCoupledModel.getElementsByTagName("coupled").item(0);
            } catch (IOException | ParserConfigurationException | SAXException ex) {
                Logger.getLogger(CoupledDistributed.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            DevsLogger.setup(Level.INFO);
            if(args.length==2){ // Simulator
                String atomicName = args[1];                                        
                CoupledDistributed gpt = new CoupledDistributed(xmlCoupled);
                LOGGER.info("Run "+atomicName+" .....");
                new SimulatorDistributed(gpt, atomicName);
            }else if(args.length==1){ // Coordinator
                LOGGER.info("Run Coordinator .....");
                CoupledDistributed gpt = new CoupledDistributed(xmlCoupled);
                CoordinatorDistributed coordinator = new CoordinatorDistributed(gpt);
                long start = System.currentTimeMillis();
                coordinator.initialize();
                coordinator.simulate(Long.MAX_VALUE);
                coordinator.exit();
                long end = System.currentTimeMillis();
                double time = (end - start) / 1000.0;
                LOGGER.info("TIME: " + time);
            }
        }else {
            LOGGER.info("Check the arguments: Two for Simulator(file name and atomic name) and One for Coordinator(file name)");
        }
    }
}
