package neuralnetwork;


/**
 *
 * @author tm
 */
public class Backpropagation extends Propagation{
    	private double momentum;
        private double learningRate;
        private String method;

        public Backpropagation(String method, double momentum, double lrate) {
            this.momentum = momentum;
            this.learningRate = lrate;
            this.method = method;
        }

        public void changeWeights(Neuron neuron) {
            for (int i=0;i<neuron.getIncomingSyn().size();i++) {
                Synapse syn = neuron.getIncomingSyn().get(i);
               // syn.setDelta(learningRate*getPartDeriv(neuron,syn) + momentum*syn.getDelta());
                syn.setDelta(syn.getGradient()*learningRate+momentum*syn.getDelta());
                syn.setValue(syn.getValue()+syn.getDelta());
            }
        }

        protected static void bcalcUpdate(Neuron neuron) {
               for (int i=0;i<neuron.getIncomingSyn().size();i++) {
                Synapse syn = neuron.getIncomingSyn().get(i);
                 syn.setGradient(syn.getGradient()+getActualGradient(neuron,syn));
              //  syn.setDelta(syn.getDelta()+syn.getPartDerivative()*learningRate+momentum*syn.getDelta());
              //  System.out.println(syn.getDelta());
            }
        }
        private static void ocalcUpdate(Neuron neuron) {
               for (int i=0;i<neuron.getIncomingSyn().size();i++) {
                Synapse syn = neuron.getIncomingSyn().get(i);
                syn.setGradient(getActualGradient(neuron,syn));
              //  System.out.println(syn.getDelta());
               }
        }

        public void calcUpdate(Neuron neuron) {
            if (method.equals("batch")) {
                bcalcUpdate(neuron);
            }
            else ocalcUpdate(neuron);
        }

    /**
     * @param momentum the momentum to set
     */
    public void setMomentum(double momentum) {
        this.momentum = momentum;
    }

    /**
     * @param learningRate the learningRate to set
     */
    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    @Override
    public void initialize(Neuron neuron) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    

}