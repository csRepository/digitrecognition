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
 * SuperSAB class that implements SuperSAB algorithm based on
 * <p>W. Schiffmann, M. Joost, R. Werner September 29, 1994 "Optimization of
 * the Backpropagation Algorithm for Training Multilayer Perceptrons"</p>
 * @author Glowczynski Tomasz
 */
public class SuperSAB extends Propagation{
    	private double momentum = 0.9;
        public SuperSAB(double[] parameters) {
   
        }

    @Override
        public void changeWeights(Neuron neuron) {
            for (int i=0;i<neuron.getIncomingSyn().size();i++) {
                Synapse syn = neuron.getIncomingSyn().get(i);
                computeChanges(syn);
               // syn.setDelta(-syn.getLearningRate() * syn.getGradient() + momentum * syn.getDelta());
               // syn.setValue(syn.getValue()+syn.getDelta());
                
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

    private void computeChanges(Synapse syn) {
        double gradient = syn.getGradient();
        double prevGradient  = syn.getPrevGradient();
        double change = gradient * prevGradient;
        if (change >= 0 && syn.getLearningRate() <= 100) {
            syn.setLearningRate(1.25 * syn.getLearningRate());
            syn.setDelta(-syn.getLearningRate() * syn.getGradient() + momentum * syn.getDelta());
            syn.setValue(syn.getValue()+syn.getDelta());
        }
        else if (change < 0) {
            syn.setLearningRate(0.6 * syn.getLearningRate());
            syn.setValue(syn.getValue()-syn.getDelta());
            syn.setDelta(0);
        }
      //  else syn.setLearningRate(syn.getLearningRate());
    }

}