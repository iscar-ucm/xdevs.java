/*
 * Copyright (C) 2014-2016 José Luis Risco Martín <jlrisco@ucm.es>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *  - José Luis Risco Martín
 */
package xdevs.core.examples.devstone;

import java.util.ArrayList;

import org.apache.commons.math3.distribution.RealDistribution;
import xdevs.core.modeling.Port;

/**
 * Coupled model to study the performance HO DEVStone models
 *
 * @author José Luis Risco Martín
 */
public class DevStoneCoupledHOmod extends DevStone {

    public Port<Integer> iInAux = new Port<>("inAux");

    public DevStoneCoupledHOmod(String prefix, int width, int depth, double preparationTime, double intDelayTime,
                                double extDelayTime) {
        super(prefix + (depth - 1));
        super.addInPort(iInAux);
        if (depth == 1) {
            DevStoneAtomic atomic = new DevStoneAtomic("A1_" + name, preparationTime, intDelayTime, extDelayTime);
            super.addComponent(atomic);
            super.addCoupling(iIn, atomic.iIn);
            super.addCoupling(atomic.oOut, oOut);
        } else {
            DevStoneCoupledHOmod coupled = new DevStoneCoupledHOmod(prefix, width, depth - 1, preparationTime,
                    intDelayTime, extDelayTime);
            super.addComponent(coupled);
            super.addCoupling(iIn, coupled.iIn);
            super.addCoupling(coupled.oOut, oOut);
            // First layer of atomic models:
            ArrayList<DevStoneAtomic> prevLayer = new ArrayList<>();
            for (int i = 0; i < (width - 1); ++i) {
                DevStoneAtomic atomic = new DevStoneAtomic("AL1_" + (i + 1) + "_" + name, preparationTime, intDelayTime,
                        extDelayTime);
                super.addComponent(atomic);
                super.addCoupling(iInAux, atomic.iIn);
                super.addCoupling(atomic.oOut, coupled.iInAux);
                prevLayer.add(atomic);
            }
            // Second layer of atomic models:
            ArrayList<DevStoneAtomic> currentLayer = new ArrayList<>();
            for (int i = 0; i < (width - 1); ++i) {
                DevStoneAtomic atomic = new DevStoneAtomic("AL2_" + (i + 1) + "_" + name, preparationTime, intDelayTime,
                        extDelayTime);
                super.addComponent(atomic);
                if (i == 0) {
                    super.addCoupling(iInAux, atomic.iIn);
                }
                currentLayer.add(atomic);
            }
            for (int i = 0; i < currentLayer.size(); ++i) {
                DevStoneAtomic fromAtomic = currentLayer.get(i);
                for (int j = 0; j < prevLayer.size(); ++j) {
                    DevStoneAtomic toAtomic = prevLayer.get(j);
                    super.addCoupling(fromAtomic.oOut, toAtomic.iIn);
                }
            }

            // Rest of the tree
            prevLayer = currentLayer;
            currentLayer = new ArrayList<>();
            int level = 3;
            while (prevLayer.size() > 1) {
                for (int i = 0; i < prevLayer.size() - 1; ++i) {
                    DevStoneAtomic atomic = new DevStoneAtomic("AL" + level + "_" + (i + 1) + "_" + name,
                            preparationTime, intDelayTime, extDelayTime);
                    super.addComponent(atomic);
                    if (i == 0) {
                        super.addCoupling(iInAux, atomic.iIn);
                    }
                    currentLayer.add(atomic);
                }
                for (int i = 0; i < currentLayer.size(); ++i) {
                    DevStoneAtomic fromAtomic = currentLayer.get(i);
                    DevStoneAtomic toAtomic = prevLayer.get(i + 1);
                    super.addCoupling(fromAtomic.oOut, toAtomic.iIn);
                }
                prevLayer = currentLayer;
                currentLayer = new ArrayList<>();
                level++;
            }
        }
    }

