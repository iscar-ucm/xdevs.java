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
package xdevs.core.examples.efp;

import xdevs.core.modeling.Coupled;
import xdevs.core.modeling.Port;

/**
 * Experimental-Frame DEVS coupled model.
 * 
 * This class has been designed to simulate the Ef model, which contains the Generator and Transducer models.
 */
public class Ef extends Coupled {

  /**
   * Input port.
   */
  protected Port<Job> iIn = new Port<>("in");
  /**
   * Output port.
   */
  protected Port<Job> oOut = new Port<>("out");

  /**
   * Constructor
   * @param name Model name
   * @param period Generator period
   * @param observationTime Observation time
   */
  public Ef(String name, double period, double observationTime) {
	  super(name);
    super.addInPort(iIn);
    super.addOutPort(oOut);
    Generator generator = new Generator("generator", period);
    super.addComponent(generator);
    Transducer transducer = new Transducer("transducer", observationTime);
    super.addComponent(transducer);
    
    super.addCoupling(this.iIn, transducer.iSolved);
    super.addCoupling(generator.oOut, this.oOut);
    super.addCoupling(generator.oOut, transducer.iArrived);
    super.addCoupling(transducer.oOut, generator.iStop);
  }
}
