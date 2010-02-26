
package util
import org.xml.sax.SAXException
/**
 *
 * @author Glowczynski Tomasz
 */
class GPathReader {

    private static network
    private static int epochsCount
    private static int hiddenNeuronsCount
    private static double accuracy; //parameter z pliku
    private static int trainPatternsCount,validPatternsCount,testPatternsCount;
    private static boolean backpropSkip, validate, test;
    private static String trainDataSet, testDataSet, validDataSet
    private static String preprocessMethod
    private static double decayValue
    private static double rmsValue
    private static double accuracyValue
    private static String algorithmType
    private static String updateMethodType
    private static double[] algParameters
    private static String weightsFileName
    private static double sigmoidPrimeTerm
    private static double rangeMin
    private static double rangeMax

    private GPathReader() {}
    
    public static setParametersFile(String file) {
        try {
            network = new XmlSlurper().parse(file)
            readParametersFromFile()
        }
        catch (SAXException ex) {
            System.err.println("Error parsing " + file);
        }
    }

    private static void readParametersFromFile() {
        trainPatternsCount = network.parameters.patterns.train.@count.toInteger()
        testPatternsCount  = network.parameters.patterns.test.@count.toInteger()
        validPatternsCount = network.parameters.patterns.valid.@count.toInteger()
        hiddenNeuronsCount = network.parameters.hidden.toInteger()
        trainDataSet       = network.parameters.patterns.train.@set.text().trim()
        testDataSet        = network.parameters.patterns.test.@set.text().trim()
        validDataSet       = network.parameters.patterns.valid.@set.text().trim()
        preprocessMethod   = network.parameters.patterns.preprocess.@method.text().trim()
        rangeMin           = network.parameters.patterns.preprocess.@min.toDouble()
        rangeMax           = network.parameters.patterns.preprocess.@max.toDouble()
        rmsValue           = network.parameters.error.toDouble()
        epochsCount        = network.parameters.epochs.toInteger()
        accuracy           = network.parameters.accuracy.toDouble()
        algorithmType      = network.parameters.algorithm.type.text().trim()
        updateMethodType   = network.parameters.algorithm.update_method.text().trim()
        backpropSkip       = network.parameters.algorithm.backprop_skip.toBoolean()
        decayValue         = network.parameters.algorithm.weights_decay.toDouble()
        sigmoidPrimeTerm   = network.parameters.algorithm.sgm_prime_term.toDouble()
        weightsFileName    = network.parameters.weights_file.text().trim()
        validate           = network.parameters.validate.toBoolean()
        test               = network.parameters.test.toBoolean()
        algParameters      = getParameters(algorithmType)
    }

    private static double[] getParameters(String name) {
        def parameters = network.algorithm.find { it.name == name}.parameter
        parameters.collect {it.toDouble()}
    }

    public static double getAccuracy() {
        return accuracy;
    }

    public static double getAccuracyValue() {
        return accuracyValue;
    }

    public static double[] getAlgParameters() {
        return algParameters;
    }

    public static String getAlgorithmType() {
        return algorithmType;
    }

    public static double getDecayValue() {
        return decayValue;
    }

    public static int getEpochsCount() {
        return epochsCount;
    }

    public static int getHiddenNeuronsCount() {
        return hiddenNeuronsCount;
    }

    public static boolean isBackpropSkip() {
        return backpropSkip;
    }

    public static boolean isTest() {
        return test;
    }

    public static boolean isValidate() {
        return validate;
    }

    public static String getPreprocessMethod() {
        return preprocessMethod;
    }

    public static double getRangeMax() {
        return rangeMax;
    }

    public static double getRangeMin() {
        return rangeMin;
    }

    public static double getRmsValue() {
        return rmsValue;
    }

    public static double getSigmoidPrimeTerm() {
        return sigmoidPrimeTerm;
    }

    public static String getTestDataSet() {
        return testDataSet;
    }

    public static int getTestPatternsCount() {
        return testPatternsCount;
    }

    public static String getTrainDataSet() {
        return trainDataSet;
    }

    public static int getTrainPatternsCount() {
        return trainPatternsCount;
    }

    public static String getUpdateMethodType() {
        return updateMethodType;
    }

    public static String getValidDataSet() {
        return validDataSet;
    }

    public static int getValidPatternsCount() {
        return validPatternsCount;
    }

    public static String getWeightsFileName() {
        return weightsFileName;
    }
}

