package neuralnetwork;

import java.util.Random;

/**
 * Klasa uzywana w celu stworzenia synapsy - polaczenia pomiedzy
 * dwoma neuronami sasiednich warstw
 * @author tm
 */
public class Synapse {

    private double value;
    private double delta; //rprop and backprop weight change
    private double prevDelta;
    private double gradient ;  //weight gradient
    private double prevGradient;
    private double updateValue;    //RProp
    private Neuron fromNeuron;
    private Neuron toNeuron;
    private Random random = new Random();

    public Synapse(Neuron f, Neuron t) {
            fromNeuron = f;
            toNeuron = t;
            t.getIncomingSyn().add(this);
            f.getOutgoingSyn().add(this);
            value = random.nextDouble()*2*0.1-0.1;
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
     * @return the data
     */
    public double getDelta() {
        return delta;
    }

    /**
     * @param data the data to set
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
     * @param partDerivative the partDerivative to set
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
     * @param weightChange the weightChange to set
     */
    public void setUpdateValue(double updateValue) {
        this.updateValue = updateValue;
    }
}
