/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import database.MNISTDatabase;
import java.io.File;
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
    public static ParametersReader readConfigFile(String[] args) {
        ParametersReader parameters;
                if (args.length > 0) {
                File file = new File(args[0]);
                if (!file.exists()) {
                     System.err.println("Brak pliku konfiguracyjnego");
                     System.exit(1);
                } else
                  return  parameters = new ParametersReader(args[0]);
        }
        else {
                System.err.println("Brak parametru z plikiem konfiguracyjnym");
                System.exit(1);
        }
        return null;
    }

    /*
     * Przygotowuej tablice wszystkich wejsc (tablica [nr][tablica_obrazu[28][28]])
     */
    public static double[][][] prepareInputSet(ArrayList<Integer> array, MNISTDatabase data) {
        double[][][] tab = new double[array.size()][][];
        for (int i = 0; i < array.size(); i++) {
            tab[i] = scale(getImage(array.get(i),data));
        }
        return tab;
    }

    /*
     * Przygotowuej tablice wszystkich wyjsc (tablica [nr][tablica_wyjsc[10])
     */
    public static int[][] prepareOutputSet(ArrayList<Integer> array, int outSize,MNISTDatabase data) {
        int tab[][] = new int[array.size()][10];
        for (int i = 0; i < array.size(); i++) {
            int d[] = new int[outSize];
            for (int j = 0; j < outSize; j++)
                d[j] = 0;
            d[ getLabel(array.get(i), data) ] = 1;
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

    private static int getLabel(int digit, MNISTDatabase data) {
        return data.readLabel("train", digit);
    }

    private static int[][] getImage(int digit, MNISTDatabase data) {
        return data.readImage("train", digit);
    }

    /*
     * Bianryzacja danych
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
               data1[j][k] = normalize(data[j][k], 255, 0, 1, -1);
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
