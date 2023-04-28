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

    public static final String PHASE_IDLE = "idle";
    public static final String PHASE_BUSY = "busy";
    public static final String PHASE_BUSY_IGNORING = "busy+ignoring";

    public Port<Job> iJob = new Port<>("iJob");
    public Port<Job> oJobSolved = new Port<>("oJobSolved");
    public Port<Job> oJobIgnored = new Port<>("oJobIgnored");
    protected double processingTime;
    protected double busyTime;
    protected Job processingJob;
    protected Job ignoringJob;

    public Machine(String name, double processingTime) {
        super(name);
        super.addInPort(iJob);
        super.addOutPort(oJobSolved);
        super.addOutPort(oJobIgnored);
        this.processingTime = processingTime;

    }

    @Override
    public void initialize() {
        super.passivate(Machine.PHASE_IDLE);
        processingJob = null;
        ignoringJob = null;
    }

    @Override
    public void exit() {
    }

    @Override
    public void deltint() {
        if(super.phaseIs(Machine.PHASE_BUSY)) {
            processingJob = null;
            busyTime = 0.0;
            super.passivate(Machine.PHASE_IDLE);
        }
        else if(super.phaseIs(Machine.PHASE_BUSY_IGNORING)) {
            ignoringJob = null;
            super.holdIn(Machine.PHASE_BUSY, processingTime-busyTime);
        }
    }

    @Override
    public void deltext(double e) {
        super.resume(e);
        Job job = iJob.getSingleValue();
        if (processingJob == null) {
            processingJob = job;
            super.holdIn(Machine.PHASE_BUSY, processingTime);
        }
        else {
            ignoringJob = job;
            busyTime += e;
            super.holdIn(Machine.PHASE_BUSY_IGNORING, 0.0);            
        }
    }

    @Override
    public void lambda() {
        if(super.phaseIs(Machine.PHASE_BUSY)) {
            oJobSolved.addValue(processingJob);
        }
        else if(super.phaseIs(Machine.PHASE_BUSY_IGNORING)) {
            oJobIgnored.addValue(ignoringJob);
        }
    }
}
