import database.MNISTDatabase;
import database.MNISTImageFile;
import database.MNISTLabelFile;


public class Main {

	private static Layer OutputLayer;				// warstwa wyjsciowa
	private static Layer InputLayer;				// warstwa wejsciowa
	private static Neuron Neuron;
	private static Layer HiddenLayer;
        private static int nIn,nHidd,nOut;
        private static NeuralNet neuralNetwork;
        private static MNISTDatabase Data;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

                nIn=28*28+1; //wielkosc obrazu + bias
                nHidd=15+1;  //l. neuronow + bias
                nOut=10;
		neuralNetwork = new NeuralNet(); 	// tworzenie sieci neuronwej
		neuralNetwork.addLayer(nIn);	   		// dodanie warstwy wejsciowej
		neuralNetwork.addLayer(nHidd);  		// dodanie warstwy ukrytej
		neuralNetwork.addLayer(nOut);  			// dodanie warstwy wyjsciowej

		InputLayer = neuralNetwork.getLayer(0);
		HiddenLayer = neuralNetwork.getLayer(1);
		OutputLayer = neuralNetwork.getLayer(2);

                //wczytywanie obrazu z bazy MNIST
                Data = new MNISTDatabase();
                int [][] image = readImage("training",4);

                int neuNr=0;
                for (int j=0;j<image.length;j++) {
                    for (int k=0;k<image.length;k++) {
                        double vn=normalize(image[j][k],255,0,1,0);
                        Neuron = (Neuron) InputLayer.getNeuron(neuNr);
                        Neuron.setValue(vn);
                        neuNr++;
                    }
                }
			
                 InputLayer.getNeuron(InputLayer.size()-1).setValue(1); //bias
                 HiddenLayer.getNeuron(HiddenLayer.size()-1).setValue(1);//bias
                 connLayer(nIn, nHidd-1,0,1);       //polaczenie warstw i wyliczenie wartosci
                 connLayer(nHidd, nOut,1,2);
                 neuralNetwork.propagate();

                 int [][] array = initAnsLay();

                 for (int i=0;i<10;i++) {
                     String str="";
                    for (int j=0;j<nOut;j++) {
                        str=str+array[i][j];
                    }
                 System.out.println(str);
                 }
        }
        private static double normalize(int value,int max,int min,int new_max,int new_min) {
            double new_value = (value-min)/(max-min)*(new_max-new_min)+new_min;
            return new_value;
        }
        /**
         *
         * @param layer warstwa
         * @param n liczba neuronow war. 1
         * @param n1 liczba neuronow war. 2
         * @param sourceLayer nazwa warstwy zrodlwoej
         * @param destLayer nazwa warstwy docelowej
         */
        private static void connLayer(int n, int n1,int sourceLayer, int destLayer) {
            for (int i=0;i<n;i++) {   
			for (int j=0;j<n1;j++) { 
				neuralNetwork.connect(sourceLayer, i, destLayer, j);      //polaczenie neuronow
				//Neuron = (Neuron) HiddenLayer.getNeuron(j);
				//System.out.println("waga["+j+","+i+"]="+Neuron.inlinks.get(i).getValue());
			}
		}
        }


        private static int[][] readImage(String kind,int nr) {
            MNISTImageFile imgFile;
            MNISTLabelFile lblFile;
            if (kind.equals("training")) {
                imgFile = Data.trainImgF;
                lblFile = Data.trainLblF;
            } else {
                imgFile = Data.testImgF;
                lblFile = Data.testLblF;
            }
                imgFile.setCurr(nr);      //obraz
                lblFile.setCurr(nr);      //etykieta
                System.out.println(lblFile.readData());
                int [][] image = imgFile.readData();
                return image;
        }
        private static int [][] initAnsLay() {
            Layer ansLayer= new Layer(nOut);
            int [][] array = new int[nOut][nOut];
            for (int i=0;i<ansLayer.size();i++) {
                for (int j=0;j<nOut;j++) {
                    if (i==j) {
                        array[i][j]=1;
                    }
                    else
                        array[i][j]=0;
               //     array[i][j]=0;
                //    array[i][i]=1;
                }

            }
            return array;
        }
}
   //poprawic strukture sieci glownie bias, wypisac wagi(inlinks,outlinks - czy bias
        // ma tez inlinks? - sprawdzic)