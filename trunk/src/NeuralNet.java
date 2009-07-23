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
	 * @param n liczba neuronów
	 */
	public void addLayer(int n) {
		 //new Layer(n);
		 layers.add(new Layer(n));
	}
	/**
         * Zwraca referencjê do warstwy
         * @param n numer warstwy
         * @return Layer
         */
	public Layer getLayer(int n) {
		return (Layer) layers.get(n);
	}

        /**
         * Tworzy po³¹czenie pomiêdzy neuronami
         * @param sourceLayer   numer warstwy, z której wychodzi po³¹czenie
         * @param sourceNeuron  numer neuronu, z którego wychodzi po³¹czenie
         * @param destLayer     numer warstwy docelowej po³¹czenia
         * @param destNeuron    numer neuronu docelowego
         */
	public void connect(int sourceLayer,int sourceNeuron,
		      int destLayer,int destNeuron)
	{
		new Synapse(getLayer(sourceLayer).getNeuron(sourceNeuron),
              getLayer(destLayer).getNeuron(destNeuron));
	}
}
