package neuralnetwork;

import java.util.ArrayList;

/**
 * Klasa implementuj�ca sie� neuronow�.
 * @author tm
 */
public class NeuralNet {
	
	private ArrayList layers;
        private double globalError;
        private double size;    //wielkosc zbioru wzorcow

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
	public Layer getLayer(int n) {
		return (Layer) layers.get(n);
	}
        public ArrayList getLayers() {
            return layers;
        }


        /**
         * Tworzy po��czenie (synaps�) pomi�dzy dwoma neuronami
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
                  //  System.out.println("neuron("+j+")="+neuron.getValue());
		}
            }
        }

        /**
         * Obliczenie bledu dla kazdego neuronu wyjsciwego na podstawie
         * wartosci porzadanych oraz sumy kwadratow wszystkich bledow
         * danego wzorca
         * @param desAns tablica porzadanych odpowiedzi
         */
        public void calculateError(int [] desAns) {
            Layer layer = getLayer(layers.size()-1);
            size += 1;  //liczba przetworzonych wzorcow
          // size = 1;
            double newError = 0;
            double error;
            for (int i=0;i<layer.size();i++) {
                error = desAns[i]-layer.getNeuron(i).getValue();
                newError += error*error;
               
               
               // System.out.println("global"+globalError);

            }
             
             globalError += newError/10;

        }

        /**
         * Obliczenie bledu sredniokwadratowego (root mean square)
         * @return zwraca blad rms
         */
        public double calculateRMS() {
            double errorRMS = Math.sqrt(globalError/size);
            this.size=0;
            this.globalError=0;
            return errorRMS;
        }

}
