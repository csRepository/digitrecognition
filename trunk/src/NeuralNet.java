import java.util.ArrayList;


public class NeuralNet {
	
	public ArrayList layers;
	public Layer inputLayer;
	public Layer outputLayer;

	public NeuralNet() {
		layers = new ArrayList();
	}

	/**
	 * Metoda dodaj¹ca warstwê do struktury sieci
	 * @param n integer liczba neuronów
	 */
	public void addLayer(int n) {
		 //new Layer(n);
		 layers.add(new Layer(n));
	}
	
	public Layer getLayer(int n) {
		return (Layer) layers.get(n);
	}
	
	public void connect(int sourceLayer,int sourceNeuron,
		      int destLayer,int destNeuron)
	{
		new Synapse(getLayer(sourceLayer).getNeuron(sourceNeuron),
              getLayer(destLayer).getNeuron(destNeuron));
	}
}
