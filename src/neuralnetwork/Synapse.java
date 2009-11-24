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
    private double gradient ;  //weight gradient
    private double weightChange;
    private Neuron fromNeuron;
    private Neuron toNeuron;
    private Random random = new Random();

    public Synapse(Neuron f, Neuron t) {
            fromNeuron = f;
            toNeuron = t;
            t.getIncomingSyn().add(this);
            f.getOutgoingSyn().add(this);
            value = random.nextDouble()*2*0.2-0.2;
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

    /**
     * @return the weightChange
     */
    public double getWeightChange() {
        return weightChange;
    }

    /**
     * @param weightChange the weightChange to set
     */
    public void setWeightChange(double weightChange) {
        this.weightChange = weightChange;
    }
}
