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

package algorithms;

import model.neuralnetwork.Neuron;
import model.neuralnetwork.Synapse;


/**
 * DeltaBarDelta class that implements Delta-Bar-Delta rule described in
 * Martin Riedmiller "Advanced Supervised Learning in Multi-layer Perceptrons -
 * From Backpropagation to Adaptive Learning Algorithms"
 * @author Glowczynski Tomasz
 */
public class DeltaBarDelta extends Propagation{
    	private double momentum = 0.09;
        public DeltaBarDelta(double[] parameters) {
   
        }

    @Override
        public void changeWeights(Neuron neuron) {
            for (int i=0;i<neuron.getIncomingSyn().size();i++) {
                Synapse syn = neuron.getIncomingSyn().get(i);
                calculateLearningRate(syn);
                syn.setDelta(-syn.getLearningRate() * syn.getGradient() + momentum * syn.getDelta());
                syn.setValue(syn.getValue()+syn.getDelta());
            }
        }


    /**
     * @param momentum the momentum to set
     */
    public void setMomentum(double momentum) {
        this.momentum = momentum;
    }

    @Override
    public void initialize(Neuron neuron) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void calculateLearningRate(Synapse syn) {
        double gradient = syn.getGradient();
        double prevGradient  = syn.getPrevGradient();
        double change = gradient * prevGradient;
        if (change > 0) 
            syn.setLearningRate(0.01 + syn.getLearningRate());
        else if (change < 0)
            syn.setLearningRate(0.5 * syn.getLearningRate());
    }

}