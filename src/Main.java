import java.io.IOException;
import neuralnetwork.*;
import database.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

	private static Layer OutputLayer;	// warstwa wyjsciowa
	private static Layer InputLayer;	// warstwa wejsciowa
	private static Layer HiddenLayer;
        private static int nIn,nHidd,nOut;      //liczba neuronow wej.,ukryt.,wyj.
        private static NeuralNet neuralNetwork;
        private static MNISTDatabase Data;
        private static double rms = 0;
        //private static int pattCount = 100;     //okreœla rozrzut wzorców
        private static String method;           //okreœla sposob zmiany wag
                                                //przyrostowo/grupowo (online/batch)
        private static int [] desiredAns;       //oczekiwand odpowiedzi sieci

        private static Propagation alg;
        /**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
                System.out.println("Rodzaj algorytmu(BP/RP) :");
                BufferedReader in
                         = new BufferedReader(new InputStreamReader(System.in));
                String algorytm = in.readLine();

                nIn=28*28+1; //wielkosc obrazu + bias
                nHidd=25+1;  //l. neuronow + bias
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

                if (algorytm.equals("BP")) {
                    method = "online";
                    alg = new Backpropagation(method,0.9,0.2);              
                }
                else {
                    method = "batch";
                    alg = new RPROP();
                    //inicjalizacja wartosci zmian wag na 0.1 - RPROP
                    for (int i=1; i<neuralNetwork.getLayers().size();i++)
                        for (int j=0;j<neuralNetwork.getLayer(i).size();j++)
                            alg.initialize(neuralNetwork.getLayer(i).getNeuron(j));
                }

                Data = new MNISTDatabase();
                ArrayList<Integer> trainArray = new ArrayList();
                setPatterns(trainArray,100,60000); //wybor 100 wzorcow z bazy wz. uczacych
                
               // System.out.println(trainArray.toString());
                //int [] patterns = new int[array.size()];
                //-----------------------------------------------------
                 int licz = 0;
                 do {
                    licz++;
                   //randomizePatterns(trainArray,trainArray); //przemieszanie kolejnosci wzorcow
                   //System.out.println(trainArray.toString());
                    for (int i=0;i<trainArray.size();i++) {
                         setInOut(trainArray.get(i),"train");
                         neuralNetwork.propagate();
                         neuralNetwork.calculateError(desiredAns);

                         for (int j=0;j<OutputLayer.size();j++) {
                             alg.calcOutError(OutputLayer.getNeuron(j),desiredAns[j]);
                             alg.calcUpdate(OutputLayer.getNeuron(j));
                         }
                         for (int j=0;j<HiddenLayer.size();j++) {
                              alg.calcHiddError( HiddenLayer.getNeuron(j));
                              alg.calcUpdate(HiddenLayer.getNeuron(j));
                         }

                         if (method.equals("online") ) {
                             for (int j=0;j<HiddenLayer.size();j++) 
                                alg.changeWeights(HiddenLayer.getNeuron(j));
                             for (int j=0;j<OutputLayer.size();j++)
                                alg.changeWeights(OutputLayer.getNeuron(j));
                         }
                     }
                    if (method.equals("batch")) {
                         for (int j=0;j<HiddenLayer.size();j++) {
                            alg.changeWeights(HiddenLayer.getNeuron(j));
                            alg.resetSyn(HiddenLayer.getNeuron(j));
                         }
                         for (int j=0;j<OutputLayer.size();j++) {
                            alg.changeWeights(OutputLayer.getNeuron(j));
                            alg.resetSyn(OutputLayer.getNeuron(j));
                         }
                    }
                     rms = neuralNetwork.calculateRMS();
                     System.out.println("Epoch "+licz+" rms "+rms);  
                 }
                 while (rms>0.05);

                //test
                System.out.println("\n-----------Testowanie sieci-------------");
                ArrayList<Integer> testArray = new ArrayList();
                setPatterns(testArray,100,15000);
                for (int i=0;i<testArray.size();i++) {
                    setInOut(testArray.get(i),"test"); //wybor rodzaju danych (test/train)
                    neuralNetwork.propagate();
                    for (int j=0;j<OutputLayer.size();j++) {
                        System.out.println("d="+desiredAns[j]+" n"+j+"="+OutputLayer.getNeuron(j).getValue());
                    }
                    System.out.println();
                }     
        }

        /**
         * Metoda pomocnicza
         * Ustawia war. neuronow warstwy wej. na war. obrazu,
         * warstwy wyj. na war. oczekiwane
         * @param digit numer cyfry z bazy
         * @param from  rodzaj bazy cyfr (test/train)
         */
        private static void setInOut(int digit,String from) {
            int [][] image = Data.readImage(from,digit);
            int label = Data.readLabel(from,digit);
            int neuNr=0;
            for (int j=0;j<image.length;j++) {
                for (int k=0;k<image.length;k++) {
                    double vn=normalize(image[j][k],255,0,1,0);
                    InputLayer.getNeuron(neuNr).setValue(vn);
                    neuNr++;
                }
            }
            int [] d = new int[nOut];
            for (int i=0;i<nOut;i++) {
                d[i]=0;
            }
            d[label]=1;
            desiredAns = d;
        }

        /**
         * Normailzuje wartosc do wybranego przedzialu
         * @param value     wartosc przed normalizacja
         * @param max       maksymalna wartosc przedzialu
         * @param min       minimalna wartosc przedzialu
         * @param new_max   nowa maks. wartosc przedzialu
         * @param new_min   nowa min. wartosc przedzialu
         * @return nowa wartosc po normalizacji
         */
        private static double normalize(int value,int max,int min,double new_max,double new_min) {
            double new_value = (value-min)/(max-min)*(new_max-new_min)+new_min;
            return new_value;
        }
        /**
         * Tworzy po³¹czenie pomiêdzy wszystkimi neuronami dwóch warstw
         * (³¹czy warstwy ze sob¹)
         * @param               layer warstwa
         * @param n             liczba neuronow war. 1
         * @param n1            liczba neuronow war. 2
         * @param sourceLayer   nazwa warstwy zrodlwoej
         * @param destLayer     nazwa warstwy docelowej
         */
        private static void connectLayers(int n, int n1,int sourceLayer, int destLayer) {
            for (int i=0;i<n;i++) {   
			for (int j=0;j<n1;j++) { 
				neuralNetwork.connect(sourceLayer, i, destLayer, j);      //polaczenie neuronow
			}
            }
        }
