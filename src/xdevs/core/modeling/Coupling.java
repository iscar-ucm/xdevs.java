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

/**
 * Class for the couplings between components in the DEVS formalism.
 * 
 * A coupling is a connection between two ports of two different components. The
 * coupling is used to propagate the values from the output port to the input
 * port.
 */
public class Coupling<E> {

    /**
     * The source port of the coupling.
     */
    protected Port<E> portFrom;
    /**
     * The destination port of the coupling.
     */
    protected Port<E> portTo;

    /**
     * Constructor of the coupling.
     * @param portFrom The source port of the coupling.
     * @param portTo The destination port of the coupling.
     */
    public Coupling(Port<E> portFrom, Port<E> portTo) {
        this.portFrom = portFrom;
        this.portTo = portTo;
    }

    /**
     * Returns the string representation of the coupling.
     */
    @Override
    public String toString() {
        return "(" + portFrom + "->" + portTo + ")";
    }

    /**
     * Propagates the values from the source port to the destination port.
     */
    public void propagateValues() {
        portTo.addValues(portFrom.getValues());
    }

    /**
     * Returns the source port of the coupling.
     * @return The source port of the coupling.
     */
    public Port<E> getPortFrom() {
        return portFrom;
    }

    /**
     * Returns the destination port of the coupling.
     * @return The destination port of the coupling.
     */
    public Port<E> getPortTo() {
        return portTo;
    }
}
