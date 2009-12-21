/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package neuralnetwork;

/**
 *
 * @author tm
 */
public class QuickPropagation extends Propagation {

    private double maxGrowthFactor;
    private double modeSwitchThreshold;
    private double learningRate;
    private double momentum;

    public QuickPropagation(double[] parameters) {
            this.learningRate = parameters[0];
            this.momentum = parameters[1];
            this.modeSwitchThreshold = parameters[2];
            this.maxGrowthFactor = parameters[3];
    }

    @Override
    public void initialize(Neuron neuron) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void calcUpdate(Neuron neuron) {
       for (int i=0;i<neuron.getIncomingSyn().size();i++) {
           Synapse syn = neuron.getIncomingSyn().get(i);
           syn.setGradient(syn.getGradient()+getActualGradient(neuron,syn));
        }
    }

    public double calculateDelta(Neuron neuron,Synapse syn) {
                double weightChange = 0.0;
                double error = syn.getGradient();
                double previousError = syn.getPrevGradient();
                double previousWeightChange = syn.getDelta();
                double shrinkFactor = (maxGrowthFactor / (1.0 + maxGrowthFactor));

                if (previousWeightChange > modeSwitchThreshold){
                    if (error > 0) {
                        weightChange += (learningRate * error);
                    }
                    if (error > (shrinkFactor * previousError)) {
                        weightChange += (maxGrowthFactor * previousWeightChange);
                    }
                    else {
                        // minimum,
                        weightChange += ((error / (previousError - error)) *
                          previousWeightChange);
                    }
                }
                else if (previousWeightChange < (-1.0 * modeSwitchThreshold)){
                    if (error < 0) {
                        weightChange += (learningRate * error);
                    }
                    if (error < (shrinkFactor * previousError)) {
                        weightChange += (maxGrowthFactor * previousWeightChange);
                    }
                    else{
                        // minimum
                        weightChange += ((error / (previousError - error)) *
                          previousWeightChange);
                    }
                }
                else {
                    // zminana wag z momentum
                    weightChange += (learningRate * error) +
                      (momentum * previousWeightChange);
                }
           return weightChange;
    }
    @Override
    public void changeWeights(Neuron neuron) {
             for (int i=0;i<neuron.getIncomingSyn().size();i++) {
                Synapse syn = neuron.getIncomingSyn().get(i);
                syn.setDelta(calculateDelta(neuron,syn));
                syn.setValue(syn.getValue()+syn.getDelta());
            }
    }

}
