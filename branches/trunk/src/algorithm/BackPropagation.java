package algorithm;

import neuralnetwork.Neuron;
import neuralnetwork.Synapse;


/**
 * BackPropagation class that implements back-propagation algorithm based on
 * <p>Rumelhart, D.E., Hinton, G.E., Williams, R.J. (1986) "Learning internal
 * representations by error propagation."</p>
 * @author tm
 */
public class BackPropagation extends Propagation{
    private double momentum;
    private double learningRate;

    public BackPropagation(double[] parameters) {
        this.momentum = parameters[1];
        this.learningRate = parameters[0];
    }

    @Override
    public void changeWeights(Neuron neuron) {
        for (int i=0;i<neuron.getIncomingSyn().size();i++) {
            Synapse syn = neuron.getIncomingSyn().get(i);
            syn.setDelta(-learningRate * syn.getGradient() + momentum * syn.getDelta());
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
    /**
     * @param learningRate the learningRate to set
     */
    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    @Override
    public void initialize(Neuron neuron) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}