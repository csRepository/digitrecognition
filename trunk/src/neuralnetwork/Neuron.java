package neuralnetwork;

import java.util.ArrayList;

/**
 * Neuron: Implementacja neuronu w sieci neuronowej
 * @author tm
 */
public class Neuron {

	private double value;				//wartosc neuronu
        private double delta;	
	private ArrayList<Synapse> incomingSyn;
        private ArrayList<Synapse> outgoingSyn;            // lista zawieraj¹ca sysnapsy wychodz¹ce z neuronu

	public Neuron() {
		incomingSyn = new ArrayList<Synapse>();
		outgoingSyn = new ArrayList<Synapse>();
	}

        /**
         * Wyliczenie waroœci wyjœcia neuronu po zastosowaniu funkcji aktywacji
         */
	public void computeOutput()
	  {
	    double sum=0.0;
	    for (int i=0; i< getIncomingSyn().size(); i++) {
	    	sum += getIncomingSyn().get(i).getValue() * getIncomingSyn().get(i).getFromNeuron().getValue();
	    }
	  this.setValue(1.0 / (1.0 + Math.exp(-sum))); // sigmoid function
	  }


    /**
     * @return the inlinks
     */
    public ArrayList<Synapse> getIncomingSyn() {
        return incomingSyn;
    }

    /**
     * @return the outlinks
     */
    public ArrayList<Synapse> getOutgoingSyn() {
        return outgoingSyn;
    }

    /**
     * @return the delta
     */
    public double getDelta() {
        return delta;
    }

    /**
     * @param delta the delta to set
     */
    public void setDelta(double delta) {
        this.delta = delta;
    }

    /**
     * @return the value
     */
    public double getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(double value) {
        this.value = value;
    }
}
