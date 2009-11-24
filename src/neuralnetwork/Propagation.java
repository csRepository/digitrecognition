/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package neuralnetwork;

import java.util.ArrayList;

/**
 *
 * @author tm
 */
public abstract class Propagation {

    public void calcOutError(Neuron neuron,double d) {
            double value = neuron.getValue();
            neuron.setDelta( (d -value) * value * (1.0 - value));
           // System.out.println("delta"+delta);
        }

   public void calcHiddError(Neuron neuron) {
            double sumErr = 0;
            double value = neuron.getValue();
            ArrayList outSynapses = neuron.getOutgoingSyn();
            for (int i=0;i<outSynapses.size();i++) {
              Synapse syn = (Synapse) outSynapses.get(i);
              sumErr+= syn.getToNeuron().getDelta()*syn.getValue(); //delta_neuronu_warstwy_wyj * waga
            }
           neuron.setDelta(sumErr * value * (1.0 - value) );
        }
  public static  double getActualGradient(Neuron neuron,Synapse syn) {
            return neuron.getDelta()*syn.getFromNeuron().getValue();
        }
  public void resetSyn(Neuron neuron) {
       for (int i=0;i<neuron.getIncomingSyn().size();i++) {
            Synapse syn = neuron.getIncomingSyn().get(i);
         // syn.setDelta(0);
            syn.setGradient(0);
       }
    }

    public abstract void initialize(Neuron neuron);

    public abstract void calcUpdate(Neuron neuron);

    public abstract void changeWeights(Neuron neuron);
}

