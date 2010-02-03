package neuralnetwork;

import java.util.ArrayList;

/**
 * Implements the Neural Network layer. Each layer contains
 * a list of the neurons.
 * @author tm
 */
public class Layer extends ArrayList {
	
	//private ArrayList neurons;

	public Layer (int n) {
		//neurons = new ArrayList();
		for(int i=0;i<n;i++)
                {
                    add(new Neuron());
                }
	}
	/**
         * Zwraca referencje do obiektu Neuron
         * @param n numer neuronu
         * @return Neuron
         */
	public Neuron getNeuron(int n) {
		return (Neuron) get(n);
	  }
}
