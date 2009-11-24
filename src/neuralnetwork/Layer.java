package neuralnetwork;

import java.util.ArrayList;

/**
 * Klasa implementuj�ca warstw� sieci neuronowej. Ka�da warstwa zawiera
 * list� neuron�w.
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
         * Zwraca referencj� do obiektu Neuron
         * @param n numer neuronu
         * @return Neuron
         */
	public Neuron getNeuron(int n) {
		return (Neuron) get(n);
	  }
}