    public DevStoneCoupledHOmod(String prefix, int width, int depth, double preparationTime, RealDistribution distribution) {
        super(prefix + (depth - 1));
        super.addInPort(iInAux);
        if (depth == 1) {
            DevStoneAtomic atomic = new DevStoneAtomic("A1_" + name, preparationTime, distribution);
            super.addComponent(atomic);
            super.addCoupling(iIn, atomic.iIn);
            super.addCoupling(atomic.oOut, oOut);
        } else {
            DevStoneCoupledHOmod coupled = new DevStoneCoupledHOmod(prefix, width, depth - 1, preparationTime, distribution);
            super.addComponent(coupled);
            super.addCoupling(iIn, coupled.iIn);
            super.addCoupling(coupled.oOut, oOut);
            // First layer of atomic models:
            ArrayList<DevStoneAtomic> prevLayer = new ArrayList<>();
            for (int i = 0; i < (width - 1); ++i) {
                DevStoneAtomic atomic = new DevStoneAtomic("AL1_" + (i + 1) + "_" + name, preparationTime, distribution);
                super.addComponent(atomic);
                super.addCoupling(iInAux, atomic.iIn);
                super.addCoupling(atomic.oOut, coupled.iInAux);
                prevLayer.add(atomic);
            }
            // Second layer of atomic models:
            ArrayList<DevStoneAtomic> currentLayer = new ArrayList<>();
            for (int i = 0; i < (width - 1); ++i) {
                DevStoneAtomic atomic = new DevStoneAtomic("AL2_" + (i + 1) + "_" + name, preparationTime, distribution);
                super.addComponent(atomic);
                if (i == 0) {
                    super.addCoupling(iInAux, atomic.iIn);
                }
                currentLayer.add(atomic);
            }
            for (int i = 0; i < currentLayer.size(); ++i) {
                DevStoneAtomic fromAtomic = currentLayer.get(i);
                for (int j = 0; j < prevLayer.size(); ++j) {
                    DevStoneAtomic toAtomic = prevLayer.get(j);
                    super.addCoupling(fromAtomic.oOut, toAtomic.iIn);
                }
            }

            // Rest of the tree
            prevLayer = currentLayer;
            currentLayer = new ArrayList<>();
            int level = 3;
            while (prevLayer.size() > 1) {
                for (int i = 0; i < prevLayer.size() - 1; ++i) {
                    DevStoneAtomic atomic = new DevStoneAtomic("AL" + level + "_" + (i + 1) + "_" + name,
                            preparationTime, distribution);
                    super.addComponent(atomic);
                    if (i == 0) {
                        super.addCoupling(iInAux, atomic.iIn);
                    }
                    currentLayer.add(atomic);
                }
                for (int i = 0; i < currentLayer.size(); ++i) {
                    DevStoneAtomic fromAtomic = currentLayer.get(i);
                    DevStoneAtomic toAtomic = prevLayer.get(i + 1);
                    super.addCoupling(fromAtomic.oOut, toAtomic.iIn);
                }
                prevLayer = currentLayer;
                currentLayer = new ArrayList<>();
                level++;
            }
        }
    }

    @Override
    public long getNumDeltExts(int maxEvents, int width, int depth) {
        long sum = 1;
        for (int i = 1; i < depth; i++) {
            sum += ((1+(i-1)*(width-1))*(width*(width-1)/2)+(width-1)*(width+(i-1)*(width-1)));
        }
        return maxEvents * sum;
    }

    @Override
    public long getNumDeltInts(int maxEvents, int width, int depth) {
        return getNumDeltExts(maxEvents, width, depth);
    }

    @Override
    public long getNumOfEvents(int maxEvents, int width, int depth) {
        int w1 = width-1, w2 = width-2;
        int d0 = depth, d1 = depth-1, d2 = depth-2;
        long sum = 1 + 2*w1+w1*w1*d2*(2+w1*d1)+(w1*w1+(w2*w1)/2)*((2*d1+w1*d0*d1-2*w1*d1)/2);
        return maxEvents * sum;
    }

    @Override
    public int getNumOfAtomic(int width, int depth) {
        int sum = 0;
        for (int i = 1; i <= (width - 1); i++) {
            sum += i;
        }
        return ((width - 1) + sum) * (depth - 1) + 1;
    }

}
