package algorithms;

import java.util.ArrayList;
import neuralnetwork.Neuron;
import neuralnetwork.Synapse;


/**
 * BackPropagation class that implements back-propagation algorithm based on
 * <p>Rumelhart, D.E., Hinton, G.E., Williams, R.J. (1986) "Learning internal
 * representations by error propagation."</p>
 * @author Glowczynski Tomasz
 */
public class BackPropagation extends Propagation{
    private double momentum;
    private double learningRate;

    public BackPropagation(double[] parameters) {
        this.learningRate = parameters[0];
        this.momentum = parameters[1];
    }

    @Override
    public void changeWeights(Neuron neuron) {
        Synapse syn;
        ArrayList<Synapse> incSyns = neuron.getIncomingSyn();
        int synSize = incSyns.size();
        for (int i=0;i< synSize;i++) {
            syn = incSyns.get(i);
            double delta = -learningRate * syn.getGradient() + momentum * syn.getDelta();
            syn.setDelta(delta);
            syn.setValue(syn.getValue() + delta);
           // syn.setValue(syn.getValue()*(1-(0.001*learningRate)/Math.pow(1+syn.getValue()*syn.getValue(),2))); //weight decay
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