
import database.MNISTDatabase;
import util.ParametersReader;
import util.WeightsFileUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import neuralnetwork.Layer;
import neuralnetwork.NeuralNet;
import neuralnetwork.Neuron;
import util.NeuralUtil;

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
    private static int[][] labels;
    private static ArrayList<Integer> patternsNr;
    private static int [] desiredAns;       //oczekiwane odpowiedzi sieci
    private static ParametersReader parametersFile;

    public static void main(String[] args) throws IOException {

        String fileName;
        if ((fileName = chooseFile())==null) {
            System.out.println("Brak plików  w katalogu 'weights' ");
            System.exit(0);
        }

        nIn = 28*28+1; //wielkosc obrazu + bias
        nHidd = parametersFile.getHiddNeuronsCount()+1;  //l. neuronow + bias
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
        // wczytanie wag
        //-------------------------------------------------------------------------------------
        double weightsArray[] = WeightsFileUtil.readWeights(neuralNetwork,"weights/" + fileName);
         int w =- 1;
         for (int i=1; i<neuralNetwork.getLayers().size();i++) {
                for (int j=0;j<neuralNetwork.getLayer(i).size();j++) {
                    Neuron neuron = neuralNetwork.getLayer(i).getNeuron(j);
                    for (int k=0;k<neuron.getIncomingSyn().size();k++)
                       neuron.getIncomingSyn().get(k).setValue(weightsArray[++w]);
                }
         }

        /*--------------------------------------------------------------------*/
        System.out.println("Przetwarzanie obrazów...");
        patternsNr = prepareData();
        /*--------------------------------------------------------------------*/
        System.out.println("Rozpoznawanie obrazów...");
        // wczytanie obrazow
        patternsNr = prepareData();
        double max[] = new double[2];
        double pom = 0;
        int badRecognizedCount = 0;

        for (int i=0;i<patternsNr.size();i++) {
            //Ustawienie wzorca
            int index = 0;
            for (int k = 0; k < images[i].length; k++) {
                for (int j = 0; j < images[i].length; j++) {
                Neuron neuron = InputLayer.getNeuron(index);
                neuron.setValue(images[i][k][j]);
                index++;
                }
            }
            //Ustawienie odpowiedzi
             desiredAns = labels[i];
             // feed-forward
             neuralNetwork.propagate();
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
                 System.out.println(badRecognizedCount + ": Digit: " + digit + " Recognized: " +
                    + (int)max[0] + " value: " + max[1] + " patternNr:" + patternsNr.get(i) );
            }
            max[1] = 0;
            pom = 0;
         }

         // wyswietlanie niepoprawnych rozpoznan sieci
        double accuracy = roundToDecimals(100-(double)badRecognizedCount/(double)parametersFile.getTestPatternsCount()*100,2);
        System.out.println("----");
        System.out.println("Bad recognized images: " + badRecognizedCount
                + "/" + parametersFile.getTestPatternsCount() + " accuracy: "
                + accuracy + "%");

    }

    private static String chooseFile() throws IOException {
         File file = new File("weights/");

         File[] fileTab;
         if ((fileTab = file.listFiles()) != null ) {
             for (int i = 0; i < fileTab.length; i++) {
                 String ext = fileTab[i].getName().substring(fileTab[i].getName().length()-3, fileTab[i].getName().length());
                    if (ext.equals("dat"))
                    System.out.println(i + ": " + fileTab[i].getName());
             }
             System.out.print("Plik zawierający wagi sieci: ");
             BufferedReader in
                = new BufferedReader(new InputStreamReader(System.in));
             String number = in.readLine();
             return fileTab[Integer.parseInt(number)].getName();
         }
     return null;
    }

    private static double roundToDecimals(double d, int c) {
        int temp=(int)((d*Math.pow(10,c)));
        return (((double)temp)/Math.pow(10,c));
    }

    private  static ArrayList<Integer> prepareData() {
        ArrayList<Integer> testArray = new ArrayList();
        NeuralUtil.setPatterns(testArray,parametersFile.getTestPatternsCount(),10000); //wybor wzorcow z bazy wz. uczacych
        images = NeuralUtil.prepareInputSet(testArray,dataMNIST);
        labels = NeuralUtil.prepareOutputSet(testArray,nOut,dataMNIST);

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
