
package util
/**
 *
 * @author Glowczynski Tomasz
 */
class GPathReader {

    def network

    GPathReader(file) {
        this.network = new XmlSlurper().parse(file)
    }

    int getHiddNeuronsCount() {
        def count = network.parameters.hidden.toInteger()
    }
//-------------------------<patterns>-------------------------------------------
    int getTrainPatternsCount() {
        def count = network.parameters.patterns.train.@count.toInteger()
    }
    int getTestPatternsCount() {
        def count = network.parameters.patterns.test.@count.toInteger()
    }
    int getValidatePatternsCount() {
        def count = network.parameters.patterns.valid.@count.toInteger()
    }
    String getTrainDataSet() {
        def set = network.parameters.patterns.train.@set.text().trim()
    }
    String getTestDataSet() {
        def set = network.parameters.patterns.test.@set.text().trim()
    }
    String getValidateDataSet() {
        def set = network.parameters.patterns.valid.@set.text().trim()
    }
    String getPreprocessMethod() {
        def method = network.parameters.patterns.preprocess.@method.text().trim()
    }
    double getRangeMin() {
        def min = network.parameters.patterns.preprocess.@min.toDouble()
    }
    double getRangeMax() {
        def max = network.parameters.patterns.preprocess.@max.toDouble()
    }
//----------------------------------------------------------------------------
    double getRMS() {
        def rms = network.parameters.error.toDouble()
    }
    int getEpochsCount() {
        def count = network.parameters.epochs.toInteger()
    }
    double getAccuracy() {
        def accuracy = network.parameters.accuracy.toDouble()
    }
//---------------------------Algorithm----------------------------------------
    String getDefaultAlgorithm() {
        def algorithm = network.parameters.algorithm.type.text().trim()
    }
    String getUpdateMethod() {
        def method = network.parameters.algorithm.update_method.text().trim()
    }
    boolean isBackpropSkip() {
        def is = network.parameters.algorithm.backprop_skip.toBoolean()
    }
    double getWeightsDecay() {
        def decay = network.parameters.algorithm.weights_decay.toDouble()
    }
    double getSigmoidPrimeTerm() {
        def decay = network.parameters.algorithm.sgm_prime_term.toDouble()
    }
//------------------------------------------------------------------------------
    String getWeightsFileName() {
        def method = network.parameters.weights_file.text().trim()
    }
    boolean isValidate() {
        def is = network.parameters.validate.toBoolean()
    }
    boolean isTest() {
        def is = network.parameters.test.toBoolean()
    }
    double[] getParameters(String name) {
        def parameters = network.algorithm.find { it.name == name}.parameter
        parameters.collect {it.toDouble()}
    }
}

