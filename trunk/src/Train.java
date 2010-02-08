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
    private static MNISTDatabase dataMNIST;             //baza danych cyfr
    private static double actualRMS;
    private static Propagation alg;
    private static GPathReader read;
    private static double[][][] trainImages,validateImages,testImages;
    private static double[][] trainLabels,validateLabels,testLabels;
    private static double[] desiredAns;         //oczekiwane odpowiedzi sieci
    private static ArrayList<Integer> trainPatternsNr, validatePatternsNr,testPatternsNr;
    private static int licz;         //aktualna liczba epok
    private static int epochStopNr;   //nr epoki do zatrzymania uczenia
    private static double trainAccuracy,validateAccuracy,testAccuracy;
    private static double lowValidError;    //najmniejszy blad walidacji
    private static double[] weightsCopy;    //kopia wag przy walidacji
    private static long time_start,time_mid,time_end; //pomiar czasu
    /*-------------- parametry z pliku ---------------------------------------*/
    private static int epochsCount;
    private static double accuracy; //parameter z pliku
    private static double decay;
    private static int trainPatternsCount,validatePatternsCount,testPatternsCount;
    private static String algorithm;         //rodzaj algorytmu z pliku konfig.
    private static String method;           //określa sposob zmiany wag
    private static double rms;
    private static boolean isBackpropSkip, validate, test;


    public static void main(String[] args)  {

        read = NeuralUtil.readConfigFile(args);
        dataMNIST = new MNISTDatabase();

        //-----------------parameters from conf. file----------------------------
        trainPatternsCount = read.getTrainPatternsCount();
        validatePatternsCount = read.getValidatePatternsCount();
        testPatternsCount = read.getTestPatternsCount();
        epochsCount = read.getEpochsCount();
        method = read.getUpdateMethod();
        decay = read.getWeightsDecay();
        rms = read.getRMS();
        accuracy = read.getAccuracy();
        isBackpropSkip = read.isBackpropSkip();
        validate = read.isValidate();
        test = read.isTest();

        // -------------------------------------------------------------------
        nIn = 28*28+1; //image size + bias
        nHidd = read.getHiddNeuronsCount()+1;  //l. neuronow + bias
        nOut = 10;
        int net[] = {nIn,nHidd,nOut};
        neuralNetwork = new NeuralNet(net); // creating neural net
      
        InputLayer = neuralNetwork.getLayer(0);
        OutputLayer = neuralNetwork.getLayer(neuralNetwork.getLayers().size()-1);

        /*------kopia wag uzywana przy walidacji-------------------*/
           int weightsCount = nIn * (nHidd - 1) + nHidd * nOut; //wag count
           weightsCopy = new double[weightsCount];
        /*---------------------------------------*/
        setBiases();
        
        /*-------------- connect layers ----------------------------------*/
        neuralNetwork.connectLayers(nIn, nHidd-1,0,1);
        neuralNetwork.connectLayers(nHidd, nOut,1,2);
        /* ------------- algorithm param ----------------------------------*/
        algorithm = read.getDefaultAlgorithm();
        double[] algParam = read.getParameters(algorithm);
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

        //initialize updateValues to 0.05 for RPROP algorithm
        if (algorithm.equals("ResilentPropagation")) {
            for (int i=1; i<neuralNetwork.getLayers().size();i++)
                for (int j=0;j<neuralNetwork.getLayer(i).size();j++)
                    alg.initialize(neuralNetwork.getLayer(i).getNeuron(j));
        }

        // neuralNetwork.initializeWeights(nIn, nHidd);
        System.out.println("------\n" + "Data set: " + read.getTrainDataSet());
        if (validate)
            System.out.println("Image count: train|validate " + trainPatternsCount + "|" + validatePatternsCount);
        else System.out.println("Image count: train " + trainPatternsCount);
        System.out.print("Hidden neurons count: " + read.getHiddNeuronsCount()  +
            "\nAlgorithm type: "+ algorithm + " [");
        for (int i = 0; i < read.getParameters(algorithm).length; i++) {
            System.out.print(" " + algParam[i]);
        }
        System.out.println(", decay:" + decay + "]\n------" + "\n" +
              "Process: Preprocessing images...");

         /*-----------------Images preprocess----------------------------------*/
        prepareData("train", trainPatternsCount);
        if (validate) prepareData("validate", validatePatternsCount);
        if (test)  prepareData("test", testPatternsCount);

        /*-----------------Neural Networks learning---------------------------*/
         System.out.println("Process: Neural Network learning...\n" +
                 "Epoch |    RMS     | Accuracy(train validate test)");
         time_start = System.currentTimeMillis();
         //epoch && error
         if ((epochsCount != 0) && (rms != 0)) do learn();
             while ((actualRMS >= rms) && (licz < epochsCount));
         //epoch && accuracy
         else if ((epochsCount != 0) && (accuracy != 0)) do learn();
             while ((trainAccuracy < accuracy) && (licz < epochsCount));
         //epoch and valdiation
         else if (epochsCount!=0 && validate) do learn();              
             while (licz < epochsCount && licz < epochStopNr );
         // epoch 
         else if (epochsCount!=0 ) do learn();
             while (licz < epochsCount);
         // error 
         else if (rms!=0) do learn();
             while (actualRMS >= rms);
         //accuracy
         else if (accuracy!=0) do learn();
             while (trainAccuracy < accuracy );

         time_end = System.currentTimeMillis();

          long time = time_end - time_start;
          long mid_time = time_mid - time_start;
         // if (time<60000)
        if (validate) {
            System.out.printf("\nBest validation accuracy: %.2f%% \nEpochs: %d\n", lowValidError, epochStopNr/2);
            System.out.println("Learning time: " + NeuralUtil.roundToDecimals((double)mid_time/1000,0) + " sek," +
                " Total learning time: " + NeuralUtil.roundToDecimals((double)time/1000,0) + " sek");
        }
        else {
            System.out.printf("\nValidation accuracy: %.2f%% \nEpochs: %d\n", accuracy, licz);
            System.out.println("Total learning time: " + NeuralUtil.roundToDecimals((double)time/1000,0) + " sek");
        }
             // System.out.println(NeuralUtil.roundToDecimals((double)time/60000,2)+" min");
        /*--------------------------------------------------------------------*/

         //write weights to file
         String weightsFileName = read.getWeightsFileName();
         if (validate)
            WeightsFileUtil.writeWeights(weightsCopy, weightsFileName);
         else  WeightsFileUtil.writeWeights(neuralNetwork, weightsFileName);
    }

     private  static void prepareData(String typeOfSet, int patternsCount) {

        double[][][] images = null; double[][] labels;
        ArrayList<Integer> trainArray = new ArrayList();
        ArrayList<Integer> pattNrs = new ArrayList();
        String dataSet = read.getTrainDataSet();
        String preprocesMethod = read.getPreprocessMethod();
        
        int startPattern = 1;
        int endPattern = 60000;
        if (typeOfSet.equals("train")) {
            if (validate) endPattern = 50000;  //z walidacja zbior trenujacy mniejszy o 10000
        }
        else if (typeOfSet.equals("test")) {
             endPattern = 10000;
        }
        else startPattern = 50001;
        NeuralUtil.setPatterns(trainArray,patternsCount,startPattern, endPattern); //wybor wzorcow z bazy wz. uczacych
   
        images = NeuralUtil.prepareInputSet(trainArray, dataMNIST, dataSet, preprocesMethod);
        labels = NeuralUtil.prepareOutputSet(trainArray, nOut, dataMNIST, dataSet);
        
        for (int i = 0; i < patternsCount; i++)
            pattNrs.add(i);

        if (typeOfSet.equals("train")) {
           trainImages = images;
           trainLabels = labels;
           trainPatternsNr = pattNrs;
        }
        else if (typeOfSet.equals("test")) {
           testImages = images;
           testLabels = labels;
           testPatternsNr = pattNrs;
        }
        else {
           validateImages = images;
           validateLabels = labels;
           validatePatternsNr = pattNrs;
        }
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
            NeuralUtil.randomizePatterns(trainPatternsNr); //przemieszanie kolejnosci wzorcow
      
        int badRecognizedCount = 0;

        //-------------------     validate       --------------------------
        if (validate) {
           for (int i=0; i < validatePatternsCount;i++) {
              setInputLayer(validatePatternsNr.get(i), validateImages);
              setOutputLayer(validatePatternsNr.get(i), validateLabels);
              neuralNetwork.passForward();
              badRecognizedCount += validate();
           }
           //-----------------blad klasyfikacji-----------------------------
           validateAccuracy = NeuralUtil.roundToDecimals(100 - (double)badRecognizedCount/(double)validatePatternsCount*100,2);
           badRecognizedCount = 0;

           //if actual accuracy is higher than earlier save weights
           if (validateAccuracy > lowValidError)  {
               saveWeights();
               //minimum epoch count is 20
               if (licz >10)
                epochStopNr = licz * 2;
               else epochStopNr = 20;
               time_mid = System.currentTimeMillis();
           }
           lowValidError = Math.max(lowValidError,  validateAccuracy);
        }

         //-------------------      test       --------------------------
        if (test) {
            for (int i=0; i < testPatternsCount;i++) {
              setInputLayer(testPatternsNr.get(i), testImages);
              setOutputLayer(testPatternsNr.get(i), testLabels);
              neuralNetwork.passForward();
              badRecognizedCount += validate();
             }
            //-----------------classification error-----------------------------
            testAccuracy = NeuralUtil.roundToDecimals(100 -(double)badRecognizedCount/(double)testPatternsCount*100,2);
            badRecognizedCount = 0;
        }

        //-------------------      train       --------------------------
        //presenting all patterns one by one
        for (int i=0; i < trainPatternsCount; i++) {
              setInputLayer(trainPatternsNr.get(i), trainImages);
              setOutputLayer(trainPatternsNr.get(i), trainLabels);
              neuralNetwork.passForward();
              badRecognizedCount += validate();
              double error = neuralNetwork.calculateError(desiredAns);
                /*------skipping learning for trained patterns------------------------------------*/
                 if (isBackpropSkip) {
                     if (error > 0.0005) {
                         passBackward(); //propagacja bledow wstecz
                         if (method.equals("online")) changeWeights();
                     }
                 }
                 else {
                        passBackward(); 
                        if (method.equals("online")) changeWeights();
                 }
         }
        //-----------------classification error------------------------------------
        trainAccuracy = NeuralUtil.roundToDecimals(100-(double)badRecognizedCount/(double)trainPatternsCount*100,2);
         //---------------------------------------------------------------------

        //lastError = rms;
         actualRMS = neuralNetwork.calculateRMS();
        
         if (test && validate)
             System.out.printf("%d %.15f %.2f%% %.2f%% %.2f%%\n", licz, actualRMS, trainAccuracy, validateAccuracy, testAccuracy);
         else if (validate)
             System.out.printf("%d %.15f %.2f%% %.2f%%\n", licz, actualRMS, trainAccuracy, validateAccuracy);
         else if (test) 
             System.out.printf("%d %.15f %.2f%% %.2f%% \n", licz, actualRMS, trainAccuracy, testAccuracy);
         else
             System.out.printf("%d %.15f %.2f%%\n", licz, actualRMS, trainAccuracy);
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

    private static void saveWeights() {
        int w = -1;
         ArrayList<Layer> layers = neuralNetwork.getLayers();
        for (int i=1; i<layers.size();i++) {
            Layer layer = neuralNetwork.getLayer(i);
            for (int j=0;j<layer.size();j++) {
                Neuron neuron =layer.getNeuron(j);
                ArrayList<Synapse> incSyn = neuron.getIncomingSyn();
                for (int k=0;k<incSyn.size();k++)
                   weightsCopy[++w] = incSyn.get(k).getValue();
            }
         }
    }
    private static void setInputLayer(int pattNr,double[][][] images) {
        int index = 0;
        int length = images[pattNr].length;
        for (int k = 0; k < length; k++) {
            for (int j = 0; j < length; j++) {
            Neuron neuron = InputLayer.getNeuron(index);
            neuron.setValue(images[pattNr][k][j]);
            index++;
            }
        }
    }
    private static void setOutputLayer(int pattNr, double[][] labels) {
         desiredAns = labels[pattNr];
    }
    /*Validate patterns on data set*/
    private static int validate() {

                

                double max[] = new double[2];
                double pom = 0;

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
                    return 1;   //zle rozpoznany
                 else return 0; //rozpoznany
    }
}
