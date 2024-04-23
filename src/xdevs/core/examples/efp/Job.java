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

import java.io.Serializable;

/**
 * Job class for the GPT or EFP examples.
 */
public class Job implements Serializable {

  private static final long serialVersionUID = 6442709491519565544L;
  /**
   * Job id
   */
  protected String id;
  /**
   * Time in which the job was started to be processed.
   */
  protected double time;

  /**
   * Constructor
   * @param name Job id
   */
  public Job(String name) {
    this.id = name;
    this.time = 0.0;
  }

  /**
   * Set the job time
   * @param time Job time
   */
  public void setTime(double time) {
    this.time = time;
  }
  
  @Override
  public String toString() {
      return "(id,t)=(" + id + "," + time + ")";
  }
}
