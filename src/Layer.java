import java.util.ArrayList;

public class Layer extends ArrayList{
	
	//public ArrayList neurons;

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
