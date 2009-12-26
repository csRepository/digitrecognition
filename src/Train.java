
import propagation.ResilentPropagation;
import propagation.Propagation;
import propagation.QuickPropagation;
import propagation.Backpropagation;
import database.MNISTDatabase;
import util.ParametersReader;
import util.WeightsFileUtil;
import java.io.IOException;
import java.util.ArrayList;
import neuralnetwork.*;
import propagation.DeltaBarDelta;
import propagation.SuperSAB;
import util.NeuralUtil;

/**
 * Main class to train the neural network.
 * @author tm
 */
public class Train {

    private static Layer OutputLayer;	// warstwa wyjsciowa
    private static Layer InputLayer;	// warstwa wejsciowa
    private static int nIn,nHidd,nOut;      //liczba neuronow wej.,ukryt.,wyj.
    private static NeuralNet neuralNetwork;
    private static MNISTDatabase dataMNIST;
    private static double rms = 0;
    private static double lastError;
    private static String method;           //określa sposob zmiany wag
    private static int [] desiredAns;       //oczekiwand odpowiedzi sieci
    private static Propagation alg;
    private static String algorytm;         //rodzaj lagorytmu z pliku konfig.
    private static ParametersReader parametersFile; //klasa wczytuajca parametery
    private static double[][][] images;
    private static int[][] labels;
    private static ArrayList<Integer> patternsNr;
    private static int licz;

    public static void main(String[] args) throws IOException {

        parametersFile = NeuralUtil.readConfigFile(args);
        dataMNIST = new MNISTDatabase();
        
        nIn = 28*28+1; //wielkosc obrazu + bias
        nHidd = parametersFile.getHiddNeuronsCount()+1;  //l. neuronow + bias
        nOut = 10;
        int net[] = {nIn,nHidd,nOut};
        neuralNetwork = new NeuralNet(net);                // tworzenie sieci neuronwej
      
        InputLayer = neuralNetwork.getLayer(0);
        OutputLayer = neuralNetwork.getLayer(neuralNetwork.getLayers().size()-1);

        setBiases();

        //polaczenie warstw 
        neuralNetwork.connectLayers(nIn, nHidd-1,0,1);
        neuralNetwork.connectLayers(nHidd, nOut,1,2);
        //rodzaj algorytmu

        algorytm = parametersFile.getDefaultAlgorithm();
        method = parametersFile.getUpdateMethod();
        System.out.println("Rodzaj algorytmu: "+ algorytm);

        if (algorytm.equals("bp")) {
                alg = new Backpropagation(method,parametersFile.getParametersBP());
        }
        else if (algorytm.equals("rp")) {
                alg = new ResilentPropagation(parametersFile.getParametersRP());
        }
        else if (algorytm.equals("qp")){
                alg = new QuickPropagation(parametersFile.getParametersQP());
        }
        else if (algorytm.equals("dbd")){
                alg = new DeltaBarDelta();
        }
        else if (algorytm.equals("ssab")){
                alg = new SuperSAB();
        }
        else {
            System.out.println("Błąd w pliku konfiguracyjnym." + "\n" +
                                "Dostępne algorytmy: bp/rp/qp/dbd/ssab");
            System.exit(1);
        }
        

        //inicjalizacja wartosci zmian wag na 0.1 - RPROP
        if (algorytm.equals("rp")) {
            for (int i=1; i<neuralNetwork.getLayers().size();i++)
                for (int j=0;j<neuralNetwork.getLayer(i).size();j++)
                    alg.initialize(neuralNetwork.getLayer(i).getNeuron(j));
        }

       // neuralNetwork.initializeWeights(nIn, nHidd);
        /*--------------------------------------------------------------------*/
        System.out.println("Przetwarzanie obrazów...");
        patternsNr = prepareData();

        /*--------------------------------------------------------------------*/
         System.out.println("Uczenie SN...");
         long time_start = System.currentTimeMillis();

         if ((parametersFile.getEpochsCount() != 0) && (parametersFile.getRMS() != 0))
             do
               learn();
             while ((rms >= parametersFile.getRMS()) && (licz < parametersFile.getEpochsCount()));
         else if (parametersFile.getEpochsCount()!=0)
             do 
               learn();
             while (licz < parametersFile.getEpochsCount());
         else if (parametersFile.getRMS()!=0) {
             do 
               learn();
             while (rms >= parametersFile.getRMS() );
         }

          long time_end = System.currentTimeMillis();
          long time = time_end - time_start;
          if (time<60000)
                System.out.println((double)time/1000+" sek");
          else System.out.println((double)time/60000+" min");
        /*--------------------------------------------------------------------*/

          //zapis wag do pliku
         WeightsFileUtil.writeWeights(neuralNetwork,algorytm);
        
    }



