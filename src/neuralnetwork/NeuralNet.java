package neuralnetwork;

import java.util.ArrayList;
import java.util.Random;

/**
 * Class that implements the neural network.
 * @author tm
 */
public class NeuralNet {
	
	private ArrayList<Layer> layers;
        private double mse;
        private double size;    //wielkosc zbioru wzorcow

        public NeuralNet(int[] layersSizes) {
		layers = new ArrayList();
                for (int i = 0; i < layersSizes.length; i++) {
                    layers.add(new Layer(layersSizes[i]));
            }
	}

        public NeuralNet() {
            layers = new ArrayList();
        }

	/**
	 * Adding layer to network
	 * @param n liczba neuron�w
	 */
	public void addLayer(int n) {
            layers.add(new Layer(n));
	}
	public Layer getLayer(int n) {
            return layers.get(n);
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
	private void connect(int sourceLayer,int sourceNeuron,
		      int destLayer,int destNeuron)
	{
            new Synapse(layers.get(sourceLayer).getNeuron(sourceNeuron),
              layers.get(destLayer).getNeuron(destNeuron));
	}

             /**
     * Tworzy połączenie pomiędzy wszystkimi neuronami dwóch warstw
     * (łączy warstwy ze sobą)
     * @param n             liczba neuronow war. 1
     * @param n1            liczba neuronow war. 2
     * @param sourceLayer   nazwa warstwy zrodlwoej
     * @param destLayer     nazwa warstwy docelowej
     */

    public void connectLayers(int n, int n1,int sourceLayer, int destLayer) {
        for (int i=0;i<n;i++)
            for (int j=0;j<n1;j++)
                connect(sourceLayer, i, destLayer, j);      //polaczenie neuronow
    }

        /**
         * Obliczenie wyjsc neuronow poczynajac od warstwy wejsciowej w
         * kierunku wyjsciowej
         */
        public void passForward(){
            Neuron neuron;
            int layersSize = layers.size();
            int out = 0; //czy aktualna warstwa to war. wyjsciowa (0 - nie)
            for (int i = 1; i < layersSize; i++) {
                Layer layer = layers.get(i);
                if ( i == layersSize - 1 ) out = 1;
                for (int j = 0; j < layer.size() - 1 + out; j++) {
                    neuron = layer.getNeuron(j);
                    neuron.computeOutput();
		}
            }
        }

        /**
         * Obliczenie bledu dla kazdego neuronu wyjsciwego na podstawie
         * wartosci porzadanych oraz sumy kwadratow wszystkich bledow
         * danego wzorca
         * @param desAns tablica porzadanych odpowiedzi
         */
        public double calculateError(double [] desAns) {
            Layer layer = layers.get(layers.size()-1);
            size += 1;  //liczba przetworzonych wzorcow
            double newError = 0;
            double error;
            int layerSize = layer.size();
            for (int i = 0; i < layerSize; i++) {
                error = desAns[i] - layer.getNeuron(i).getValue();
                newError += error*error;
            }
             newError /= 10;
          
             mse += newError ;
             return newError;
        }

        /**
         * Calculate Root Mean Square Error (RMS)
         * @return rms error
         */
        public double calculateRMS() {
            double errorRMS = Math.sqrt(mse/size);
            size=0;
            mse=0;
            return errorRMS;
        }

        public void initializeWeights(long seed) {
            Neuron neuron;
            Random random = new Random(seed);
            int layersSize = layers.size();
            for (int i = 1; i < layersSize; i++) {
                Layer layer = layers.get(i);
                for (int j = 0; j < layer.size(); j++) {
                    neuron = layer.getNeuron(j);
                    double synCount = neuron.getIncomingSyn().size();
                    for (int k = 0; k < synCount; k++) {
                        Synapse syn = neuron.getIncomingSyn().get(k);
                        double value = random.nextDouble() * 2 * 2.38/Math.sqrt(synCount) - 2.38/Math.sqrt(synCount);
                        syn.setValue(value);
                    }
		}
            }
        }
        public void initializeWeightsDefault(long seed) {
            Neuron neuron;
            Random random = new Random(seed);
            int layersSize = layers.size();
            for (int i = 1; i < layersSize; i++) {
                Layer layer = layers.get(i);
                for (int j = 0; j < layer.size(); j++) {
                    neuron = layer.getNeuron(j);
                    for (int k = 0; k < neuron.getIncomingSyn().size(); k++) {
                        Synapse syn = neuron.getIncomingSyn().get(k);
                        double value = random.nextDouble() * 2 * 0.25 - 0.25;
                        syn.setValue(value);
                    }
		}
            }
        }

}
