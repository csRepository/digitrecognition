package util;

/**
 * Class for printing out informations.
 * @author Glowczynski Tomasz
 */
public class OutPrinter {
    private static String testDataSet      = GPathReader.getTestDataSet();
    private static String trainDataSet     = GPathReader.getTrainDataSet();
    private static boolean validate        = GPathReader.isValidate();
    private static boolean test            = GPathReader.isTest();
    private static int trainPatternsCount  = GPathReader.getTrainPatternsCount();
    private static int testPatternsCount   = GPathReader.getTestPatternsCount();
    private static int validPatternsCount  = GPathReader.getValidPatternsCount();
    private static int hiddenNeuronsCount  = GPathReader.getHiddenNeuronsCount();
    private static double decay            = GPathReader.getDecayValue();
    private static String algorithmType    = GPathReader.getAlgorithmType();
    private static double[] algParam       = GPathReader.getAlgParameters();
    private static double rangeMin         = GPathReader.getRangeMin();
    private static double rangeMax         = GPathReader.getRangeMax();
    private static String preprocessMethod = GPathReader.getPreprocessMethod();

    public static void printTrainHeader() {

        System.out.println("------\n" + "Data set: " + trainDataSet);
        String val = validate ? "Image count: train|validate " + trainPatternsCount
                + "|" + validPatternsCount : "Image count: train " + trainPatternsCount;
        System.out.println(val + "\nPreprocess method: " + preprocessMethod + ", Range: " +
                "[" + rangeMin + ";" + rangeMax + "]");
        System.out.print("Hidden neurons count: " + hiddenNeuronsCount  +
            "\nAlgorithm type: "+ algorithmType + " [");
        for (int i = 0; i < algParam.length; i++) {
            System.out.print(" " + algParam[i]);
        }
        System.out.println(", decay:" + decay + " ]\n------" + "\n" +
              "Process: Preprocessing images...");
    }

    public static void printTrainLearning() {
        System.out.println("Process: Neural Network learning...\n" +
                 "Epoch |    RMS     | Accuracy(train validate test)");
    }

    public static void printEpochResults(double lowValidError, int epochStopNr,
            long mid_time, long time, double trainAccuracy, int licz) {
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

    public static void printOverallTrainResults(double trainAccuracy, double testAccuracy, 
            double validateAccuracy, int licz, double actualRMS) {
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

    public static void printTestHeader() {
        System.out.println("Data set: " + testDataSet);
        System.out.println("Image count: " + testPatternsCount);
        System.out.println("Preprocess method: " + preprocessMethod + ", Range: " +
                "[" + rangeMin + ";" + rangeMax + "]");
        System.out.println("Hidden neurons count: " + hiddenNeuronsCount);
        System.out.print("Algorithm type: "+ algorithmType + " [");
        for (int i = 0; i < algParam.length; i++) {
            System.out.print(" " + algParam[i]);
        }
        System.out.println(", decay:" + decay + " ]\n------");
       /*-----------------Images preprocess----------------------------------*/
        System.out.println("Process: Preprocessing images...");
    }

    public static void printOverallTestResults(int badRecCount, double accuracy) {
        System.out.println("----");
        System.out.println("Bad recognized images: " + badRecCount
                + "/" + testPatternsCount + " accuracy: "
                + accuracy + "%");
    }

}