    private  static ArrayList<Integer> prepareData() {
        ArrayList<Integer> trainArray = new ArrayList();
        NeuralUtil.setPatterns(trainArray,parametersFile.getTrainPatternsCount(),60000); //wybor wzorcow z bazy wz. uczacych
        images = NeuralUtil.prepareInputSet(trainArray,dataMNIST);
        labels = NeuralUtil.prepareOutputSet(trainArray,nOut,dataMNIST);

        patternsNr = new ArrayList();
        for (int i = 0; i < parametersFile.getTrainPatternsCount(); i++) {
            patternsNr.add(i);
        }
        return patternsNr;
    }

    /*
     * Ustawia wartosc neuronow typu bias
     */
    private static void setBiases() {
         for (int i = 0; i < neuralNetwork.getLayers().size()-1; i++) {
            Layer layer = neuralNetwork.getLayer(i);
            layer.getNeuron(layer.size()-1).setValue(1);
        }
    }

    private static void learn() {
            licz++;
            if (method.equals("online"))
                NeuralUtil.randomizePatterns(patternsNr); //przemieszanie kolejnosci wzorcow

            //Prezetuje kolejno wszysykie wzorce (1 epocha)
            for (int i=0;i<patternsNr.size();i++) {
                //Ustawienie wzorca
                int index = 0;
                for (int k = 0; k < images[patternsNr.get(i)].length; k++) {
                    for (int j = 0; j < images[patternsNr.get(i)].length; j++) {
                    Neuron neuron = InputLayer.getNeuron(index);
                    neuron.setValue(images[patternsNr.get(i)][k][j]);
                    index++;
                    }
                }
                //Ustawienie odpowiedzi
                 desiredAns = labels[patternsNr.get(i)];
                 // feed-forward
                 neuralNetwork.propagate();
                 //wylicznie bledow wyjsc
                 neuralNetwork.calculateError(desiredAns);
                 //propagacja bledow wstecz
                 backPropagate();

                 if (method.equals("online")) 
                    changeWeights();
             }
                lastError = rms;
                rms = neuralNetwork.calculateRMS();
                System.out.println("Epoch: " + licz + " RMS: " + rms);

             if (method.equals("batch")) {
                changeWeights();
                resetGradients();
             }
    }
    /**
     * Propagacja bledu wstecz
     */
    private static void backPropagate() {
          for(int i = neuralNetwork.getLayers().size()-1; i > 0; i--) {
                Layer layer = (Layer) neuralNetwork.getLayers().get(i);
                for (int j = 0; j < layer.size(); j++) {
                    if (i==neuralNetwork.getLayers().size()-1) {
                         alg.calcOutDelta(layer.getNeuron(j),desiredAns[j]);
                         alg.calcUpdate(layer.getNeuron(j));
                    } else {
                         alg.calcHiddDelta(layer.getNeuron(j));
                         alg.calcUpdate(layer.getNeuron(j));
                    }
                }
         }
    }
    /**
     * Zmiana wartosci wag zgodnie z regula uczenia wybranego algorytmu
     */
    private static void changeWeights() {
        for(int i = neuralNetwork.getLayers().size()-1; i > 0; i--) {
                Layer layer = (Layer) neuralNetwork.getLayers().get(i);
                for (int j = 0; j < layer.size(); j++)
                         alg.changeWeights(layer.getNeuron(j));
         }
    }
    /**
     * Reset gradientow kazdej synapsy
     */
    private static void resetGradients() {
        for(int i = neuralNetwork.getLayers().size()-1; i > 0; i--) {
                Layer layer = (Layer) neuralNetwork.getLayers().get(i);
                for (int j = 0; j < layer.size(); j++)
                         alg.resetSyn(layer.getNeuron(j));
         }
    }
}
