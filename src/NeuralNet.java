import java.util.ArrayList;


public class NeuralNet {
	
	public ArrayList layers;
	public Layer inputLayer;
	public Layer outputLayer;

	public NeuralNet() {
		layers = new ArrayList();
	}

	/**
	 * Metoda dodaj�ca warstw� do struktury sieci
	 * @param n liczba neuron�w
	 */
	public void addLayer(int n) {
		 //new Layer(n);
		 layers.add(new Layer(n));
	}
	/**
         * Zwraca referencj� do warstwy
         * @param n numer warstwy
         * @return Layer
         */
	public Layer getLayer(int n) {
		return (Layer) layers.get(n);
	}

        /**
         * Tworzy po��czenie pomi�dzy neuronami
         * @param sourceLayer   numer warstwy, z kt�rej wychodzi po��czenie
         * @param sourceNeuron  numer neuronu, z kt�rego wychodzi po��czenie
         * @param destLayer     numer warstwy docelowej po��czenia
         * @param destNeuron    numer neuronu docelowego
         */
	public void connect(int sourceLayer,int sourceNeuron,
		      int destLayer,int destNeuron)
	{
		new Synapse(getLayer(sourceLayer).getNeuron(sourceNeuron),
              getLayer(destLayer).getNeuron(destNeuron));
	}
}
