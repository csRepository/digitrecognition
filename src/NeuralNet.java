import java.util.ArrayList;


public class NeuralNet {
	
	public ArrayList layers;

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
        public void propagate(){
            Neuron neuron;
            int out = 0; //czy aktualna warstwa to war. wyjsciowa (0 - nie)
            for (int i=1;i<layers.size();i++) {
                Layer layer = getLayer(i);
                if (i==layers.size()-1) out=1;
                for (int j=0;j<layer.size()-1+out;j++) {
                    neuron = (Neuron) layer.getNeuron(j);
                    neuron.computeOutput();
                    System.out.println("neuron("+j+")="+neuron.getValue());
		}
            }
        }
}
