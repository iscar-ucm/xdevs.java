/*
* Copyright (C) 2024 José Luis Risco Martín <jlrisco@ucm.es>
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

package xdevs.core.modeling;

import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;

import xdevs.core.examples.efp.Ef;
import xdevs.core.examples.efp.Processor;
import xdevs.core.simulation.Coordinator;
import xdevs.core.util.Constants;
import xdevs.core.util.DevsLogger;

public class Coupled2Atomic extends Atomic {
    protected Coupled coupled;

    public Coupled2Atomic(Coupled model) {
        super(model.getName());
        this.coupled = model;
        for (Port<?> port : coupled.getInPorts()) {
            super.addInPort(port);
        }
        for (Port<?> port : coupled.getOutPorts()) {
            super.addOutPort(port);
        }
    }

    @Override
    public void initialize() {
        initialize(coupled);
        super.setPhase("PHASE_" + super.getName());
    }

    @Override
    public void exit() {
        exit(coupled);
    }

    @Override
    public void deltint() {
        deltfcn(super.getSigma(), coupled);
    }

    @Override
    public void deltext(double e) {        
        deltfcn(e, coupled);
        // Important: super.resume(e) must go here, at the end.
        super.resume(e);
    }

    @Override
    public void deltcon(double e) {
        deltfcn(e, coupled);
    }

    @Override
    public void lambda() {
        lambda(coupled);
    }

    @Override
    public double ta() {
        super.setSigma(ta(coupled));
        return super.getSigma();
    }

    private void initialize(Coupled model) {
        for (Component component : model.getComponents()) {
            if (component instanceof Atomic) {
                component.initialize();
            } else if (component instanceof Coupled) {
                initialize((Coupled)component);
            }
        }
    }

    private void exit(Coupled model) {
        for (Component component : model.getComponents()) {
            if (component instanceof Atomic) {
                component.exit();
            } else if (component instanceof Coupled) {
                exit((Coupled)component);
            }
        }
    }

    private void deltfcn(double e, Coupled model) {
        if(!model.isInputEmpty())
            propagateInput(model);
        for (Component component : model.getComponents()) {
            if (component instanceof Atomic) {
                Atomic atomic = (Atomic)component;
                if (!atomic.isInputEmpty()) {
                    if (e == atomic.getSigma()) {
                        atomic.deltcon(e);
                    } 
                    else {                        
                        atomic.deltext(e);
                    }
                }
                else if (e == atomic.getSigma()) {
                        atomic.deltint();
                }
            } else if (component instanceof Coupled) {
                deltfcn(e, (Coupled)component);
            }
        }
        clear(model);
    }

    private void lambda(Coupled model) {
        for (Component component : model.getComponents()) {
            if (component instanceof Atomic) {
                Atomic atomic = (Atomic)component;
                if(atomic.getSigma()==super.getSigma())
                    atomic.lambda();
            } else if (component instanceof Coupled) {
                lambda((Coupled)component);                
            }
        }
        propagateOutput(model);
    }

    private void propagateInput(Coupled model) {
        LinkedList<Coupling<?>> eic = model.getEIC();
        eic.forEach((c) -> {
            c.propagateValues();
        });
    }

    private void propagateOutput(Coupled model) {
        LinkedList<Coupling<?>> ic = model.getIC();
        ic.forEach((c) -> {
            c.propagateValues();
        });

        LinkedList<Coupling<?>> eoc = model.getEOC();
        eoc.forEach((c) -> {
            c.propagateValues();
        });
    }

    private double ta(Coupled model) {
        double sigma = Constants.INFINITY;
        for (Component component : model.getComponents()) {
            if (component instanceof Atomic) {
                Atomic atomic = (Atomic)component;
                if (atomic.ta() < sigma) {
                    sigma = atomic.ta();
                }
            } else if (component instanceof Coupled) {
                double sigma_aux = ta((Coupled)component);
                if (sigma_aux < sigma) {
                    sigma = sigma_aux;
                }
            }
        }
        return sigma;
    }

    private void clear(Coupled model) {
        for (Component component : model.getComponents()) {
            if (component instanceof Atomic) {
                Collection<Port<?>> inPorts;
                inPorts = component.getInPorts();
                inPorts.forEach((port) -> {
                    port.clear();
                });
                Collection<Port<?>> outPorts;
                outPorts = component.getOutPorts();
                outPorts.forEach((port) -> {
                    port.clear();
                });    
            }
            else if (component instanceof Coupled) {
                clear((Coupled)component);
            }
        }
        Collection<Port<?>> inPorts;
        inPorts = model.getInPorts();
        inPorts.forEach((port) -> {
            port.clear();
        });
        Collection<Port<?>> outPorts;
        outPorts = model.getOutPorts();
        outPorts.forEach((port) -> {
            port.clear();
        });
    }

    public static void main(String[] args) {
        DevsLogger.setup(Level.FINE);
        Coupled coupled = new Coupled("Coupled2Atomic-EFP");
        Processor processor = new Processor("processor", 3);
        coupled.addComponent(processor);
        Atomic ef = new Coupled2Atomic(new Ef("ef", 1, 100));
        coupled.addComponent(ef);
        coupled.addCoupling(ef.getOutPort("out"), processor.getInPort("in"));
        coupled.addCoupling(processor.getOutPort("out"), ef.getInPort("in"));


        Coordinator coordinator = new Coordinator(coupled);
        coordinator.initialize();
        coordinator.simulate(Long.MAX_VALUE);
        coordinator.exit();
    }
}
