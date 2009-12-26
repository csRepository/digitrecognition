/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package propagation;

import java.util.ArrayList;
import neuralnetwork.Neuron;
import neuralnetwork.Synapse;

/**
 * Propagation class
 * @author tm
 */
public abstract class Propagation {

    public void calcOutDelta(Neuron neuron, double d) {
            double value = neuron.getValue();
            neuron.setDelta(-(d - value) * neuron.sigmDerivative(value));
    }

    public void calcHiddDelta(Neuron neuron) {
            double sumErr = 0;
            double value = neuron.getValue();
            ArrayList outSynapses = neuron.getOutgoingSyn();
            for (int i = 0; i < outSynapses.size(); i++) {
                Synapse syn = (Synapse) outSynapses.get(i);
                sumErr += syn.getToNeuron().getDelta() * syn.getValue(); //delta_neuronu_warstwy_wyj * waga
            }
           neuron.setDelta(sumErr * neuron.sigmDerivative(value));
    }

    public static  double getActualGradient(Neuron neuron, Synapse syn) {
            return neuron.getDelta() * syn.getFromNeuron().getValue();
    }
    /**
     * Remember last gradient and reset actual gradient to 0.
     * @param neuron
     */
    public void resetSyn(Neuron neuron) {
       for (int i = 0; i < neuron.getIncomingSyn().size(); i++) {
            Synapse syn = neuron.getIncomingSyn().get(i);
            syn.setPrevGradient(syn.getGradient());
            syn.setGradient(0);
       }
    }

    public abstract void initialize(Neuron neuron);

    public abstract void calcUpdate(Neuron neuron);

    public abstract void changeWeights(Neuron neuron);
}

