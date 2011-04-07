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

import java.util.ArrayList;
import model.neuralnetwork.Neuron;
import model.neuralnetwork.Synapse;

/**
 * QuickPropagation class that implements quick-propagation algorithm
 * inherits from Propagation class
 * @author Glowczynski Tomasz
 */
public class QuickPropagation extends Propagation {

    private final double maxGrowthFactor;
    private final double modeSwitchThreshold;
    private final double learningRate;
    private final double momentum;

    public QuickPropagation(double[] parameters) {
        learningRate        = parameters[0];
        momentum            = parameters[1];
        modeSwitchThreshold = parameters[2];
        maxGrowthFactor     = parameters[3];
    }

    private  double calculateDelta(Synapse syn, int incSynSize) {
        double weightChange = 0.0;
        double error = -syn.getGradient();
        double previousError = -syn.getPrevGradient();
        double previousWeightChange = syn.getDelta();
        double shrinkFactor = maxGrowthFactor / (1.0 + maxGrowthFactor);
        double epsilon = (learningRate * error) / incSynSize;
        //System.out.printf("%fe4\n",learningRate/incSynSize);

        if (previousWeightChange > modeSwitchThreshold){
            if (error > 0) 
                weightChange += epsilon;
            if (error > shrinkFactor * previousError) 
                weightChange += maxGrowthFactor * previousWeightChange;          
            else  // minimum,
                weightChange += error / (previousError - error) * previousWeightChange;
        }
        else if (previousWeightChange < (-1.0 * modeSwitchThreshold)){
            if (error < 0) 
                weightChange += epsilon;
            if (error < (shrinkFactor * previousError)) 
                weightChange += maxGrowthFactor * previousWeightChange;
            else // minimum
                weightChange += error / (previousError - error) * previousWeightChange;
        }
        else  // zminana wag z momentum
            weightChange += epsilon + momentum * previousWeightChange;
        
   return weightChange;
    }

    @Override
    public void changeWeights(Neuron neuron) {
         Synapse syn;
         ArrayList<Synapse> incSyn = neuron.getIncomingSyn();
         int incSynSize = incSyn.size();
             for (int i=0;i < incSynSize;i++) {
                syn = incSyn.get(i);
                double delta = calculateDelta(syn,incSynSize);
                syn.setDelta(delta);
                syn.setValue(syn.getValue() + delta);
            }
    }

 @Override
  public void initialize(Neuron neuron) {
        throw new UnsupportedOperationException("Not supported yet.");
 }
}