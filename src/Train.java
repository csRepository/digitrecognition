import algorithms.AlgorithmsFactory;
import algorithms.Propagation;
import java.util.ArrayList;
import neuralnetwork.*;
import database.MNISTDatabase;
import util.WeightsFileUtil;
import util.NeuralUtil;
import util.GPathReader;

/**
 * Main class to train the neural network.
 * @author tm
 */
public class Train {

    private static Layer InputLayer,OutputLayer;	//  warstwa wejsciowa, warstwa wyjsciowa
    private static NeuralNet neuralNetwork;
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
    private static String method;           //określa sposob zmiany wag
    private static boolean isBackpropSkip, validate, test;
    private static double primeTerm;


    public static void main(String[] args)  {

        NeuralUtil.readConfigFile(args);

        //-----------------parameters from conf. file----------------------------
        trainPatternsCount     = GPathReader.getTrainPatternsCount();
        validatePatternsCount  = GPathReader.getValidPatternsCount();
        testPatternsCount      = GPathReader.getTestPatternsCount();
        int epochsCount            = GPathReader.getEpochsCount();
        method                 = GPathReader.getUpdateMethodType();
        decay                  = GPathReader.getDecayValue();
        double rms                    = GPathReader.getRmsValue();
        double accuracy               = GPathReader.getAccuracy();
        isBackpropSkip         = GPathReader.isBackpropSkip();
        validate               = GPathReader.isValidate();
        test                   = GPathReader.isTest();
        String algorithm              = GPathReader.getAlgorithmType();
        double[] algParam      = GPathReader.getAlgParameters();
        String weightsFileName = GPathReader.getWeightsFileName();
        primeTerm = GPathReader.getSigmoidPrimeTerm();
        // -------------------------------------------------------------------

        int nIn   = 28*28+1;                         //image size + bias
        int nHidd = GPathReader.getHiddenNeuronsCount()+1;  //neurons count + bias
        int nOut  = 10;                             //10 digit classes
        int net[] = {nIn,nHidd,nOut};
        neuralNetwork = NeuralNet.FeedForwardNetwork(net);        // creating neural net
      
        InputLayer  = neuralNetwork.getLayer(0);
        OutputLayer = neuralNetwork.getLayer(neuralNetwork.getLayers().size()-1);

        /*------kopia wag uzywana przy walidacji-------------------*/
        int weightsCount = nIn * (nHidd - 1) + nHidd * nOut; //wag count
        weightsCopy = new double[weightsCount];
        /*---------------------------------------*/
        
        neuralNetwork.initializeWeights(getSeed());
        
        /* -------make instance of algorithm class -------------------------*/
        alg = AlgorithmsFactory.getInstance(algorithm, algParam);
        if (alg == null) {
            System.out.println("Algorytm nie został zaimplementowany");
            System.exit(1);
        }

        //initialize updateValues to 0.05 for RPROP algorithm
        if (algorithm.equals("ResilentPropagation")) {
            for (int i=1; i<neuralNetwork.getLayers().size();i++)
                for (int j=0;j<neuralNetwork.getLayer(i).size();j++)
                    alg.initialize(neuralNetwork.getLayer(i).getNeuron(j));
        }

         util.OutPrinter.printTrainHeader();

         /*-----------------Images preprocess----------------------------------*/
        prepareData("train", trainPatternsCount);
        if (validate) prepareData("validate", validatePatternsCount);
        if (test)  prepareData("test", testPatternsCount);

        /*-----------------Neural Networks learning---------------------------*/
         util.OutPrinter.printTrainLearning();

         time_start = System.currentTimeMillis();

         int caseNr =
             (epochsCount != 0) && (rms != 0)       ? 1 :
             (epochsCount != 0) && (accuracy != 0)  ? 2 :
             (epochsCount != 0) && validate         ? 3 :
             epochsCount  != 0                      ? 4 :
             rms != 0                               ? 5 :
             accuracy != 0                          ? 6 :
             0;

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

          util.OutPrinter.printEpochResults(lowValidError, epochStopNr,
                  mid_time, time, trainAccuracy, licz);

         //write weights to file
         if (validate)
            WeightsFileUtil.writeWeights(weightsCopy, weightsFileName);
         else  WeightsFileUtil.writeWeights(neuralNetwork, weightsFileName);
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
              neuralNetwork.passForward();
              badRecognizedCount += NeuralUtil.validate(OutputLayer,desiredAns);
           }
           //-----------------blad klasyfikacji-----------------------------
           validateAccuracy = NeuralUtil.calculateClassError(badRecognizedCount,validatePatternsCount);
           badRecognizedCount = 0;

           //if actual accuracy is higher than earlier save weights
           if (validateAccuracy > lowValidError)  {
               saveWeights();
               //minimum epoch count is 20
               //if (licz >10)
                epochStopNr = licz * 2;
              // else epochStopNr = 20;
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
              neuralNetwork.passForward();
              badRecognizedCount += NeuralUtil.validate(OutputLayer,desiredAns);
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
              neuralNetwork.passForward();
              badRecognizedCount += NeuralUtil.validate(OutputLayer,desiredAns);
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
        trainAccuracy = NeuralUtil.calculateClassError(badRecognizedCount,trainPatternsCount);
         //---------------------------------------------------------------------

        //lastError = rms;
         actualRMS = neuralNetwork.calculateRMS();
        
         util.OutPrinter.printOverallTrainResults(trainAccuracy, testAccuracy,
                 validateAccuracy, licz, actualRMS);

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
                     alg.calcOutDelta(neuron, desiredAns[j],primeTerm);
                     alg.calcUpdate(neuron, decay);
                } else {
                     alg.calcHiddDelta(neuron,primeTerm);
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

    private static long getSeed() {
        String wFile = GPathReader.getWeightsFileName();
        long seed;
        try {
            seed = Long.parseLong(wFile.substring(wFile.length()-6,wFile.length()-4));
        }
        catch (NumberFormatException ex){
            seed = System.currentTimeMillis();
        }
       // System.out.println(seed);
        return seed;

    }
}
