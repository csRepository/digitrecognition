import neuralnetwork.NeuralNet;
import neuralnetwork.Layer;
import neuralnetwork.Neuron;
import database.*;


public class Main {

	private static Layer OutputLayer;				// warstwa wyjsciowa
	private static Layer InputLayer;				// warstwa wejsciowa
	private static Neuron Neuron;
	private static Layer HiddenLayer;
        private static int nIn,nHidd,nOut;
        private static NeuralNet neuralNetwork;
        private static MNISTDatabase Data;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

                nIn=28*28+1; //wielkosc obrazu + bias
                nHidd=nIn*2/3+1;  //l. neuronow + bias
                nOut=10;
		neuralNetwork = new NeuralNet(); 	// tworzenie sieci neuronwej
		neuralNetwork.addLayer(nIn);	   		// dodanie warstwy wejsciowej
		neuralNetwork.addLayer(nHidd);  		// dodanie warstwy ukrytej
		neuralNetwork.addLayer(nOut);  			// dodanie warstwy wyjsciowej

		InputLayer = neuralNetwork.getLayer(0);
		HiddenLayer = neuralNetwork.getLayer(1);
		OutputLayer = neuralNetwork.getLayer(2);

                //wczytywanie obrazu z bazy MNIST
                Data = new MNISTDatabase();
                int [][] image = Data.readImage("training",5);
                int label = Data.readLabel("training",5);
                System.out.println(label);

                int neuNr=0;
                for (int j=0;j<image.length;j++) {
                    for (int k=0;k<image.length;k++) {
                        double vn=normalize(image[j][k],255,0,1,0);
                        InputLayer.getNeuron(neuNr).setValue(vn);
                        neuNr++;
                    }
                }
			
                 InputLayer.getNeuron(InputLayer.size()-1).setValue(1); //bias
                 HiddenLayer.getNeuron(HiddenLayer.size()-1).setValue(1);//bias
                 connLayer(nIn, nHidd-1,0,1);       //polaczenie warstw i wyliczenie wartosci
                 connLayer(nHidd, nOut,1,2);
                 neuralNetwork.propagate();

                 int [] desiredAns = readDesiredAns(label);
                 neuralNetwork.calculateError(desiredAns);
                 double rms = neuralNetwork.calculateRMS();
                 System.out.println(rms);
             //    System.out.println(InputLayer.getNeuron(0).outlinks.size());   //sprawdzenie ilsoci polaczen z neuronu
             //    System.out.println(HiddenLayer.getNeuron(HiddenLayer.size()-2).outlinks.size());
        }
        private static double normalize(int value,int max,int min,int new_max,int new_min) {
            double new_value = (value-min)/(max-min)*(new_max-new_min)+new_min;
            return new_value;
        }
        /**
         * Tworzy po³¹czenie pomiêdzy wszystkimi neuronami dwóch warstw
         * (³¹czy warstwy ze sob¹)
         * @param layer warstwa
         * @param n liczba neuronow war. 1
         * @param n1 liczba neuronow war. 2
         * @param sourceLayer nazwa warstwy zrodlwoej
         * @param destLayer nazwa warstwy docelowej
         */
        private static void connLayer(int n, int n1,int sourceLayer, int destLayer) {
            for (int i=0;i<n;i++) {   
			for (int j=0;j<n1;j++) { 
				neuralNetwork.connect(sourceLayer, i, destLayer, j);      //polaczenie neuronow
				//Neuron = (Neuron) HiddenLayer.getNeuron(j);
				//System.out.println("waga["+j+","+i+"]="+Neuron.inlinks.get(i).getValue());
			}
		}
        }



        
        private static int [] readDesiredAns(int digit) {
            int [] d = new int[nOut];
            for (int i=0;i<nOut;i++) {
                d[i]=0;
            }
            d[digit]=1;
            return d;
        }
}
