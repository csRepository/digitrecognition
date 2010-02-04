package algorithm;

import neuralnetwork.Neuron;
import neuralnetwork.Synapse;


/**
 * SuperSAB class that implements SuperSAB algorithm based on
 * <p>W. Schiffmann, M. Joost, R. Werner September 29, 1994 "Optimization of
 * the Backpropagation Algorithm for Training Multilayer Perceptrons"</p>
 * @author tm
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