import database.MNISTDatabase;
import util.WeightsFileUtil;
import java.io.IOException;
import java.util.ArrayList;
import neuralnetwork.*;
import util.NeuralUtil;
import util.GPathReader;

/**
 * Main class to test the neural network.
 * @author Glowczynski Tomasz
 */
public class Test {

    private static Layer OutputLayer;	// warstwa wyjsciowa
    private static Layer InputLayer;	// warstwa wejsciowa
    private static int nIn,nHidd,nOut;      //liczba neuronow wej.,ukryt.,wyj.
    private static NeuralNet neuralNetwork;
    private static MNISTDatabase dataMNIST;
    private static double[][][] images;
    private static double[][] labels;
    private static ArrayList<Integer> patternsNr;
    private static double [] desiredAns;       //oczekiwane odpowiedzi sieci
    //private static ParametersReader parametersFile;
    private static GPathReader read;

    public static void main(String[] args) throws IOException {

        read = new GPathReader(args[0]);

        nIn = 28*28+1; //wielkosc obrazu + bias
        nHidd = read.getHiddNeuronsCount()+1;  //l. neuronow + bias
        nOut = 10;
        int net[] = {nIn,nHidd,nOut};
        neuralNetwork = NeuralNet.FeedForwardNetwork(net);                // tworzenie sieci neuronwej

        InputLayer = neuralNetwork.getLayer(0);
        OutputLayer = neuralNetwork.getLayer(neuralNetwork.getLayers().size()-1);

        //create database 
        dataMNIST = new MNISTDatabase();
       
       /*-----------------set weights----------------------------------------*/
        String weightsFileName = read.getWeightsFileName();
        double weightsArray[] = WeightsFileUtil.readWeights(neuralNetwork,weightsFileName);
         int w =- 1;
         int layersSize = neuralNetwork.getLayers().size();

         for (int i=1; i < layersSize;i++) {
                Layer layer = neuralNetwork.getLayer(i);
                for (int j=0;j < layer.size();j++) {
                    Neuron neuron = layer.getNeuron(j);
                    ArrayList<Synapse> incSyns = neuron.getIncomingSyn();
                    for (int k = 0; k < incSyns.size(); k++)
                       incSyns.get(k).setValue(weightsArray[++w]);
                }
         }
        /*-----------------parameters from conf. file------------------------*/
        String dataSet        = read.getTestDataSet();
        int testPatternsCount = read.getTestPatternsCount();
        double decay          = read.getWeightsDecay();
        String algorithm      = read.getDefaultAlgorithm();
        double[] algParam     = read.getParameters(algorithm);
        String method         = read.getPreprocessMethod();
        double range_min      = read.getRangeMin();
        double range_max      = read.getRangeMax();

        util.OutPrinter.printTestHeader(dataSet, algorithm, testPatternsCount,
                nHidd-1, algParam, decay, method, range_min, range_max);

        patternsNr = prepareData();
        /*-----------------Digit recogntion----------------------------------*/
        System.out.println("Process: Patterns recognition...");
      
        int badRecognizedCount = 0;

        for (int i=0;i<patternsNr.size();i++) {
            //Ustawienie wzorca
              NeuralUtil.setInputLayer(InputLayer,i, images);
              desiredAns = NeuralUtil.setOutputLayer(i, labels);
             // feed-forward
             neuralNetwork.passForward();
             badRecognizedCount += NeuralUtil.validate(OutputLayer, desiredAns);
         }

         // wyswietlanie niepoprawnych rozpoznan sieci
        double accuracy = NeuralUtil.calculateClassError(badRecognizedCount,read.getTestPatternsCount());

        util.OutPrinter.printOverallTestResults(testPatternsCount, badRecognizedCount, accuracy);
    }

    private  static ArrayList<Integer> prepareData() {
        ArrayList<Integer> testArray = new ArrayList();
        double min = read.getRangeMin();
        double max = read.getRangeMax();
        String dataSet = read.getTestDataSet();
        String preprocesMethod = read.getPreprocessMethod();
        NeuralUtil.setPatterns(testArray,read.getTestPatternsCount(),1,10000); //wybor wzorcow z bazy wz. uczacych
        images = NeuralUtil.prepareInputSet(testArray, dataMNIST, dataSet, preprocesMethod, min, max);
        labels = NeuralUtil.prepareOutputSet(testArray,nOut,dataMNIST, read.getTestDataSet());
        return testArray ;
    }


}
