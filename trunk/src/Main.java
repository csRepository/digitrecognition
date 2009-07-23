import java.util.Random;


public class Main {


	private static Layer OutputLayer;				// warstwa wyjsciowa
	private static Layer InputLayer;				// warstwa wejsciowa
	private static Neuron Neuron;
	private static Layer HiddenLayer;
        private static int nIn,nHidd,nOut;


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

                nIn=20;
                nHidd=15;
                nOut=10;
		NeuralNet NeuralNetwork = new NeuralNet(); 	// tworzenie sieci neuronwej
		NeuralNetwork.addLayer(nIn);	   		// dodanie warstwy wejsciowej o 20 neuronach
		NeuralNetwork.addLayer(nHidd);  		// dodanie warstwy ukrytej o 10 neuronach
		NeuralNetwork.addLayer(nOut);  			// dodanie warstwy wyjsciowej o 10 neuronach
		
		InputLayer = NeuralNetwork.getLayer(0);
		HiddenLayer = NeuralNetwork.getLayer(1);
		OutputLayer = NeuralNetwork.getLayer(2);

		Random randomGenerator = new Random();
		for (int i=0;i<nIn;i++) {
			Neuron = (Neuron) InputLayer.getNeuron(i);
			Neuron.setValue(randomGenerator.nextInt(2));    //losowe warstosci dla neuronow wejsciowych
			//System.out.println(Neuron.getValue());
			InputLayer.getNeuron(InputLayer.neurons.size()-1).setValue(1); //bias
		}


                //obliczenia dla warstwy ukrytej
		for (int i=0;i<nIn;i++) {   //20
			for (int j=0;j<nHidd;j++) { //15
				NeuralNetwork.connect(0, i, 1, j);      //polaczenie neuronow
				Neuron = (Neuron) HiddenLayer.getNeuron(j);
				//System.out.println("waga["+j+","+i+"]="+Neuron.inlinks.get(i).getValue());
			}
		}
		
		for (int i=0;i<nHidd;i++) {
			Neuron = (Neuron) HiddenLayer.getNeuron(i);
			//Neuron.setValue(randomGenerator.nextInt(2));
			Neuron.computeOutput();
			System.out.println("neuronHidd("+i+")="+Neuron.getValue());
		}

                //oblcizenia dla warstwy wyjsciowej
                for (int i=0;i<nHidd;i++) {
                    for (int j=0;j<nOut;j++) {
                        NeuralNetwork.connect(1, i, 2, j);
                    }
                }
                for (int i=0;i<nOut;i++) {
                    Neuron = (Neuron) OutputLayer.getNeuron(i);
                    Neuron.computeOutput();
                    System.out.println("neuronOut("+i+")="+Neuron.getValue());
                }
	}
}
