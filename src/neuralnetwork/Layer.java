package neuralnetwork;

import java.util.ArrayList;

/**
 * Klasa implementuj¹ca warstwê sieci neuronowej. Ka¿da warstwa zawiera
 * listê neuronów.
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
         * Zwraca referencjê do obiektu Neuron
         * @param n numer neuronu
         * @return Neuron
         */
	public Neuron getNeuron(int n) {
		return (Neuron) get(n);
	  }
}
