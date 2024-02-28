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
 *  - José Luis Risco Martín <jlrisco@ucm.es>
 *  - Román Cárdenas Rodríguez <r.cardenas@upm.es>
 */
package xdevs.core.modeling;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstract class for all components in the DEVS formalism.
 * 
 * A component is a model that can be connected to other components to form a
 * hierarchical model. It has input and output ports to communicate with other
 * components.
 * 
 * The component has a parent component, which is the component that contains
 * this component. The parent component is null if this component is the root
 * component of the model.
 * 
 * The component has a list of input ports and a list of output ports. The input
 * ports are used to receive input from other components, and the output ports
 * are used to send output to other components.
 */
public abstract class Component {

    // Component attributes
    /// Parent component
    /**
     * The parent component of this component. The parent component is the
     * component that contains this component. The parent component is null if
     * this component is the root component of the model.
     */
    protected Component parent = null;
    /** 
     * The name of the component. The name is used to identify the component in
     * the model.
     */
    protected String name;

    /**
     * The list of input ports of the component. The input ports are used to
     * receive input from other components.
     */
    protected ArrayList<Port<?>> inPorts = new ArrayList<>();
    /**
     * The list of output ports of the component. The output ports are used to
     * send output to other components.
     */
    protected ArrayList<Port<?>> outPorts = new ArrayList<>();
    /**
     * The chained attribute is used to indicate if the component is chained to
     * another component. A component is chained to another component if the
     * output ports of the first component are connected to the input ports of
     * the second component.
     */
    protected Boolean chained = false;

    /**
     * The constructor of the component. The constructor receives the name of the
     * component. The name is used to identify the component in the model.
     * 
     * @param name The name of the component.
     */
    public Component(String name) {
        this.name = name;
    }

    /**
     * The default constructor of the component. The default constructor creates
     * a component with the name "Component".
     */
    public Component() {
        this(Component.class.getSimpleName());
    }
    
    /**
     * Get the name of the component.
     * @return The name of the component.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Initialize the component. The initialize method is called when the
     * simulation starts. The initialize method is used to initialize the
     * component before the simulation starts.
     */
    public abstract void initialize();

    /**
     * Exit function. The exit function is called when the simulation ends. The
     * exit function is used to release resources used by the component.
     */
    public abstract void exit();

    /**
     * This function has been designed to obtain the XML representation of the
     * component. The XML representation of the component is used to save the
     * model to a file.
     * @return The XML representation of the component.
     */
    public abstract String toXml();

    /**
     * This function checks if the input ports of the component are empty. The
     * input ports are empty if they do not have any input.
     * @return True if the input ports of the component are empty, false
     * otherwise.
     */
    public boolean isInputEmpty() {
        return inPorts.stream().noneMatch((port) -> (!port.isEmpty()));
    }

    /**
     * This function has been designed to add an input port to the component. The
     * input port is added to the list of input ports of the component.
     * @param port The input port to be added to the component.
     */
    public void addInPort(Port<?> port) {
        inPorts.add(port);
        port.parent = this;
    }

    /**
     * This function has been designed to obtain an input port of the component.
     * @param portName The name of the input port to be obtained.
     * @return The input port with the specified name. If the input port does not
     * exist, the function returns null.
     */
    public Port<?> getInPort(String portName) {
        for(Port<?> port : inPorts) {
            if(port.name.equals(portName)) {
                return port;
            }
        }
        return null;
    }

    /**
     * This function has been designed to obtain the list of input ports of the
     * component.
     * @return The list of input ports of the component.
     */
    public Collection<Port<?>> getInPorts() {
        return inPorts;
    }

    /**
     * This function has been designed to add an output port to the component. The
     * output port is added to the list of output ports of the component.
     * @param port The output port to be added to the component.
     */
    public void addOutPort(Port<?> port) {
        outPorts.add(port);
        port.parent = this;
    }

    /**
     * This function has been designed to obtain an output port of the component.
     * @param portName The name of the output port to be obtained.
     * @return The output port with the specified name. If the output port does
     * not exist, the function returns null.
     */
    public Port<?> getOutPort(String portName) {
        for(Port<?> port : outPorts) {
            if(port.name.equals(portName)) {
                return port;
            }
        }
        return null;
    }

    /**
     * This function has been designed to obtain the list of output ports of the
     * component.
     * @return The list of output ports of the component.
     */
    public Collection<Port<?>> getOutPorts() {
        return outPorts;
    }

    /**
     * This function has been designed to obtain the parent component of the
     * component.
     * @return The parent component of the component.
     */
    public Component getParent() {
        return parent;
    }

    /**
     * This function has been designed to set the parent component of the
     * component.
     * @param parent The parent component of the component.
     */
    public void setParent(Component parent) {
        this.parent = parent;
    }

    /**
     * This function builds the string representation of the component. The
     * string representation of the component is used to print the component to
     * the standard output.
     * @return The string representation of the component.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name + " :");
        sb.append(" Inports[ ");
        inPorts.forEach((p) -> {
            sb.append(p).append(" ");
        });
        sb.append("]");
        sb.append(" Outports[ ");
        outPorts.forEach((p) -> {
            sb.append(p).append(" ");
        });
        sb.append("]");
        return sb.toString();
    }
}
