import database.MNISTDatabase;
import java.io.IOException;
import java.util.ArrayList;
import neuralnetwork.*;
import util.*;;

/**
 * Main class to test the neural network.
 * @author Glowczynski Tomasz
 */
public class Test {

    private static Layer OutputLayer;	// warstwa wyjsciowa
    private static Layer InputLayer;	// warstwa wejsciowa
    private static double[][][] images;
    private static double[][] labels;
    private static double[] desiredAns;       //oczekiwane odpowiedzi sieci

    public static void main(String[] args) throws IOException {
   
        NeuralUtil.readConfigFile(args);

        int nIn = 28*28+1; //wielkosc obrazu + bias
        int nHidd = GPathReader.getHiddenNeuronsCount() + 1;  //l. neuronow + bias
        int nOut = 10;
        int net[] = {nIn,nHidd,nOut};

        NeuralNet.createFeedForwardNetwork(net);                // tworzenie sieci neuronwej

        InputLayer = NeuralNet.getLayer(0);
        OutputLayer = NeuralNet.getLayer(NeuralNet.getLayers().size()-1);
        ArrayList<Integer> patternsNr;
       /*-----------------set weights----------------------------------------*/
        String weightsFileName = GPathReader.getWeightsFileName();
        int weightsCount = nIn * (nHidd - 1) + nHidd * nOut;
        double weightsArray[] = WeightsFileUtil.readWeights(weightsCount, weightsFileName);
         int w =- 1;
         int layersSize = NeuralNet.getLayers().size();

         for (int i=1; i < layersSize;i++) {
                Layer layer = NeuralNet.getLayer(i);
                for (int j=0;j < layer.size();j++) {
                    Neuron neuron = layer.getNeuron(j);
                    ArrayList<Synapse> incSyns = neuron.getIncomingSyn();
                    for (int k = 0; k < incSyns.size(); k++)
                       incSyns.get(k).setValue(weightsArray[++w]);
                }
         }
        OutPrinter.printTestHeader();

        patternsNr = prepareData();
        /*-----------------Digit recogntion----------------------------------*/
        System.out.println("Process: Patterns recognition...");
      
        int badRecognizedCount = 0;

        for (int i=0;i<patternsNr.size();i++) {
            //Ustawienie wzorca
              NeuralUtil.setInputLayer(InputLayer,i, images);
              desiredAns = NeuralUtil.setOutputLayer(i, labels);
             // feed-forward
             NeuralNet.passForward();
             badRecognizedCount += NeuralUtil.validate(OutputLayer, desiredAns);
         }

         // wyswietlanie niepoprawnych rozpoznan sieci
        double accuracy = NeuralUtil.calculateClassError(badRecognizedCount,GPathReader.getTestPatternsCount());
        OutPrinter.printOverallTestResults(badRecognizedCount, accuracy);
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
        labels = NeuralUtil.prepareOutputSet(testArray,OutputLayer.size(),dataMNIST, GPathReader.getTestDataSet());
        return testArray ;
    }


}
