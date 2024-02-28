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
package xdevs.core.modeling;

import xdevs.core.util.Constants;

/**
 * Abstract class for all atomic components in the DEVS formalism.
 * 
 * An atomic component is a model that can be connected to other components to
 * form a hierarchical model. It has input and output ports to communicate with
 * other components.
 */
public abstract class Atomic extends Component {

    /**
     * The phase of the atomic model. The phase is used to determine the behavior
     * of the model. Default values can be passive or active.
     */
    protected String phase = Constants.PHASE_PASSIVE;
    /**
     * The time until the next internal transition of the model. The time is used
     * to determine when the model will change its state. The time is set to
     * infinity if the model is passive.
     */
    protected double sigma = Constants.INFINITY;

    /**
     * Constructor of the atomic model.
     * @param name The name of the atomic model.
     */
    public Atomic(String name) {
        super(name);
    }

    /**
     * Constructor of the atomic model.
     */
    public Atomic() {
        this(Atomic.class.getSimpleName());
    }

    /**
     * Method to get the time until the next internal transition of the model.
     * @return The time until the next internal transition of the model.
     */
    public double ta() {
        return sigma;
    }

    /**
     * Internal transition method of the atomic model. This method is called when
     * the time until the next internal transition of the model is zero.
     */
    public abstract void deltint();

    /**
     * External transition method of the atomic model. This method is called when
     * the model receives an input from another model.
     * @param e The time elapsed since the last internal transition of the model.
     */
    public abstract void deltext(double e);

    public void deltcon(double e) {
        deltint();
        deltext(0);
    }

    /**
     * Output function of the atomic model. This method is called when the model
     * has an output to send to another model.
     */
    public abstract void lambda();

    /**
     * Method to updated sigma in the external transition function.
     * @param e The time elapsed since the last internal transition of the model.
     */
    public void resume(double e) {
        sigma = sigma - e;
    }

    /**
     * Method to hold the model in a given phase for a given time.
     * @param phase The phase to hold the model in.
     * @param sigma The time to hold the model in the phase.
     */
    public void holdIn(String phase, double sigma) {
        this.phase = phase;
        this.sigma = sigma;
    }

    /**
     * Method to activate the model with sigma 0.
     */
    public void activate() {
        this.phase = Constants.PHASE_ACTIVE;
        this.sigma = 0;
    }

    /**
     * Method to passivate the model with sigma infinity.
     */
    public void passivate() {
        this.phase = Constants.PHASE_PASSIVE;
        this.sigma = Constants.INFINITY;
    }

    /**
     * Method to passivate the model in a given phase.
     * @param phase The phase to passivate the model in.
     */
    public void passivate(String phase) {
        this.phase = phase;
        this.sigma = Constants.INFINITY;
    }

    /**
     * This method is deprecated. Use passivate(String phase) instead.
     * @param phase The phase to passivate the model in.
     */
    @Deprecated
    public void passivateIn(String phase) {
        this.phase = phase;
        this.sigma = Constants.INFINITY;
    }

    /**
     * Method to check if the model is in a given phase.
     * @param phase The phase to check.
     * @return True if the model is in the given phase, false otherwise.
     */
    public boolean phaseIs(String phase) {
        return this.phase.equals(phase);
    }

    /**
     * Method to get the phase of the model.
     * @return The phase of the model.
     */
    public String getPhase() {
        return phase;
    }

    /**
     * Method to set the phase of the model.
     * @param phase The phase to set the model to.
     */
    public final void setPhase(String phase) {
        this.phase = phase;
    }

    /**
     * Method to get the time until the next internal transition of the model.
     * @return The time until the next internal transition of the model.
     */
    public double getSigma() {
        return sigma;
    }

    /**
     * Method to set the time until the next internal transition of the model.
     * @param sigma The time until the next internal transition of the model.
     */
    public final void setSigma(double sigma) {
        this.sigma = sigma;
    }

    /**
     * Method to get the state of the model.
     * @return The state of the model.
     */
    public String showState() {
        StringBuilder sb = new StringBuilder(name + ":[");
        sb.append("\tstate: ").append(phase);
        sb.append("\t, sigma: ").append(sigma);
        sb.append("]");
        return sb.toString();
    }

    /**
     * Method to get the XML representation of the model.
     * @return The XML representation of the model.
     */
    public String toXml() {
        StringBuilder builder = new StringBuilder();
        StringBuilder tabs = new StringBuilder();
        Component parent = this.parent;
        int level = 0;
        while (parent!=null) {
            tabs.append("\t");
            parent = parent.parent;
            level++;
        }
        builder.append(tabs).append("<atomic name=\"").append(this.getName()).append("\"");
        builder.append(" class=\"").append(this.getClass().getCanonicalName()).append("\"");
        builder.append(" host=\"127.0.0.1\"");
        builder.append(" port=\"").append(5000 + level).append("\"");
        builder.append(">\n");
        builder.append(tabs).append("</atomic>\n");

        return builder.toString();
    }
}
