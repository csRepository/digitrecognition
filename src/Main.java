import java.util.Random;


public class Main {


	private static Layer OutputLayer;				// warstwa wyjsciowa
	private static Layer InputLayer;				// warstwa wejsciowa
	private static Neuron Neuron;
	private static Layer HiddenLayer;


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		NeuralNet NeuralNetwork = new NeuralNet(); 	// tworzenie sieci neuronwej
		NeuralNetwork.addLayer(21);	   				// dodanie warstwy wejsciowej o 20 neuronach
		NeuralNetwork.addLayer(16);  				// dodanie warstwy ukrytej o 10 neuronach
		NeuralNetwork.addLayer(10);  				// dodanie warstwy wyjsciowej o 10 neuronach
		
		InputLayer = NeuralNetwork.getLayer(0);
		HiddenLayer = NeuralNetwork.getLayer(1);
		OutputLayer = NeuralNetwork.getLayer(2);

		Random randomGenerator = new Random();
		for (int i=0;i<20;i++) {	
			Neuron = (Neuron) InputLayer.getNeuron(i);
			Neuron.setValue(randomGenerator.nextInt(2));
			//System.out.println(Neuron.getValue());
			InputLayer.getNeuron(InputLayer.neurons.size()-1).setValue(1); //bias
		}
		
		for (int i=0;i<15;i++) {
			for (int j=0;j<20;j++) {
				NeuralNetwork.connect(0, j, 1, i);
				Neuron = (Neuron) HiddenLayer.getNeuron(i);
				System.out.println("waga["+j+","+i+"]="+Neuron.inlinks.get(j).getValue());
			}
		}
		
		for (int i=0;i<15;i++) {
			Neuron = (Neuron) HiddenLayer.getNeuron(i);
			//Neuron.setValue(randomGenerator.nextInt(2));
			Neuron.computeOutput();
			System.out.println("neuron"+i+"="+Neuron.getValue());
		}
			
	}
}
