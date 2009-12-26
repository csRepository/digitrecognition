package neuralnetwork;

import java.util.ArrayList;

/**
 * Neuron: Implementation of a neuron in the Neural Network.
 * @author tm
 */
public class Neuron {

	private double value;				//wartosc neuronu
        private double delta;	
	private ArrayList<Synapse> incomingSyn;
        private ArrayList<Synapse> outgoingSyn;            // lista zawieraj�ca sysnapsy wychodz�ce z neuronu

	public Neuron() {
		incomingSyn = new ArrayList<Synapse>();
		outgoingSyn = new ArrayList<Synapse>();
	}

        /**
         * Wyliczenie wartosci wyjscia neuronu po zastosowaniu funkcji aktywacji
         */
	public void computeOutput()
	  {
	    double sum=0.0;
	    for (int i=0; i< getIncomingSyn().size(); i++) {
	    	sum += getIncomingSyn().get(i).getValue() * getIncomingSyn().get(i).getFromNeuron().getValue();
	    }
	  this.setValue(sigm(sum)); // sigmoid function
          
	  }

        public double tanh (double u)
        {
            double a = Math.exp( u );
            double b = Math.exp( -u );
            return ((a-b)/(a+b));
        }

        public double sigm (double u)
        {
            return 1.0 / (1 + Math.exp(-1.0 * u));
        }

        public double sigmDerivative(double d) {
            return d * (1.0 - d) ;
	}

        public double tanhDerivative(double d) {
            return  (1 + d) * (1 - d);
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
