/*
* File: CoupledDistributed.java
* Author: José Luis Risco Martín <jlrisco@ucm.es>
* Created: 2024/03/14 (YYYY/MM/DD)
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
package xdevs.core.modeling.distributed;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import xdevs.core.modeling.Atomic;
import xdevs.core.modeling.Coupled;
import xdevs.core.simulation.distributed.CoordinatorDistributed;
import xdevs.core.util.DevsLogger;

/**
 * This class represents a distributed coupled model.
 * 
 * The model is defined in an XML file with the following structure:
 * <pre>
 * {@code
 * <coupled name="GPT" class="xdevs.core.examples.efp.Efp" host="192.168.1.3" mainPort="5000" auxPort="6000">
 * 	<atomic name="processor" class="xdevs.core.examples.efp.Processor" host="192.168.1.4" mainPort="5001" auxPort="6001">
 * 		<constructor-arg value="3.0"/>
 * 	</atomic>
 * 	<atomic name="generator" class="xdevs.core.examples.efp.Generator" host="192.168.1.5" mainPort="5002" auxPort="6002">
 * 		<constructor-arg value="1.0"/>
 * 	</atomic>
 * 	<atomic name="transducer" class="xdevs.core.examples.efp.Transducer" host="192.168.1.6" mainPort="5003" auxPort="6003">
 * 		<constructor-arg value="100.0"/>
 * 	</atomic>
 * 	<connection componentFrom="processor" portFrom="oOut" componentTo="transducer" portTo="iSolved"/>
 * 	<connection componentFrom="generator" portFrom="oOut" componentTo="processor" portTo="iIn"/>
 * 	<connection componentFrom="generator" portFrom="oOut" componentTo="transducer" portTo="iArrived"/>
 * 	<connection componentFrom="transducer" portFrom="oOut" componentTo="generator" portTo="iStop"/>
 * </coupled>
 * }
 * </pre>
 * 
 * The model is composed of atomic components and connections between them.
 * The model is executed by a distributed coordinator class.
 */
public class CoupledDistributed extends Coupled {

    private static final Logger LOGGER = Logger.getLogger(CoupledDistributed.class.getName());

    /**
     * XML element with the model definition.
     */
    protected Element xmlCoupled;
    /**
     * Hosts of the components.
     */
    protected HashMap<String, String> hosts = new HashMap<>();
    /**
     * Main ports of the components.
     */
    protected HashMap<String, Integer> mainPorts = new HashMap<>();
    /**
     * Auxiliary ports of the components.
     */
    protected HashMap<String, Integer> auxPorts = new HashMap<>();

    /**
     * Constructor of the class.
     * 
     * @param xmlCoupled XML element with the model definition.
     */
    public CoupledDistributed(Element xmlCoupled) {
        super(xmlCoupled.getAttribute("name"));
        hosts.put(xmlCoupled.getAttribute("name"), xmlCoupled.getAttribute("host"));
        mainPorts.put(xmlCoupled.getAttribute("name"), Integer.parseInt(xmlCoupled.getAttribute("mainPort")));
        auxPorts.put(xmlCoupled.getAttribute("name"), Integer.parseInt(xmlCoupled.getAttribute("auxPort")));
        // Creamos los distintos elementos
        NodeList xmlChildList = xmlCoupled.getChildNodes();
        for (int i = 0; i < xmlChildList.getLength(); ++i) {
            Node xmlNode = xmlChildList.item(i);
            String nodeName = xmlNode.getNodeName();
            switch (nodeName) {
                case "atomic":
                    try {
                    Element xmlChild = (Element) xmlNode;
                    Class<?> atomicClass = Class.forName(xmlChild.getAttribute("class"));
                    Constructor<?> constructor = atomicClass.getConstructor(new Class[]{Class.forName("org.w3c.dom.Element")});
                    Object atomicObject = constructor.newInstance(new Object[]{xmlChild});
                    this.addComponent((Atomic) atomicObject);
                    hosts.put(xmlChild.getAttribute("name"), xmlChild.getAttribute("host"));
                    mainPorts.put(xmlChild.getAttribute("name"), Integer.parseInt(xmlChild.getAttribute("mainPort")));
                    auxPorts.put(xmlChild.getAttribute("name"), Integer.parseInt(xmlChild.getAttribute("auxPort")));
                } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
                    LOGGER.severe(ex.getLocalizedMessage());
                }
                break;
                case "connection":
                    Element xmlChild = (Element) xmlNode;
                    String componentFrom = xmlChild.getAttribute("componentFrom");
                    String portFrom = xmlChild.getAttribute("portFrom");
                    String componentTo = xmlChild.getAttribute("componentTo");
                    String portTo = xmlChild.getAttribute("portTo");
                    this.addCoupling(componentFrom, portFrom, componentTo, portTo);
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * Get the host of a component.
     * @param componentName Name of the component.
     * @return Host of the component.
     */
    public String getHost(String componentName) {
        return hosts.get(componentName);
    }

    /**
     * Get the main port of a component.
     * @param componentName Name of the component.
     * @return Main port of the component.
     */
    public Integer getMainPort(String componentName) {
        return mainPorts.get(componentName);
    }

    /**
     * Get the auxiliary port of a component.
     * @param componentName Name of the component.
     * @return Auxiliary port of the component.
     */
    public Integer getAuxPort(String componentName) {
        return auxPorts.get(componentName);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            args = new String[]{"tmp" + File.separator + "gpt.xml"};
        }
        Element xmlCoupled;
        try {
            Document xmlCoupledModel = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(args[0]));
            xmlCoupled = (Element) xmlCoupledModel.getElementsByTagName("coupled").item(0);
            LOGGER.info(xmlCoupled.toString());
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return;
        }
        DevsLogger.setup(Level.INFO);
        LOGGER.info("Run Coordinator Atomic....");
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
}
