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

    private static Layer InputLayer,OutputLayer;	//  warstwa wejsciowa, warstwa wyjsciowa
    private static int nIn,nHidd,nOut;      //liczba neuronow wej.,ukryt.,wyj.
    private static NeuralNet neuralNetwork;
    private static MNISTDatabase dataMNIST;
    private static double actualRMS = 0;
    private static String method;           //określa sposob zmiany wag
    private static double [] desiredAns;       //oczekiwand odpowiedzi sieci
    private static Propagation alg;
    private static String algorithm;         //rodzaj lagorytmu z pliku konfig.
    private static GPathReader read;
    private static double[][][] images;
    private static double[][] labels;
    private static ArrayList<Integer> patternsNr;
    private static int licz;
    private static double actualAccuracy;
    private static double accuracy;
    private static double decay;
    private static int patternsCount;
    private static int epochsCount;
    private static double rms;
    private static boolean isBackpropSkip;

    public static void main(String[] args)  {

        read = NeuralUtil.readConfigFile(args);
        dataMNIST = new MNISTDatabase();
        //------------------------parametry------------------------------------
        patternsCount = read.getTrainPatternsCount();
        epochsCount = read.getEpochsCount();
        rms = read.getRMS();
        accuracy = read.getAccuracy();
        isBackpropSkip = read.isBackpropSkip();
       // -------------------------------------------------------------------
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
        /* ------------- algorithm param ----------------------------------*/
        algorithm = read.getDefaultAlgorithm();
        double[] algParam = read.getParameters(algorithm);
        /* --------------weights change method ---------------------------*/
        method = read.getUpdateMethod();
        decay = read.getWeightsDecay();
        
        /* -------make object of algorithm class -------------------------*/
        try {
            Class c = Class.forName("algorithm." + algorithm);
            if (read.getParameters(algorithm) != null) {
                Constructor constr = c.getConstructor(new Class[] {double[].class}); //constructor with parameters
                alg = (Propagation) constr.newInstance(new Object[] {algParam});
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
        System.out.println("------");
        System.out.println("Data set: " + read.getTrainDataSet());
        System.out.println("Image count: " + patternsCount);
        System.out.println("Hidden neurons count: " + read.getHiddNeuronsCount());
        System.out.print("Algorithm type: "+ algorithm + " [");
        for (int i = 0; i < read.getParameters(algorithm).length; i++) {
            System.out.print(" " + algParam[i]);
        }
        System.out.println(", decay:" + decay + "]\n------");
        System.out.println("Process: Preprocessing images...");
        patternsNr = prepareData();

        /*-----------------Neural Networks learning---------------------------*/
         System.out.println("Process: Neural Network learning...");
         long time_start = System.currentTimeMillis();
         //epoch && error
         if ((epochsCount != 0) && (rms != 0)) do learn();
             while ((actualRMS >= rms) && (licz < epochsCount));
         //epoch && accuracy
         else if ((epochsCount != 0) && (accuracy != 0)) do learn();
             while ((actualAccuracy < accuracy) && (licz < epochsCount));
         // epoch
         else if (epochsCount!=0) do learn();
             while (licz < epochsCount);
         // error 
         else if (rms!=0) do learn();
             while (actualRMS >= rms);
         //accuracy
         else if (accuracy!=0) do learn();
              while (actualAccuracy < accuracy );

          long time_end = System.currentTimeMillis();
          long time = time_end - time_start;
         // if (time<60000)
                System.out.println(NeuralUtil.roundToDecimals((double)time/1000,0) + " sek");
          //else
            //  System.out.println(NeuralUtil.roundToDecimals((double)time/60000,2)+" min");
        /*--------------------------------------------------------------------*/

          //write weights to file
         String weightsFileName = read.getWeightsFileName();
         WeightsFileUtil.writeWeights(neuralNetwork,weightsFileName);
    }

    private  static ArrayList<Integer> prepareData() {
        ArrayList<Integer> trainArray = new ArrayList();
        String dataSet = read.getTrainDataSet();
        String preprocesMethod = read.getPreprocessMethod();
        NeuralUtil.setPatterns(trainArray,patternsCount,60000); //wybor wzorcow z bazy wz. uczacych
        try {
            images = NeuralUtil.prepareInputSet(trainArray, dataMNIST, dataSet, preprocesMethod);
        } catch (Exception ex) {
            Logger.getLogger(Train.class.getName()).log(Level.SEVERE, null, ex);
        }
        labels = NeuralUtil.prepareOutputSet(trainArray,nOut,dataMNIST, dataSet);

        patternsNr = new ArrayList();
        for (int i = 0; i < patternsCount; i++) {
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
        double max[] = new double[2];
        double pom = 0;
        int badRecognizedCount = 0;
        //Prezetuje kolejno wszysykie wzorce (1 epocha)
        for (int i=0; i < patternsNr.size();i++) {
                //Ustawienie wzorca
                int index = 0;
                int pattNr = patternsNr.get(i);
                for (int k = 0; k < images[pattNr].length; k++) {
                    for (int j = 0; j < images[pattNr].length; j++) {
                    Neuron neuron = InputLayer.getNeuron(index);
                    neuron.setValue(images[pattNr][k][j]);
                    index++;
                    }
                }
                //Ustawienie odpowiedzi
                 desiredAns = labels[pattNr];
                 // feed-forward
                 neuralNetwork.passForward();
                 //classification error----------------------------------------
                 for (int j=0;j<OutputLayer.size();j++) {
                    Neuron neuron = OutputLayer.getNeuron(j);
                    max[1] = Math.max(max[1], neuron.getValue());
                    if (max[1]!=pom) {
                        max[0] = OutputLayer.indexOf(neuron);
                        pom = max[1];
                    }
                }
                 //jesli wyjscie odpowiedzi oczekiwanych != 1 czyli cyfra z bazy nie odpowiada
                 //najwiekszemu wyjsciu to cyfra nie zostala rozpoznana
                // int digit = 0;
                 if (desiredAns[(int)max[0]] != 1)
                     ++badRecognizedCount;

                max[1] = 0;
                pom = 0;

                //---------------------------------------------------------------

                double error = neuralNetwork.calculateError(desiredAns);

                 if (isBackpropSkip) { //pomijanie wzorcow nauczonych
                     if (error > 0.0005) {
                         passBackward(); //propagacja bledow wstecz
                         if (method.equals("online"))
                            changeWeights();
                     }
                 }
                 else {
                        passBackward(); //propagacja bledow wstecz
                        if (method.equals("online"))
                            changeWeights();
                 }
         }
        //-----------------blad klasyfikacji-----------------------------
        actualAccuracy = NeuralUtil.roundToDecimals(100-(double)badRecognizedCount/(double)patternsCount*100,2);
         //------------------------------------------------------------
       //  lastError = rms;
         actualRMS = neuralNetwork.calculateRMS();
//             System.out.println("Epoch: " + licz + " RMS: " + rms + " Accuracy: "
//                    + accuracy + "%");
         System.out.printf("Epoch: %d RMS: %.15f Accuracy: %.2f%%\n", licz, actualRMS, actualAccuracy);
         if (method.equals("batch"))
            changeWeights();
    }
    /**
     * Propagate error backwards.
     */
    private static void passBackward() {
        ArrayList<Layer> layers = neuralNetwork.getLayers();
        int layersSize = layers.size();
          for(int i=layersSize-1; i > 0; i--) {
            Layer layer = layers.get(i);
            for (int j = 0; j < layer.size(); j++) {
                Neuron neuron = layer.getNeuron(j);
                if (i == layersSize-1) {
                     alg.calcOutDelta(neuron, desiredAns[j]);
                     alg.calcUpdate(neuron, decay);
                } else {
                     alg.calcHiddDelta(neuron);
                     alg.calcUpdate(neuron, decay);
                }
            }
         }
    }
    /**
     * Change weights with selected algorithm rule.
     */
    private static void changeWeights() {
        ArrayList<Layer> layers = neuralNetwork.getLayers();
        for(int i = layers.size()-1; i > 0; i--) {
            Layer layer =  layers.get(i);
            for (int j = 0; j < layer.size(); j++) {
                     Neuron neuron = layer.getNeuron(j);
                     alg.changeWeights(neuron);
                     alg.resetSyn(neuron);
            }
         }
    }
}
