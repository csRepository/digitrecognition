/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithms;

import java.util.ArrayList;
import neuralnetwork.Activation;
import neuralnetwork.Neuron;
import neuralnetwork.Synapse;

/**
 * Propagation class
 * @author Glowczynski Tomasz
 */
public abstract class Propagation {

    public void calcOutDelta(Neuron neuron, double d) {
            double value = neuron.getValue();
            double error = d - value;
            neuron.setDelta(-(error) * Activation.sigmDerivative(value));
    }

    public void calcHiddDelta(Neuron neuron) {
            double sumErr = 0;
            double value = neuron.getValue();
            ArrayList<Synapse> outSynapses = neuron.getOutgoingSyn();
            for (int i = 0; i < outSynapses.size(); i++) {
                Synapse syn =  outSynapses.get(i);
                sumErr += syn.getToNeuron().getDelta() * syn.getValue(); //delta_neuronu_warstwy_wyj * waga
            }
           neuron.setDelta(sumErr * Activation.sigmDerivative(value));
    }
   /**
     * Calculate gradient
     * @param neuron   the neuron whose incoming weights will be changed
     */
    public  void calcUpdate(Neuron neuron, double decay) {
        ArrayList<Synapse> incSyns = neuron.getIncomingSyn();
        int size = incSyns.size();
        double delta = neuron.getDelta();
        for (int i=0;i<size;i++) {
           Synapse syn = incSyns.get(i);
           double gradient = getActualGradient(delta, syn);//neuron.getDelta() * syn.getFromNeuron().getValue();
           if (decay != 0)
           if (i!=size-1) //nie uwzgledniamy synaps biasu przy zanikaniu wag
                gradient += decay * syn.getValue();
            syn.setGradient(syn.getGradient() + gradient);
        }
    }
    /**
     * Remember last gradient and reset actual gradient to 0.
     * @param neuron
     */
    public void resetSyn(Neuron neuron) {
       ArrayList<Synapse> incSyns = neuron.getIncomingSyn();
       for (int i = 0; i < incSyns.size(); i++) {
            Synapse syn = incSyns.get(i);
            syn.setPrevGradient(syn.getGradient());
            syn.setGradient(0);
       }
    }

    public abstract void initialize(Neuron neuron);

    public abstract void changeWeights(Neuron neuron);

    private static  double getActualGradient(double delta, Synapse syn) {
        return delta * syn.getFromNeuron().getValue();
    }
    private static double weightDecay(Synapse syn, double decay) {
        return decay * syn.getValue();//weight decay
    }
    private static double weightElimination(Synapse syn, double decay) {
        return (decay * syn.getValue()) /
                Math.pow(0.5 + syn.getValue()*syn.getValue(), 2); //weight elimination
    }

    public static void getInstance() {
        
    }
}

