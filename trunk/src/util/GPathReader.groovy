
package util

/**
 *
 * @author tm
 */
class GPathReader {

    def network

    GPathReader(file) {
        this.network = new XmlSlurper().parse(file)
    }

    String getDefaultAlgorithm() {
        def algorithm = network.parameters.algorithm.text()
    }

    int getHiddNeuronsCount() {
        def count = network.parameters.hidden.toInteger()
    }
    int getTrainPatternsCount() {
        def count = network.parameters.patterns.train.count.toInteger()
    }
    int getTestPatternsCount() {
        def count = network.parameters.patterns.test.count.toInteger()
    }
    String getTrainDataSet() {
        def set = network.parameters.patterns.train.set.text()
    }

    String getTestDataSet() {
        def set = network.parameters.patterns.test.set.text()
    }
    String getPreprocessMethod() {
        def method = network.parameters.patterns.preprocess.text()
    }
    double getRMS() {
        def rms = network.parameters.error.toDouble()
    }
    int getEpochsCount() {
        def count = network.parameters.epochs.toInteger()
    }
    double getAccuracy() {
        def accuracy = network.parameters.accuracy.toDouble()
    }
    String getUpdateMethod() {
        def method = network.parameters.change_method.text()
    }
    String getWeightsFileName() {
        def method = network.parameters.weights_file.text()
    }
    double getWeightsDecay() {
        def decay = network.parameters.weights_decay.toDouble()
    }
    boolean isBackpropSkip() {
        def is = network.parameters.backprop_skip.toBoolean()
    }
    double[] getParameters(String name) {
        def parameters = network.algorithm.find { it.name == name}.parameter
        parameters.collect {it.toDouble()}
    }
}

