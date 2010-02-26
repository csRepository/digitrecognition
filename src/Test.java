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
    private static double[][][] images;
    private static double[][] labels;
    private static ArrayList<Integer> patternsNr;
    private static double [] desiredAns;       //oczekiwane odpowiedzi sieci
    //private static ParametersGPathReaderer parametersFile;

    public static void main(String[] args) throws IOException {
   
        NeuralUtil.readConfigFile(args);
        nIn = 28*28+1; //wielkosc obrazu + bias
        nHidd = GPathReader.getHiddenNeuronsCount() + 1;  //l. neuronow + bias
        nOut = 10;
        int net[] = {nIn,nHidd,nOut};
        neuralNetwork = NeuralNet.FeedForwardNetwork(net);                // tworzenie sieci neuronwej

        InputLayer = neuralNetwork.getLayer(0);
        OutputLayer = neuralNetwork.getLayer(neuralNetwork.getLayers().size()-1);

       /*-----------------set weights----------------------------------------*/
        String weightsFileName = GPathReader.getWeightsFileName();
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
        util.OutPrinter.printTestHeader();

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
        double accuracy = NeuralUtil.calculateClassError(badRecognizedCount,GPathReader.getTestPatternsCount());
        util.OutPrinter.printOverallTestResults(badRecognizedCount, accuracy);
    }

    private  static ArrayList<Integer> prepareData() {
        MNISTDatabase dataMNIST = MNISTDatabase.getInstance();
        ArrayList<Integer> testArray = new ArrayList();
        double min = GPathReader.getRangeMin();
        double max = GPathReader.getRangeMax();
        String dataSet = GPathReader.getTestDataSet();
        String preprocesMethod = GPathReader.getPreprocessMethod();
        NeuralUtil.setPatterns(testArray,GPathReader.getTestPatternsCount(),1,10000); //wybor wzorcow z bazy wz. uczacych
        images = NeuralUtil.prepareInputSet(testArray, dataMNIST, dataSet, preprocesMethod, min, max);
        labels = NeuralUtil.prepareOutputSet(testArray,nOut,dataMNIST, GPathReader.getTestDataSet());
        return testArray ;
    }


}
