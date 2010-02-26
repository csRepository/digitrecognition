package neuralnetwork;

import java.util.ArrayList;

/**
 * Neuron: Implementation of a neuron in the Neural Network.
 * @author Glowczynski Tomasz
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
	public void computeOutput() {
	    double sum=0.0;
            Synapse syn;
	    for (int i=0; i< incomingSyn.size(); i++) {
                syn = incomingSyn.get(i);
	    	sum += syn.getValue() * syn.getFromNeuron().getValue();
	    }
	  value = sigm(sum); // sigmoid function
          
	  }

        private static  double tanh (double u)
        {
            double a = Math.exp( u );
            double b = Math.exp( -u );
            return ((a-b)/(a+b));
        }

        private static double sigm (double u)
        {
            return 1.0 / (1 + Math.exp(-1.0 * u));
        }

        public  double sigmDerivative(double d, double primeTerm) {
            return d * (1.0 - d) + primeTerm;
	}

        public  double tanhDerivative(double d) {
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

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}