/*
* Copyright (C) 2023 José Luis Risco Martín <jlrisco@ucm.es>
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
*
* Contributors:
*  - José Luis Risco Martín
*/

package xdevs.core.examples.dynamic;

import xdevs.core.examples.efp.Job;
import xdevs.core.modeling.Atomic;
import xdevs.core.modeling.Port;

public class Machine extends Atomic {

    public Port<Job> iJob = new Port<>("iJob");
    public Port<Job> oJobSolved = new Port<>("oJobSolved");
    public Port<Job> oJobIgnored = new Port<>("oJobIgnored");
    protected double processingTime;
    protected double ignoringTime;
    protected double clock;
    protected Job processingJob;
    protected Job ignoringJob;

    public Machine(String name, double processingTime) {
        super(name);
        super.addInPort(iJob);
        super.addOutPort(oJobSolved);
        super.addOutPort(oJobIgnored);
        this.processingTime = processingTime;
        this.clock = 0;

    }

    @Override
    public void initialize() {
        super.passivate();
        processingJob = null;
        ignoringJob = null;
    }

    @Override
    public void exit() {
    }

    @Override
    public void deltint() {
        clock += super.getSigma();
        if(super.phaseIs("busy")) {
            processingJob = null;
            super.passivate();
        }
        else if(super.phaseIs("busy+ignoring")) {
            ignoringJob = null;
            super.holdIn("busy", processingTime-ignoringTime);
        }
    }

    @Override
    public void deltext(double e) {
        super.resume(e);
        clock += e;
        Job job = iJob.getSingleValue();
        if (processingJob == null) {
            processingJob = job;
            super.holdIn("busy", processingTime);
            processingJob.setTime(clock);
        }
        else {
            ignoringJob = job;
            ignoringTime = e;
            super.holdIn("busy+ignoring", 0.0);            
        }
    }

    @Override
    public void lambda() {
        if(super.phaseIs("busy")) {
            oJobSolved.addValue(processingJob);
        }
        else if(super.phaseIs("busy+ignoring")) {
            oJobIgnored.addValue(ignoringJob);
        }
    }
}
