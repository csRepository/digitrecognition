
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import algorithm.*;
import neuralnetwork.*;
import database.MNISTDatabase;
import util.WeightsFileUtil;
import util.NeuralUtil;
import util.GPathReader;
import java.lang.reflect.Constructor;

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
    private static String algorithm;         //rodzaj lagorytmu z pliku konfig.
   // private static ParametersReader parametersFile; //klasa wczytuajca parametery
    private static GPathReader read;
    private static double[][][] images;
    private static int[][] labels;
    private static ArrayList<Integer> patternsNr;
    private static int licz;

    public static void main(String[] args)  {

        read = NeuralUtil.readConfigFile(args);
        dataMNIST = new MNISTDatabase();

       // read.getParameters(algorithm);
        nIn = 28*28+1; //wielkosc obrazu + bias
        nHidd = read.getHiddNeuronsCount()+1;  //l. neuronow + bias
        nOut = 10;
        int net[] = {nIn,nHidd,nOut};
        neuralNetwork = new NeuralNet(net);                // tworzenie sieci neuronwej
      
        InputLayer = neuralNetwork.getLayer(0);
        OutputLayer = neuralNetwork.getLayer(neuralNetwork.getLayers().size()-1);

        setBiases();
        
        /*-------------- connect layers ----------------------------------*/
        neuralNetwork.connectLayers(nIn, nHidd-1,0,1);
        neuralNetwork.connectLayers(nHidd, nOut,1,2);
        /* ------------- algorithm name ----------------------------------*/
        algorithm = read.getDefaultAlgorithm();
        /* --------------weights change method ---------------------------*/
        method = read.getUpdateMethod();
        
        /* -------make object of algorithm class -------------------------*/
        try {
            Class c = Class.forName("propagation." + algorithm);
            if (read.getParameters(algorithm) != null) {
                Constructor constr = c.getConstructor(new Class[] {double[].class}); //constructor with parameters
                alg = (Propagation) constr.newInstance(new Object[] {read.getParameters(algorithm)});
            }
            else {
                Constructor constr = c.getConstructor(new Class[] {});  //constructor without parameters
                alg = (Propagation) constr.newInstance(new Object[] {});
            }
        } catch (Exception ex) {
            Logger.getLogger(Train.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Błąd w pliku konfiguracyjnym." + "\n" +
                               "Dostępne algorytmy: Backpropagation/QuickPropagation/" +
                               "ResilentPropagation/DeltaBarDelta/SuperSAB ");
            System.exit(1);
        }

        //initialize updateValues to 0.1 for RPROP algorithm
        if (algorithm.equals("ResilentPropagation")) {
            for (int i=1; i<neuralNetwork.getLayers().size();i++)
                for (int j=0;j<neuralNetwork.getLayer(i).size();j++)
                    alg.initialize(neuralNetwork.getLayer(i).getNeuron(j));
        }

        // neuralNetwork.initializeWeights(nIn, nHidd);
        /*-----------------Images preprocess----------------------------------*/
        System.out.println("Data set: " + read.getTrainDataSet());
        System.out.println("Image count: " + read.getTrainPatternsCount());
        System.out.println("Algorithm type: "+ algorithm);
        System.out.println("------");
        System.out.println("Process: Preprocessing images...");
        patternsNr = prepareData();

        /*-----------------Neural Networks learning---------------------------*/
         System.out.println("Process: Neural Network learning...");
         long time_start = System.currentTimeMillis();

         if ((read.getEpochsCount() != 0) && (read.getRMS() != 0))
             do
               learn();
             while ((rms >= read.getRMS()) && (licz < read.getEpochsCount()));
         else if (read.getEpochsCount()!=0)
             do 
               learn();
             while (licz < read.getEpochsCount());
         else if (read.getRMS()!=0) {
             do 
               learn();
             while (rms >= read.getRMS() );
         }

          long time_end = System.currentTimeMillis();
          long time = time_end - time_start;
          if (time<60000)
                System.out.println((double)time/1000+" sek");
          else System.out.println((double)time/60000+" min");
        /*--------------------------------------------------------------------*/

          //write weights to file
         String weightsFileName = read.getWeightsFileName();
         WeightsFileUtil.writeWeights(neuralNetwork,"weights/" +  weightsFileName);
    }

    private  static ArrayList<Integer> prepareData() {
        ArrayList<Integer> trainArray = new ArrayList();
        NeuralUtil.setPatterns(trainArray,read.getTrainPatternsCount(),60000); //wybor wzorcow z bazy wz. uczacych
        try {
            images = NeuralUtil.prepareInputSet(trainArray, dataMNIST, read.getTrainDataSet(), read.getPreprocessMethod());
        } catch (Exception ex) {
            Logger.getLogger(Train.class.getName()).log(Level.SEVERE, null, ex);
        }
        labels = NeuralUtil.prepareOutputSet(trainArray,nOut,dataMNIST, read.getTrainDataSet());

        patternsNr = new ArrayList();
        for (int i = 0; i < read.getTrainPatternsCount(); i++) {
            patternsNr.add(i);
        }
        return patternsNr;
    }

    /*
     * Set values of bias neurons.
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

                 if (method.equals("online")) {
                    changeWeights();
                    resetGradients();
                 }
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
     * Propagate error backwards.
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
     * Change weights with selected algorithm rule.
     */
    private static void changeWeights() {
        for(int i = neuralNetwork.getLayers().size()-1; i > 0; i--) {
                Layer layer = (Layer) neuralNetwork.getLayers().get(i);
                for (int j = 0; j < layer.size(); j++)
                         alg.changeWeights(layer.getNeuron(j));
         }
    }
    /**
     * Reset gradients for all synapses.
     */
    private static void resetGradients() {
        for(int i = neuralNetwork.getLayers().size()-1; i > 0; i--) {
                Layer layer = (Layer) neuralNetwork.getLayers().get(i);
                for (int j = 0; j < layer.size(); j++)
                         alg.resetSyn(layer.getNeuron(j));
         }
    }
}
