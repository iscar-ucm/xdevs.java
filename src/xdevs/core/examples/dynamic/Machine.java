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

import java.util.Deque;
import java.util.LinkedList;

import xdevs.core.examples.efp.Job;
import xdevs.core.modeling.Atomic;
import xdevs.core.modeling.Port;

public class Machine extends Atomic {

    protected Port<Job> iJob = new Port<>("iJob");
    protected Port<Job> oJob = new Port<>("oJob");
    protected Port<Double> oServiceTime = new Port<>("oServiceTime");
    protected Deque<Job> queue = new LinkedList<>();
    protected double processingTime;
    protected double clock;

    public Machine(String name, double processingTime) {
        super(name);
        super.addInPort(iJob);
        super.addOutPort(oJob);
        this.processingTime = processingTime;
        this.clock = 0;

    }

    @Override
    public void initialize() {
        queue.clear();
        super.passivate();
    }

    @Override
    public void exit() {
    }

    @Override
    public void deltint() {
        clock += super.getSigma();
        queue.poll();
        if(queue.isEmpty())
            super.passivate();
        else
            super.holdIn("busy", processingTime);
    }

    @Override
    public void deltext(double e) {
        super.resume(e);
        clock += e;
        iJob.getValues().forEach((job) -> {
            if(queue.isEmpty())
                super.holdIn("busy", processingTime);
                job.setTime(clock);
                queue.add(job);
        });
    }

    @Override
    public void lambda() {
        oJob.addValue(queue.peek());
        oServiceTime.addValue(processingTime*(queue.size()-1));
    }
}
