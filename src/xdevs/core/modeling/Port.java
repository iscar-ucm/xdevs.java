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

import java.util.Collection;
import java.util.LinkedList;

/** 
 * Class for the ports of the components in the DEVS formalism.
 * 
 * A port is a connection point of a component. It can be an input port or an
 * output port. The input port is used to receive values from other components,
 * and the output port is used to send values to other components.
*/
public class Port<E> {

    /** 
     * The parent component of the port.
     */
    protected Component parent = null;
    /**
     * The name of the port.
     */
    protected String name;
    /**
     * The values stored in the port.
     */
    protected LinkedList<E> values = new LinkedList<>();

    /**
     * Constructor of the port.
     * @param name The name of the port.
     */
    public Port(String name) {
        this.name = name;
    }

    /**
     * Constructor of the port.
     */
    public Port() {
        this(Port.class.getSimpleName());
    }

    /**
     * Get the name of the port.
     * @return The name of the port.
     */
    public String getName() {
        return name;
    }

    /**
     * Empties the values stored in the port.
     */
    public void clear() {
        values.clear();
    }

    /**
     * Returns true if the port is empty.
     * @return True if the port is empty.
     */
    public boolean isEmpty() {
        return values.isEmpty();
    }

    /**
     * Returns the first value stored in the port.
     * @return The first value stored in the port.
     */
    public E getSingleValue() {
        return values.element();
    }

    /**
     * Returns the values stored in the port.
     * @return The values stored in the port.
     */
    public Collection<E> getValues() {
        return values;
    }

    /**
     * Adds a value to the port.
     * @param value The value to add to the port.
     */
    public void addValue(E value) {
        values.add(value);
    }

    /**
     * Adds a collection of values to the port.
     * @param valuesPort The collection of values to add to the port.
     */
    public void addValues(Collection<E> valuesPort) {
        this.values.addAll(valuesPort);
    }

    /**
     * Get the parent component of the port.
     * @return The parent component of the port.
     */
    public Component getParent() {
        return parent;
    }

    /**
     * Returns the qualified name of the port.
     * @return The qualified name of the port.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        Component parentAux = this.getParent();
        while (parentAux != null) {
            sb.insert(0, ".");
            sb.insert(0, parentAux.getName());
            parentAux = parentAux.getParent();
        }
        return sb.toString();
    }
}
