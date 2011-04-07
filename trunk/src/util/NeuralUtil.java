/**
 *  Copyright 2010 Główczyński Tomasz
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 */

package util;

import model.database.MNISTDatabase;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.neuralnetwork.Layer;
import model.neuralnetwork.Neuron;

/**
 * Class that contains methods for handling train and test programs.
 * @author Glowczynski Tomasz
 */
public class NeuralUtil {
    private static Random rand = new Random();
   // static HashMap map;
    private NeuralUtil() {}
    /*
     * Wczytuje plik konfiguracyjny z parametrami sieci
     */
    public static void readConfigFile(String[] args) {
         if (args.length > 0) {
            try {
                GPathReader.setParametersFile(args[0]);
            }
            catch (NullPointerException ex){
                System.err.println("Brak pliku konfiguracyjnego");
                System.exit(1);
            } 
        }
        else {
            System.err.println("Brak parametru z plikiem konfiguracyjnym" +
                                " np. java -jar Train \"parameters.xml\" ");
            System.exit(1);
        }
    }


    /**
     * Prepare array for all inputs (3-dimesional array: [nr][array[28][28]])
     * @param array
     * @param data
     * @param method    data preprocess method (Method must be defined in NeuralUtil class)
     * @return
     */
    public static double[][][] prepareInputSet(ArrayList<Integer> array, MNISTDatabase data,
            String dataType, String methodName, double min, double max) {
        double[][][] tab = new double[array.size()][][];
        for (int i = 0; i < array.size(); i++) {
            Object[] methodParam =  new Object[] {getImage(array.get(i), data, dataType),min,max};
            Method method = null;
            try {
                method = NeuralUtil.class.getDeclaredMethod(methodName, new Class[]{int[][].class, double.class, double.class});
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(NeuralUtil.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(NeuralUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                tab[i] = (double[][]) method.invoke(NeuralUtil.class, methodParam);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(NeuralUtil.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(NeuralUtil.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(NeuralUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
     //   System.out.println("");
        return tab;
    }

    /*
     * Przygotowuej tablice wszystkich wyjsc (tablica [nr][tablica_wyjsc[10])
     */
    public static double[][] prepareOutputSet(ArrayList<Integer> array, int outSize,MNISTDatabase data, String dataType) {
        double tab[][] = new double[array.size()][10];
      //  map = new HashMap();
        for (int i = 0; i < array.size(); i++) {
            double d[] = new double[outSize];
            for (int j = 0; j < outSize; j++)
                d[j] = 0;
            d[ getLabel(array.get(i), data, dataType) ] = 1;
            tab[i] = d;
//            if (map.containsKey(getLabel(array.get(i), data, dataType))) {
//                int value = (Integer)map.get(getLabel(array.get(i), data, dataType));
//                value++;
//                map.put(getLabel(array.get(i), data, dataType), value);
//            } else
//            map.put(getLabel(array.get(i), data, dataType), 1);
        }
       //z System.out.println(map);
        return tab;
    }
     public static void setInputLayer(Layer inputLayer, int pattNr,double[][][] images) {
        int index = 0;
        int length = images[pattNr].length;
        for (int k = 0; k < length; k++) {
            for (int j = 0; j < length; j++) {
                Neuron neuron = inputLayer.getNeuron(index);
                neuron.setValue(images[pattNr][k][j]);
                index++;
            }
        }
    }
    public static double[] setOutputLayer(int pattNr, double[][] labels) {
         return labels[pattNr];
    }

     /*
     * Tworzy tabice z numerami wzorcow (jednakowy rozrzut pomiedzy wzorcami)
     */
    public static void setPatterns(ArrayList array,int patternCount,int startPattern, int endPattern) {
        int size = endPattern - startPattern + 1;
        for(int i = startPattern; i <= endPattern; i++) {
               // if (i % (size/patternCount) == 0 && i == 0) array.add(i+1);
                if (i % (size/patternCount) == 0) array.add(i);
        }
    }
        /*
     * Losowanie liczb z zadanego przedzialu [a,b] do tablicy
     */
    public static List randomizePatterns(ArrayList<Integer> array) {
        ArrayList<Integer> list = new ArrayList();
        for (int i=0;i<array.size();i++) {
            list.add(array.get(i));
        }
       
        int k = 0;
        while (list.size()>0)  {
            int elem = list.remove(rand.nextInt(list.size()));
            array.set(k, elem);
            k++;
        }
        return array;
    }

    private static int getLabel(int digit, MNISTDatabase data, String dataType) {
        return data.readLabel(dataType, digit);
    }

    private static int[][] getImage(int digit, MNISTDatabase data, String dataType) {
        return data.readImage(dataType, digit);
    }

    /*
     * Binaryzacja danych
     */
    private static double[][] binarize(int[][] data, double min, double max) {
        int suma = 0;
        for (int j = 0; j<data.length; j++) {
            for (int k = 0; k < data.length; k++) {
                suma += data[j][k];
            }

        }
        double srednia = suma / (28*28);
        double data1[][] = new double[data.length][data.length];
        for (int j = 0; j < data.length; j++) {
            for (int k = 0; k < data.length; k++) {
                if (data[j][k] >= srednia) data1[j][k] = max;
                else  data1[j][k] = min;
            }
        }
        return data1;
    }

private static double[][] OtsuTreshold(int[][] data, double min, double max) {
    // Obliczenie histogramu
    int[] histData = new int[256];
     for (int i = 0; i < data.length; i++) {
        for (int j = 0; j < data[i].length; j++) {
           int h = data[i][j];
       histData[h] ++;
        }
    }
    // Liczba wszytkich pikseli
    int total = data.length * data[0].length;

    //suma wszystkich wartosci histogramu x prog - do obiczenia sredniej
    float sum = 0;
    for (int t=0 ; t<256 ; t++) sum += t * histData[t];

    float sumB = 0;
    int wB = 0;
    int wF = 0;

    float varMax = 0;
    int threshold = 0;

    for (int t=0 ; t<256 ; t++) {
       wB += histData[t];               // Tlo wagi
       if (wB == 0) continue;

       wF = total - wB;                 // Obiekt wagi
       if (wF == 0) break;

       sumB += (float) (t * histData[t]);

       float mB = sumB / wB;            // wartosc srednia tla
       float mF = (sum - sumB) / wF;    // wartosc srednia obiektu
       // Wyznaczenie wariancji miedzy-klasowej
       float varBetween = (float) (wB * wF) * (mB - mF) * (mB - mF);
       // Sprawdzenie czy wariancja progu jest wieksza od poprzedniej
       if (varBetween > varMax) {
          varMax = varBetween;
          threshold = t;
       }
    }
    //Progowanie obrazu
    double[][] outData = new double[data.length][data[0].length];
    for (int i = 0; i < data.length; i++) {
        for (int j = 0; j < data[i].length; j++) {
           outData[i][j] = (data[i][j] >= threshold) ?  max : min;
        }
    }
    return outData;

}
    
    /*
     * Skalowanie calej tablicy z przedzialem wartosci od - do do
     * nowej tablicy nowym przedziale wartosci od - do
     */
      private static double[][] scale(int[][] data, double min,  double max) {
        double data1[][] = new double[data.length][data.length];
        for (int j = 0; j < data.length; j++) {
            for (int k = 0; k < data.length; k++) {
               data1[j][k] = normalize(data[j][k], 255, 0, max, min);
            }
        }
        return data1;
    }

     /*
     * Dokonuje normalizacji danych do wybranego przedzialu
     */
      private static double normalize(int value,int max,int min,double new_max,double new_min) {
            double new_value = (double) (new_max - new_min) * (value - min) / (max - min) + new_min;
            return new_value;
      }
      
      public static double roundToDecimals(double d, int c) {
        int temp=(int)((d*Math.pow(10,c)));
        return (((double)temp)/Math.pow(10,c));
      }

      public static int validate(Layer outputLayer, double[] desiredAns, int pattNr, boolean printAll) {
        double max[] = new double[2];
        double pom = 0;

         //classification error----------------------------------------
         for (int j=0;j<outputLayer.size();j++) {
            Neuron neuron = outputLayer.getNeuron(j);
            max[1] = Math.max(max[1], neuron.getValue());
            if (max[1]!=pom) {
                max[0] = outputLayer.indexOf(neuron);
                pom = max[1];
            }
        }
         //jesli wyjscie odpowiedzi oczekiwanych != 1 czyli cyfra z bazy nie odpowiada
         //najwiekszemu wyjsciu to cyfra nie zostala rozpoznana
        // int digit = 0;
        if (!printAll) {
         if (desiredAns[(int)max[0]] != 1)
            return 1;   //zle rozpoznany
         else return 0; //rozpoznany
        }
        else {
            DecimalFormat digitDec = new DecimalFormat("#,##0.00000000000");//formats to 2 decimal places
            String value = digitDec.format(max[1]);

        //------------------wypisywanie zle rozpoznanych wzorcow
             int digit = 0;
             if (desiredAns[(int)max[0]]!= 1)
             {
                 for (int j = 0; j < desiredAns.length; j++) {
                     if (desiredAns[j] == 1)  digit = j;
                 }
                 System.out.println("Digit: " + digit + " Recognized: " +
                    + (int)max[0] + " value: " + value + " patternNr:" + pattNr );
                // System.out.println(digit + "->"
                 //   + (int)max[0] + " " +pattNr );
                 return 1;
            }
             else return 0; //rozpoznany
        }
    }

    public static double calculateClassError(int badRecognizedCount, int patternsCount) {
        return NeuralUtil.roundToDecimals(100 - badRecognizedCount/(double)patternsCount*100,2);
    }
    public static long getSeed() {
        String wFile = GPathReader.getWeightsFileName();
        long seed;
        try {
            seed = Long.parseLong(wFile.substring(wFile.length()-6,wFile.length()-4));
            //seed = System.currentTimeMillis();
        }
        catch (NumberFormatException ex){
            seed = System.currentTimeMillis();
        }
       // System.out.println(seed);
        return seed;
    }
}
