/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

/**
 * Class for printing out informations.
 * @author Glowczynski Tomasz
 */
public class OutPrinter {

    public static void printTrainHeader(String dataSet, boolean validate, String algorithm,
            int trainCount, int validCount, int hiddCount, double[] parameters,
            double decay, String method, double range_min, double range_max) {

        System.out.println("------\n" + "Data set: " + dataSet);
        String val = validate ? "Image count: train|validate " + trainCount + "|" + validCount:
            "Image count: train " + trainCount;
        System.out.println(val + "\nPreprocess method: " + method + ", Range: " +
                "[" + range_min + ";" + range_max + "]");
        System.out.print("Hidden neurons count: " + (hiddCount-1)  +
            "\nAlgorithm type: "+ algorithm + " [");
        for (int i = 0; i < parameters.length; i++) {
            System.out.print(" " + parameters[i]);
        }
        System.out.println(", decay:" + decay + " ]\n------" + "\n" +
              "Process: Preprocessing images...");
    }
    public static void printTrainLearning() {
        System.out.println("Process: Neural Network learning...\n" +
                 "Epoch |    RMS     | Accuracy(train validate test)");
    }

    public static void printEpochResults(boolean validate, double lowValidError,
            int epochStopNr, long mid_time, long time, double trainAccuracy, int licz) {
        if (validate) {
            System.out.printf("\nBest validation accuracy: %.2f%% \nEpochs: %d\n", lowValidError, epochStopNr/2);
            System.out.println("Learning time: " + NeuralUtil.roundToDecimals((double)mid_time/1000,0) + " sek," +
                " Total learning time: " + NeuralUtil.roundToDecimals((double)time/1000,0) + " sek");
        }
        else {
            System.out.printf("\nValidation accuracy: %.2f%% \nEpochs: %d\n", trainAccuracy, licz);
            System.out.println("Total learning time: " + NeuralUtil.roundToDecimals((double)time/1000,0) + " sek");
        }
    }

    public static void printOverallTrainResults(boolean test, boolean validate,
            double trainAccuracy, double testAccuracy, double validateAccuracy,
            int licz, double actualRMS) {
         if (test && validate)
             System.out.printf("%d %.15f %.2f%% %.2f%% %.2f%%\n", licz, actualRMS,
                     trainAccuracy, validateAccuracy, testAccuracy);
         else if (validate)
             System.out.printf("%d %.15f %.2f%% %.2f%%\n", licz, actualRMS,
                     trainAccuracy, validateAccuracy);
         else if (test)
             System.out.printf("%d %.15f %.2f%% %.2f%% \n", licz, actualRMS, trainAccuracy, testAccuracy);
         else
             System.out.printf("%d %.15f %.2f%%\n", licz, actualRMS, trainAccuracy);
    }

    public static void printTestHeader(String dataSet, String algorithm,
            int testCount, int hiddCount, double[] parameters, double decay,
            String method, double range_min, double range_max) {

        System.out.println("Data set: " + dataSet);
        System.out.println("Image count: " + testCount);
        System.out.println("Preprocess method: " + method + ", Range: " +
                "[" + range_min + ";" + range_max + "]");
        System.out.println("Hidden neurons count: " + hiddCount);
        System.out.print("Algorithm type: "+ algorithm + " [");
        for (int i = 0; i < parameters.length; i++) {
            System.out.print(" " + parameters[i]);
        }
        System.out.println(", decay:" + decay + " ]\n------");
       /*-----------------Images preprocess----------------------------------*/
        System.out.println("Process: Preprocessing images...");
    }

    public static void printOverallTestResults(int testCount, int badRecCount, double accuracy) {
        System.out.println("----");
        System.out.println("Bad recognized images: " + badRecCount
                + "/" + testCount + " accuracy: "
                + accuracy + "%");
    }

}
