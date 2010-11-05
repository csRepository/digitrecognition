import algorithms.AlgorithmsFactory;
import algorithms.Propagation;
import java.util.ArrayList;
import database.MNISTDatabase;
import neuralnetwork.*;
import util.*;

/**
 * Main class to train the neural network.
 * @author tm
 */
public class Train {

    private static Layer InputLayer,OutputLayer;	//  warstwa wejsciowa, warstwa wyjsciowa
    private static double actualRMS;
    private static Propagation alg;
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
    private static double decay;
    private static int trainPatternsCount,validatePatternsCount,testPatternsCount;
    private static String method, regularizationMethod;
    private static boolean backpropSkip, validate, test;


    public static void main(String[] args)  {

        NeuralUtil.readConfigFile(args);

        //-----------------parameters from conf. file----------------------------
        trainPatternsCount     = GPathReader.getTrainPatternsCount();
        validatePatternsCount  = GPathReader.getValidPatternsCount();
        testPatternsCount      = GPathReader.getTestPatternsCount();
        method                 = GPathReader.getUpdateMethodType();
        decay                  = GPathReader.getDecayValue();
        regularizationMethod   = GPathReader.getRegularizationMethod();
        backpropSkip           = GPathReader.isBackpropSkip();
        validate               = GPathReader.isValidate();
        test                   = GPathReader.isTest();
        String algorithm       = GPathReader.getAlgorithmType();
        String weightsFileName = GPathReader.getWeightsFileName();
        double[] algParam      = GPathReader.getAlgParameters();
        double rms             = GPathReader.getRmsValue();
        double accuracy        = GPathReader.getAccuracy();
        double primeTerm       = GPathReader.getSigmoidPrimeTerm();
        int epochsCount        = GPathReader.getEpochsCount();

        // -------------------------------------------------------------------

        int nIn   = 28*28+1;                         //image size + bias
        int nHidd = GPathReader.getHiddenNeuronsCount()+1;  //neurons count + bias
        int nOut  = 10;                             //10 digit classes
        int net[] = {nIn,nHidd,nOut};
        NeuralNet.createFeedForwardNetwork(net);        // creating neural net
      
        InputLayer  = NeuralNet.getLayer(0);
        OutputLayer = NeuralNet.getLayer(NeuralNet.getLayers().size()-1);

        /*------kopia wag uzywana przy walidacji-------------------*/
        int weightsCount = nIn * (nHidd - 1) + nHidd * nOut; //wag count
        weightsCopy = new double[weightsCount];
        /*---------------------------------------*/
        
        NeuralNet.initializeWeights(NeuralUtil.getSeed());
        Activation.setPrimeTerm(primeTerm);
        
        /* -------make instance of algorithm class -------------------------*/
        alg = AlgorithmsFactory.getInstance(algorithm, algParam);
        if (alg == null) {
            System.out.println("Algorytm nie został zaimplementowany");
            System.exit(1);
        }

        //initialize updateValues to 0.05 for RPROP algorithm
        if (algorithm.equals("ResilentPropagation")) {
            for (int i=1; i<NeuralNet.getLayers().size();i++)
                for (int j=0;j<NeuralNet.getLayer(i).size();j++)
                    alg.initialize(NeuralNet.getLayer(i).getNeuron(j));
        }

         OutPrinter.printTrainHeader();

         /*-----------------Images preprocess----------------------------------*/
        prepareData("train", trainPatternsCount);
        if (validate) prepareData("validate", validatePatternsCount);
        if (test)  prepareData("test", testPatternsCount);

        /*-----------------Neural Networks learning---------------------------*/
        OutPrinter.printTrainLearning();

         time_start = System.currentTimeMillis();

         int caseNr =
             (epochsCount != 0) && (rms != 0)       ? 1 :
             (epochsCount != 0) && (accuracy != 0)  ? 2 :
             (epochsCount != 0) && validate         ? 3 :
             epochsCount  != 0                      ? 4 :
             rms != 0                               ? 5 :
             accuracy != 0                          ? 6 :
             null;

         switch (caseNr) {
             //epoch && rms
             case 1: do learn() ; while ((actualRMS >= rms) && (licz < epochsCount)); break;
             //epoch && accuracy
             case 2: do learn() ; while ((trainAccuracy < accuracy) && (licz < epochsCount)); break;
             //epoch && valdiation
             case 3: do learn() ; while ((licz < epochsCount) && (licz < epochStopNr || licz < 20)); break;
             // epoch
             case 4: do learn() ; while (licz < epochsCount); break;
             // error
             case 5: do learn() ; while (actualRMS >= rms); break;
             //accuracy
             case 6: do learn() ; while (trainAccuracy < accuracy ); break;
             default: break;
         }

         time_end = System.currentTimeMillis();

          long time = time_end - time_start;
          long mid_time = time_mid - time_start;

          OutPrinter.printEpochResults(lowValidError, epochStopNr,
                  mid_time, time, trainAccuracy, licz);

         //write weights to file
         if (!validate) saveWeights();
         WeightsFileUtil.writeWeights(weightsCopy, weightsFileName);
    }

