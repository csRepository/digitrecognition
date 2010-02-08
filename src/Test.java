import database.MNISTDatabase;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.WeightsFileUtil;
import java.io.IOException;
import java.util.ArrayList;
import neuralnetwork.Layer;
import neuralnetwork.NeuralNet;
import neuralnetwork.Neuron;
import neuralnetwork.Synapse;
import util.NeuralUtil;
import util.GPathReader;

/**
 * Main class to test the neural network.
 * @author tm
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

        //read = NeuralUtil.readConfigFile(args);
        read = new GPathReader(args[0]);

        nIn = 28*28+1; //wielkosc obrazu + bias
        nHidd = read.getHiddNeuronsCount()+1;  //l. neuronow + bias
        nOut = 10;
        int net[] = {nIn,nHidd,nOut};
        neuralNetwork = new NeuralNet(net);                // tworzenie sieci neuronwej

        InputLayer = neuralNetwork.getLayer(0);
        OutputLayer = neuralNetwork.getLayer(neuralNetwork.getLayers().size()-1);

        setBiases();
        //connect layers
        neuralNetwork.connectLayers(nIn, nHidd-1,0,1);
        neuralNetwork.connectLayers(nHidd, nOut,1,2);
        //create database object
        dataMNIST = new MNISTDatabase();
       
       /*-----------------read weights----------------------------------------*/
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
        String algorithm = read.getDefaultAlgorithm();
        double[] algParam = read.getParameters(algorithm);
        System.out.println("Data set: " + read.getTestDataSet());
        System.out.println("Image count: " + read.getTestPatternsCount());
        System.out.println("Hidden neurons count: " + read.getHiddNeuronsCount());
        System.out.print("Algorithm type: "+ algorithm + " [");
        for (int i = 0; i < read.getParameters(algorithm).length; i++) {
            System.out.print(" " + algParam[i]);
        }
        double decay = read.getWeightsDecay();
        System.out.println(", decay:" + decay + "]\n------");
       /*-----------------Images preprocess----------------------------------*/
        System.out.println("Process: Preprocessing images...");
        patternsNr = prepareData();
        /*-----------------Digit recogntion----------------------------------*/
        System.out.println("Process: Patterns recognition...");
        // wczytanie obrazow
        double max[] = new double[2];
        double pom = 0;
        int badRecognizedCount = 0;

        for (int i=0;i<patternsNr.size();i++) {
            //Ustawienie wzorca
            int index = 0;
            int length = images[i].length;
            for (int k = 0; k < length; k++) {
                for (int j = 0; j < length; j++) {
                Neuron neuron = InputLayer.getNeuron(index);
                neuron.setValue(images[i][k][j]);
                index++;
                }
            }
            //Ustawienie odpowiedzi
             desiredAns = labels[i];
             // feed-forward
             neuralNetwork.passForward();
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
             int digit = 0;
             if (desiredAns[(int)max[0]]!= 1)
             {
                 for (int j = 0; j < desiredAns.length; j++) {
                     if (desiredAns[j] == 1)  digit = j;
                 }
                 ++badRecognizedCount;
//                 System.out.println(badRecognizedCount + ": Digit: " + digit + " Recognized: " +
//                    + (int)max[0] + " value: " + max[1] + " patternNr:" + patternsNr.get(i) );
            }
            max[1] = 0;
            pom = 0;
         }

         // wyswietlanie niepoprawnych rozpoznan sieci
        double accuracy = NeuralUtil.roundToDecimals(100-(double)badRecognizedCount/(double)read.getTestPatternsCount()*100,2);
        System.out.println("----");
        System.out.println("Bad recognized images: " + badRecognizedCount
                + "/" + read.getTestPatternsCount() + " accuracy: "
                + accuracy + "%");

    }

    private  static ArrayList<Integer> prepareData() {
        ArrayList<Integer> testArray = new ArrayList();
        NeuralUtil.setPatterns(testArray,read.getTestPatternsCount(),1,10000); //wybor wzorcow z bazy wz. uczacych
        images = NeuralUtil.prepareInputSet(testArray, dataMNIST, read.getTestDataSet(), read.getPreprocessMethod());
        labels = NeuralUtil.prepareOutputSet(testArray,nOut,dataMNIST, read.getTestDataSet());
        return testArray ;
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
}
