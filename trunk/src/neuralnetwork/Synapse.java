package neuralnetwork;

import java.util.Random;

/**
 * Makes a connection between two neighbours layers.
 * @author tm
 */
public class Synapse {

    private double value;
    private double delta; //rprop and backprop weight change
    private double prevDelta;
    private double gradient ;  //weight gradient
    private double prevGradient;
    private double updateValue;    //RProp
    private double learningRate;    //Delta-bar-delta
    private Neuron fromNeuron;
    private Neuron toNeuron;
    private Random random = new Random();

    public Synapse(Neuron f, Neuron t) {
        fromNeuron = f;
        toNeuron = t;
        t.getIncomingSyn().add(this);
        f.getOutgoingSyn().add(this);
        value = random.nextDouble() * 2 * 0.25 - 0.25;
        learningRate = 0.005;
    }

    /**
     * @return the fromNeuron
     */
    public Neuron getFromNeuron() {
        return fromNeuron;
    }

    /**
     * @return the toNeuron
     */
    public Neuron getToNeuron() {
        return toNeuron;
    }

    /**
     * @return the delta
     */
    public double getDelta() {
        return delta;
    }

    /**
     * @param delta the delta to set
     */
    public void setDelta(double delta) {
        this.delta = delta;
    }

    public double getPrevDelta() {
        return prevDelta;
    }

    public void setPrevDelta(double prevDelta) {
        this.prevDelta = prevDelta;
    }

    /**
     * @return the value
     */
    public double getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * @return the partDerivative
     */
    public double getGradient() {
        return gradient;
    }

    /**
     * @param gradient the gradient to set
     */
    public void setGradient(double gradient) {
        this.gradient = gradient;
    }

    public double getPrevGradient() {
        return prevGradient;
    }

    public void setPrevGradient(double prevGradient) {
        this.prevGradient = prevGradient;
    }

    /**
     * @return the weightChange
     */
    public double getUpdateValue() {
        return updateValue;
    }

    /**
     * @param updateValue the updateValue to set
     */
    public void setUpdateValue(double updateValue) {
        this.updateValue = updateValue;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }
    
}
