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
 * ResilentPropagation class that implements resilent-propagation algorithm (RPROP)
 * inherits from Propagation class
 * @author Glowczynski Tomasz
 */
public class ResilentPropagation extends Propagation{
    private double DELTAMAX;
    private double DELTAMIN;
    private double DELTAZERO;
    private double DECFACTOR;
    private double INCFACTOR;

    public ResilentPropagation(double[] parameters) {
        this.DELTAMAX  = parameters[0];
        this.DELTAMIN  = parameters[1];
        this.DELTAZERO = parameters[2];
        this.DECFACTOR = parameters[3];
        this.INCFACTOR = parameters[4];
    }

    /**
     * Implements iRprop+ algorithm
     * @param the neuron whose incoming weights will be changed
     * @param lastError last root mean square error of whole epoch
     * @param lastRmsError   actual root mean square error of whole epoch
     */
    public void changeWeights(Neuron neuron, double lastRmsError, double rmsError){
          for (int i=0;i<neuron.getIncomingSyn().size();i++) {
                Synapse syn = neuron.getIncomingSyn().get(i);

                // compute product of accumulated error for the weight
                // for this epoch  and the last epoch
                double change = syn.getPrevGradient()*syn.getGradient();
                double gradient = syn.getGradient(); //
                double synValue = syn.getValue();

              //  double weightChange = 0;
                 if(change>0) {                      //no sign change
                     syn.setUpdateValue(Math.min(syn.getUpdateValue()*INCFACTOR, DELTAMAX));
                     syn.setDelta(-Math.signum(gradient)*syn.getUpdateValue());
                     syn.setValue(synValue + syn.getDelta());
                   
                 }
                 else if (change<0) {               //sign change
                     syn.setUpdateValue(Math.max(syn.getUpdateValue()*DECFACTOR, DELTAMIN));
                     if (rmsError > lastRmsError) syn.setValue(synValue - syn.getDelta());
                     syn.setGradient(0.0);
                 }
                 else {              
                     syn.setDelta(-Math.signum(gradient)*syn.getUpdateValue());
                     syn.setValue(synValue + syn.getDelta());
                 }
            }
    }
    /**
     * Implements iRprop- algorithm
     * @param neuron the neuron whose incoming weights will be changed
     */
    @Override
    public void changeWeights(Neuron neuron){
      for (int i=0;i<neuron.getIncomingSyn().size();i++) {
            Synapse syn = neuron.getIncomingSyn().get(i);

            // compute product of accumulated error for the weight
            // for this epoch  and the last epoch
            double change = syn.getPrevGradient()*syn.getGradient();
            double gradient = syn.getGradient(); //

             if(change>0) {                      //no sign change
                 syn.setUpdateValue(Math.min(syn.getUpdateValue()*INCFACTOR, DELTAMAX));
                 //syn.setDelta(Math.signum(gradient)*syn.getUpdateValue());
                 //syn.setValue(syn.getValue()+syn.getDelta());
             }
             else if (change<0) {               //sign change
                 syn.setUpdateValue(Math.max(syn.getUpdateValue()*DECFACTOR, DELTAMIN));
                 //if (rms > lastError) syn.setValue(syn.getValue()-syn.getDelta());
                 syn.setGradient(0.0);
             }
             //else {
                 syn.setDelta(-Math.signum(gradient)*syn.getUpdateValue());
                 syn.setValue(syn.getValue()+syn.getDelta());
           //  }
        }
    }

    /**
     * Initialize weights update value
     * @param neuron the neuron whose incoming weights will be changed
     */
    @Override
    public void initialize(Neuron neuron) {
        ArrayList<Synapse> incSyns = neuron.getIncomingSyn();
        for (int i=0; i < incSyns.size(); i++) {
            Synapse syn = incSyns.get(i);
            syn.setUpdateValue(DELTAZERO);
        }
    }
}
    