//        /**
//         * Zwraca wektor oczekiwanych odpowiedzi sieci dla wybranego wzorca
//         * @param digit wybrany wzorzec (cyfra)
//         * @return wektor odpowiedzi
//         */
//        private static int [] getDesiredAns(int digit) {
//            int [] d = new int[nOut];
//            for (int i=0;i<nOut;i++) {
//                d[i]=0;
//            }
//            d[digit]=1;
//            return d;
//        }

        /**
         * Losowanie liczb z zadanego przedzialu [a,b] do tablicy
         * @param tab   tablica do której wstawiane s¹ wylosowane liczby
         * @param a     pocz¹tek przedzia³u
         * @param b     koniec przedzia³u
         */
        private static List randomizePatterns(ArrayList<Integer> oldArray,ArrayList newArray) {
            ArrayList<Integer> list = new ArrayList();
            for (int i=0;i<oldArray.size();i++) {
                list.add(oldArray.get(i));
            }
            Random rand = new Random();
            int k = 0;
            while (list.size()>0)  {
                Object elem = list.remove(rand.nextInt(list.size()));
                newArray.set(k,elem);
                k++;
            }
            return newArray;
        }

//        private static void randomizePatterns(ArrayList array,int [] tab, int count) {
//            Random rand = new Random();
//
//            int k = 0;
//            if (array.size()!=tab.length)
//                throw new IllegalArgumentException("Zakres wybranych wzorców " +
//                        "musi odpwiadaæ zdefiniowanej ilosci tych wzorców");
//            while (array.size()>0)  {
//                tab[k] = array.remove(rand.nextInt(array.size()));
//                System.out.println("wzorzec="+tab[k]);
//                k++;
//            }
//        }

        private static List setPatterns(ArrayList array,int patternCount,int size) {
            for(int i = 0; i <= size; i++) {
                    if (i % (size/patternCount) == 0 && i == 0) array.add(i+1);
                    else if (i % (size/patternCount) == 0) array.add(i);
                }
            return array;
        }
}
