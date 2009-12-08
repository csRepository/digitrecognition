
import database.MNISTDatabase;
import io.ParametersReader;
import io.WeightsFileReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import neuralnetwork.Layer;
import neuralnetwork.NeuralNet;
import neuralnetwork.Neuron;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author tm
 */
public class Test {

    private static Layer OutputLayer;	// warstwa wyjsciowa
    private static Layer InputLayer;	// warstwa wejsciowa
    private static Layer HiddenLayer;
    private static int nIn,nHidd,nOut;      //liczba neuronow wej.,ukryt.,wyj.
    private static NeuralNet neuralNetwork;
    private static MNISTDatabase Data;
   
    private static int [] desiredAns;       //oczekiwand odpowiedzi sieci
    private static int weightsCount;

    public static void main(String[] args) throws IOException {

            String fileName;
            ParametersReader parameters = new ParametersReader();

            if ((fileName = chooseFile())==null) {
                System.out.println("Brak plików  w katalogu 'weights' ");
                System.exit(0);
            }
            

            nIn=28*28+1; //wielkosc obrazu + bias
            nHidd= parameters.getHiddNeuronsCount()+1;  //l. neuronow + bias
            nOut=10;

            neuralNetwork = new NeuralNet();                // tworzenie sieci neuronwej
            neuralNetwork.addLayer(nIn);	   		// dodanie warstwy wejsciowej
            neuralNetwork.addLayer(nHidd);  		// dodanie warstwy ukrytej
            neuralNetwork.addLayer(nOut);  			// dodanie warstwy wyjsciowej
            InputLayer = neuralNetwork.getLayer(0);
            HiddenLayer = neuralNetwork.getLayer(1);
            OutputLayer = neuralNetwork.getLayer(2);

            InputLayer.getNeuron(InputLayer.size()-1).setValue(1);         //ustawienie biasu na 1
            HiddenLayer.getNeuron(HiddenLayer.size()-1).setValue(1);       //ustawienie biasu na 1

            //polaczenie warstw i wyliczenie wartosci
            connectLayers(nIn, nHidd-1,0,1);
            connectLayers(nHidd, nOut,1,2);

            Data = new MNISTDatabase();
            weightsCount = nIn*(nHidd-1)+nHidd*nOut;
            double dataread[] = new double[weightsCount];
            dataread = readWeights("weights/" + fileName);
             int w=-1;
             for (int i=1; i<neuralNetwork.getLayers().size();i++) {
                    for (int j=0;j<neuralNetwork.getLayer(i).size();j++) {
                        Neuron neuron = neuralNetwork.getLayer(i).getNeuron(j);
                        for (int k=0;k<neuron.getIncomingSyn().size();k++)
                           neuron.getIncomingSyn().get(k).setValue(dataread[++w]);
                    }
             }
            

            System.out.println("\n-----------Testowanie sieci-------------");
            ArrayList<Integer> testArray = new ArrayList();
            setPatterns(testArray,15,10000);
            for (int i=0;i<testArray.size();i++) {
                setInOut(testArray.get(i),"test"); //wybor rodzaju danych (test/train)
                neuralNetwork.propagate();
                for (int j=0;j<OutputLayer.size();j++) {
                    System.out.println("d="+desiredAns[j]+" n"+j+"="+OutputLayer.getNeuron(j).getValue());
                }
                System.out.println();
            }

    }

    public static double[] readWeights(String fileName) throws IOException {
        WeightsFileReader wFiler = null;
        double dataread[] = null;
        try {
            wFiler = new WeightsFileReader(fileName);

                dataread = new double[wFiler.getCount()];
                if (dataread.length == weightsCount)
                    dataread = wFiler.readData(dataread.length);
                else {
                    System.out.println("Plik wag nie zgadza siê z architektur¹ sieci.");
                    chooseFile();
                }
            
           
        } catch (IOException ex) {
            System.err.println("Blad I/O pliku: " + ex);
           
        }
        finally {
            wFiler.close();
        }
        return dataread;

    }


    private static List setPatterns(ArrayList array,int patternCount,int size) {
            for(int i = 0; i <= size; i++) {
                    if (i % (size/patternCount) == 0 && i == 0) array.add(i+1);
                    else if (i % (size/patternCount) == 0) array.add(i);
                }
            return array;
    }
          /**
         * Metoda pomocnicza
         * Ustawia war. neuronow warstwy wej. na war. obrazu,
         * warstwy wyj. na war. oczekiwane
         * @param digit   numer cyfry z bazy
         * @param source  rodzaj bazy cyfr (test/train)
         */
    private static void setInOut(int digit,String source) {
        int [][] image = Data.readImage(source,digit);
        int label = Data.readLabel(source,digit);
        int neuNr=0;
        int vn;
        int suma = 0;

        for (int j=0;j<image.length;j++) {
            for (int k=0;k<image.length;k++) {
                //double vn=normalize(image[j][k],255,0,1,0);
                suma += image[j][k];
            }
        }
        double srednia = suma/(28*28);

        for (int j=0;j<image.length;j++) {
            for (int k=0;k<image.length;k++) {
                //suma += image[j][k];
                //System.out.print(image[j][k]);
                if (image[j][k]>=srednia) {
                    vn=1;
                }
                else
                   vn=0;
              //  System.out.print(vn);
                InputLayer.getNeuron(neuNr).setValue(vn);
                neuNr++;
            }
            // System.out.print("\n");
        }
        //System.out.print("\n");
       // System.out.println("srednia "+srednia);
        int [] d = new int[nOut];
        for (int i=0;i<nOut;i++) {
            d[i]=0;
        }
        d[label]=1;
        desiredAns = d;
    }

        /**
         * Tworzy po³¹czenie pomiêdzy wszystkimi neuronami dwóch warstw
         * (³¹czy warstwy ze sob¹)
         * @param n             liczba neuronow war. 1
         * @param n1            liczba neuronow war. 2
         * @param sourceLayer   nazwa warstwy zrodlwoej
         * @param destLayer     nazwa warstwy docelowej
         */
        private static void connectLayers(int n, int n1,int sourceLayer, int destLayer) {
            for (int i=0;i<n;i++)
                for (int j=0;j<n1;j++)
                    neuralNetwork.connect(sourceLayer, i, destLayer, j);      //polaczenie neuronow
        }

        private static String chooseFile() throws IOException {
             File file = new File("weights/");
             File[] fileTab;
             if ((fileTab = file.listFiles()) != null) {
                 for (int i = 0; i < fileTab.length; i++) {                  
                        System.out.println("[" + i + "]" + fileTab[i]);
                 }
                 System.out.println("Plik zawieraj¹cy wagi sieci: ");

                BufferedReader in
                    = new BufferedReader(new InputStreamReader(System.in));
                String number = in.readLine();
                return fileTab[Integer.parseInt(number)].getName();
             }
             return null;


        }
}
