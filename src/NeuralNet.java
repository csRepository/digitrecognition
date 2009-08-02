import java.util.ArrayList;


public class NeuralNet {
	
	public ArrayList layers;

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
