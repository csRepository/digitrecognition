/**
 *  Copyright 2010 Główczyński Tomasz
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 */

package model.neuralnetwork;

import java.util.ArrayList;

/**
 * Neuron: Implementation of a neuron in the Neural Network.
 * @author Glowczynski Tomasz
 */
public class Neuron {

	private double value;				//wartosc neuronu
        private double delta;	
	private ArrayList<Synapse> incomingSyn;
        private ArrayList<Synapse> outgoingSyn;            // lista zawieraj�ca sysnapsy wychodz�ce z neuronu

	public Neuron() {
            incomingSyn = new ArrayList<Synapse>();
            outgoingSyn = new ArrayList<Synapse>();
	}

        /**
         * Wyliczenie wartosci wyjscia neuronu po zastosowaniu funkcji aktywacji
         */
	public void computeOutput() {
	    double sum=0.0;
            Synapse syn;
	    for (int i=0; i< incomingSyn.size(); i++) {
                syn = incomingSyn.get(i);
	    	sum += syn.getValue() * syn.getFromNeuron().getValue();
	    }
	  value = Activation.sigmoid(sum); // sigmoid function      
	  }

    /**
     * @return the inlinks
     */
    public ArrayList<Synapse> getIncomingSyn() {
        return incomingSyn;
    }

    /**
     * @return the outlinks
     */
    public ArrayList<Synapse> getOutgoingSyn() {
        return outgoingSyn;
    }

    /**
     * @return the delta
     */
    public double getDelta() {
        return delta;
    }

    /**
     * @param delta the delta to set
     */
    public void setDelta(double delta) {
        this.delta = delta;
    }

    /**
     * @return the value
     */
    public double getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
