package neuralnetwork;

import neuralnetwork.Layer;
import neuralnetwork.Neuron;
import neuralnetwork.Synapse;
import java.util.ArrayList;

/**
 * Klasa implementuj¹ca sieæ neuronow¹.
 * @author tm
 */
public class NeuralNet {
	
	public ArrayList layers;
        private double globalError;
        private double size;
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
         * Tworzy po³¹czenie (synapsê) pomiêdzy dwoma neuronami
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

        /**
         * Obliczenie wyjsc neuronow poczynajac od warstwy wejsciowej w
         * kierunku wyjsciowej
         */
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

        /**
         * Obliczenie bledu dla kazdego neuronu wyjsciwego na podstawie
         * wartosci porz¹danych oraz sumy kwadratow wszystkich bledow
         * danego wzorca
         * @param desAns tablica porzdanych odpowiedzi
         */
        public void calculateError(int [] desAns) {
            Layer layer = getLayer(layers.size()-1);
            size = desAns.length;
             double error;
            for (int i=0;i<layer.size();i++) {
                error=desAns[i]-layer.getNeuron(i).getValue();
                System.out.println(error);
                globalError += error*error;
              //  System.out.println("size"+size);

            }
        }

        /**
         * Obliczenie bledu sredniokwadratowego (root mean square)
         * @return zwraca blad rms dla jednej epoki
         */
        public double calculateRMS() {
            double errorRMS = Math.sqrt(globalError/size);
            this.globalError=0;
            return errorRMS;
        }

        public void backPropagate() {

        }
}
