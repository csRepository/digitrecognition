package propagation;

import neuralnetwork.Neuron;
import neuralnetwork.Synapse;


/**
 * DeltaBarDelta class that implements Delta-Bar-Delta rule described in
 * Martin Riedmiller "Advanced Supervised Learning in Multi-layer Perceptrons -
 * From Backpropagation to Adaptive Learning Algorithms"
 * @author tm
 */
public class DeltaBarDelta extends Propagation{
    	private double momentum = 0.09;
        public DeltaBarDelta() {
   
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

    @Override
     public void calcUpdate(Neuron neuron) {
               for (int i=0;i<neuron.getIncomingSyn().size();i++) {
                Synapse syn = neuron.getIncomingSyn().get(i);
                syn.setGradient(syn.getGradient()+getActualGradient(neuron,syn));
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