     private  static void prepareData(String typeOfSet, int patternsCount) {
        MNISTDatabase dataMNIST = MNISTDatabase.getInstance();
        double[][][] images = null; double[][] labels;
        ArrayList<Integer> trainArray = new ArrayList();
        ArrayList<Integer> pattNrs = new ArrayList();
        String dataSet = GPathReader.getTrainDataSet();
        String preprocesMethod = GPathReader.getPreprocessMethod();
        double min = GPathReader.getRangeMin();
        double max = GPathReader.getRangeMax();
        
        int startPattern = 1;
        int endPattern = 60000;
        if (typeOfSet.equals("train")) {
            if (validate) endPattern = 50000;  //z walidacja zbior trenujacy mniejszy o 10000
        }
        else if (typeOfSet.equals("test")) {
            dataSet = GPathReader.getTestDataSet();
            endPattern = 10000;
        }
        else startPattern = 50001;
        NeuralUtil.setPatterns(trainArray,patternsCount,startPattern, endPattern); //wybor wzorcow z bazy wz. uczacych
   
        images = NeuralUtil.prepareInputSet(trainArray, dataMNIST, dataSet, preprocesMethod, min, max);
        labels = NeuralUtil.prepareOutputSet(trainArray, OutputLayer.size(), dataMNIST, dataSet);
        
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

    private static void learn() {
        licz++;
        if (method.equals("online"))
           NeuralUtil.randomizePatterns(trainPatternsNr); //przemieszanie kolejnosci wzorcow
        int badRecognizedCount = 0;

        //-------------------     validate       --------------------------
        if (validate) {
           for (int i=0; i < validatePatternsCount;i++) {
              int pattNr = validatePatternsNr.get(i);
              NeuralUtil.setInputLayer(InputLayer,pattNr, validateImages);
              desiredAns = NeuralUtil.setOutputLayer(pattNr, validateLabels);
              NeuralNet.passForward();
              badRecognizedCount += NeuralUtil.validate(OutputLayer,desiredAns,pattNr,false);
           }
           //-----------------blad klasyfikacji-----------------------------
           validateAccuracy = NeuralUtil.calculateClassError(badRecognizedCount,validatePatternsCount);
           badRecognizedCount = 0;

           //if actual accuracy is higher than earlier save weights
           if (validateAccuracy > lowValidError)  {
               saveWeights();
               epochStopNr = licz * 2;
               time_mid = System.currentTimeMillis();
           }
           lowValidError = Math.max(lowValidError,  validateAccuracy);
        }

        //-------------------      test       --------------------------
        if (test) {
            for (int i=0; i < testPatternsCount;i++) {
              int pattNr = testPatternsNr.get(i);
              NeuralUtil.setInputLayer(InputLayer,pattNr, testImages);
              desiredAns = NeuralUtil.setOutputLayer(pattNr, testLabels);
              NeuralNet.passForward();
              badRecognizedCount += NeuralUtil.validate(OutputLayer,desiredAns,pattNr,false);
             }
            //-----------------classification error-----------------------------
           
            testAccuracy = NeuralUtil.calculateClassError(badRecognizedCount,testPatternsCount);
            badRecognizedCount = 0;
        }

       //-------------------      train       --------------------------
        //presenting all patterns one by one
        for (int i=0; i < trainPatternsCount; i++) {
              int pattNr = trainPatternsNr.get(i);
              NeuralUtil.setInputLayer(InputLayer, pattNr, trainImages);
              desiredAns = NeuralUtil.setOutputLayer(pattNr, trainLabels);
              NeuralNet.passForward();
              badRecognizedCount += NeuralUtil.validate(OutputLayer,desiredAns,pattNr,false);
              double error = NeuralNet.calculateError(desiredAns);
                /*------skipping learning for trained patterns------------------------------------*/
                 if (backpropSkip) {
                     if (error > 0.0005) {
                         passBackward(); //propagacja bledow wstecz
                         if (method.equals("online")) changeWeights();
                     }
                 }
                 else {
                        passBackward(); 
                        if (method.equals("online")) changeWeights();
                 }
            //if (method.equals("batch"))
            // if (i%100==0) changeWeights();
         }
        //-----------------classification error------------------------------------
        trainAccuracy = NeuralUtil.calculateClassError(badRecognizedCount,trainPatternsCount);
         //---------------------------------------------------------------------

        //lastError = rms;
         actualRMS = NeuralNet.calculateRMS();
        
         OutPrinter.printOverallTrainResults(trainAccuracy, testAccuracy,
                 validateAccuracy, licz, actualRMS);

         if (method.equals("batch")) changeWeights();
    }

    /**
     * Propagate error backwards.
     */
    private static void passBackward() {
     
        ArrayList<Layer> layers = NeuralNet.getLayers();
        int layersSize = layers.size();
          for(int i=layersSize-1; i > 0; i--) {
            Layer layer = layers.get(i);
            for (int j = 0; j < layer.size(); j++) {
                Neuron neuron = layer.getNeuron(j);
                if (i == layersSize-1) {
                     alg.calcOutDelta(neuron, desiredAns[j]);
                     alg.calcUpdate(neuron, decay, regularizationMethod);
                } else {
                     alg.calcHiddDelta(neuron);
                     alg.calcUpdate(neuron, decay, regularizationMethod);
                }
            }
         }
    }
    /**
     * Change weights with selected algorithm rule.
     */
    private static void changeWeights() {
        ArrayList<Layer> layers = NeuralNet.getLayers();
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
         ArrayList<Layer> layers = NeuralNet.getLayers();
        for (int i=1; i<layers.size();i++) {
            Layer layer = NeuralNet.getLayer(i);
            for (int j=0;j<layer.size();j++) {
                Neuron neuron =layer.getNeuron(j);
                ArrayList<Synapse> incSyn = neuron.getIncomingSyn();
                for (int k=0;k<incSyn.size();k++)
                   weightsCopy[++w] = incSyn.get(k).getValue();
            }
         }
    }


}
