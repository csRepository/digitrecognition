/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithm;

import java.util.ArrayList;
import neuralnetwork.Neuron;
import neuralnetwork.Synapse;

/**
 * QuickPropagation class that implements quick-propagation algorithm
 * inherits from Propagation class
 * @author tm
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
        double shrinkFactor = (maxGrowthFactor / (1.0 + maxGrowthFactor));
        double epsilon = (learningRate * error) / incSynSize;
        //System.out.printf("%fe4\n",learningRate/incSynSize);

        if (previousWeightChange > modeSwitchThreshold){
            if (error > 0) {
                weightChange += epsilon;
            }
            if (error > (shrinkFactor * previousError)) {
                weightChange += maxGrowthFactor * previousWeightChange;
            }
            else {
                // minimum,
                weightChange += ((error / (previousError - error)) *
                  previousWeightChange);
            }
        }
        else if (previousWeightChange < (-1.0 * modeSwitchThreshold)){
            if (error < 0) {
                weightChange += epsilon;
            }
            if (error < (shrinkFactor * previousError)) {
                weightChange += maxGrowthFactor * previousWeightChange;
            }
            else{
                // minimum
                weightChange += ((error / (previousError - error)) *
                  previousWeightChange);
            }
        }
        else {
            // zminana wag z momentum
            weightChange += (epsilon) +
              (momentum * previousWeightChange);
        }
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