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
package xdevs.core.util;

/**
 * Constants used in the xDEVS framework.
 */
public class Constants {
    // Commmon constants
    public static final double INFINITY = Double.POSITIVE_INFINITY;
    public static final String PHASE_PASSIVE = "passive";
    public static final String PHASE_ACTIVE = "active";
    // Commands for the distributed simulation
    public static final int DS_NONE = 0; // Distrituted simulation: this does not do anything.   
    public static final int DS_INITIALIZE = 2; // Distrituted simulation: it allows to initialize the simulator.
    public static final int DS_TA = 3; // Distrituted simulation: it allows find the advance time.
    public static final int DS_LAMBDA = 4; // Distrituted simulation:: it allows to execute the lambda method.
    public static final int DS_PROPAGATE_OUTPUT = 5; // Distrituted simulation: it allows propagate the outputs for each simulator.    
    public static final int DS_PROPAGATE_OUTPUT_N2N = 6; // Distrituted simulation: it allows propagate the outputs of node to node.    
    public static final int DS_DELTFCN = 7; // Distrituted simulation: it allows to execute the delta methods.    
    public static final int DS_CLEAR = 8; // Distrituted simulation: it allows to refresh ports.    
    public static final int DS_EXIT = 15; // Distrituted simulation: it allows to exit of each simulator.    
}
