/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import database.MNISTDatabase;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class that contains methods for handling train and test programs.
 * @author tm
 */
public class NeuralUtil {
    /*
     * Wczytuje plik konfiguracyjny z parametrami sieci
     */
    public static GPathReader readConfigFile(String[] args) {
         GPathReader read;
                if (args.length > 0) {
                File file = new File(args[0]);
                if (!file.exists()) {
                     System.err.println("Brak pliku konfiguracyjnego");
                     System.exit(1);
                } else
                  return  read = new GPathReader(args[0]);
        }
        else {
                System.err.println("Brak parametru z plikiem konfiguracyjnym" +
                                    "np. java Train \"parameters.xml\" ");
                System.exit(1);
        }
        return null;
    }

    /**
     * Prepare array for all inputs (3-dimesional array: [nr][array[28][28]])
     * @param array
     * @param data
     * @param method    data preprocess method (Method must be defined in NeuralUtil class)
     * @return
     */
    public static double[][][] prepareInputSet(ArrayList<Integer> array, MNISTDatabase data, String dataType, String methodName) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        double[][][] tab = new double[array.size()][][];
        for (int i = 0; i < array.size(); i++) {
            Object[] methodParam =  new Object[] {getImage(array.get(i), data, dataType)};
            Method method = NeuralUtil.class.getDeclaredMethod(methodName,new Class[] {int[][].class});
            tab[i] = (double[][]) method.invoke(NeuralUtil.class, methodParam);
        }
        return tab;
    }

    /*
     * Przygotowuej tablice wszystkich wyjsc (tablica [nr][tablica_wyjsc[10])
     */
    public static int[][] prepareOutputSet(ArrayList<Integer> array, int outSize,MNISTDatabase data, String dataType) {
        int tab[][] = new int[array.size()][10];
        for (int i = 0; i < array.size(); i++) {
            int d[] = new int[outSize];
            for (int j = 0; j < outSize; j++)
                d[j] = 0;
            d[ getLabel(array.get(i), data, dataType) ] = 1;
            tab[i] = d;
        }
        return tab;
    }

     /*
     * Tworzy tabice z numerami wzorcow (jednakowy rozrzut pomiedzy wzorcami)
     */
    public static List setPatterns(ArrayList array,int patternCount,int size) {
        for(int i = 1; i <= size; i++) {
               // if (i % (size/patternCount) == 0 && i == 0) array.add(i+1);
                if (i % (size/patternCount) == 0) array.add(i);
            }
        return array;
    }

        /*
     * Losowanie liczb z zadanego przedzialu [a,b] do tablicy
     */
    public static List randomizePatterns(ArrayList<Integer> array) {
        ArrayList<Integer> list = new ArrayList();
        for (int i=0;i<array.size();i++) {
            list.add(array.get(i));
        }
        Random rand = new Random();
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
    private static double[][] binarize(int[][] data) {
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
                if (data[j][k] >= srednia) data1[j][k] = 1;
                else  data1[j][k] = 0;
            }
        }
        return data1;
    }
    /*
     * Skalowanie calej tablicy z przedzialem wartosci od - do do
     * nowej tablicy nowym przedziale wartosci od - do
     */
      private static double[][] scale(int[][] data) {
        double data1[][] = new double[data.length][data.length];
        for (int j = 0; j < data.length; j++) {
            for (int k = 0; k < data.length; k++) {
               data1[j][k] = normalize(data[j][k], 255, 0, 1, 0);
            }
        }
        return data1;
    }

     /*
     * Dokonuje normalizacji danych do wybranego przedzialu
     */
      private static double normalize(int value,int max,int min,double new_max,double new_min) {
            double new_value = (double) (value - min) / (max - min) * (new_max - new_min) + new_min;
            return new_value;
        }
}
