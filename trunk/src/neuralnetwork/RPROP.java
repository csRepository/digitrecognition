package neuralnetwork;

/**
 *
 * @author tm
 */
public class RPROP extends Propagation{
    private final double DELTAMAX = 50;
    private final double DELTAMIN = 1e-6;
    private final double DELTAZERO = 0.1;
    private final double DECFACTOR = 0.5;
    private final double INCFACTOR = 1.2;
    double weightChange ;

    public RPROP() {

    }

    public void changeWeights(Neuron neuron){
          for (int i=0;i<neuron.getIncomingSyn().size();i++) {
                Synapse syn = neuron.getIncomingSyn().get(i);

                // compute product of accumulated error for the weight
                // for this epoch  and the last epoch
                double change = syn.getGradient()*getActualGradient(neuron,syn);

                double gradient = getActualGradient(neuron,syn); //
                //double weightChange = 0;
                 if(change>0) {                      //no sign change
                     syn.setDelta(Math.min(syn.getDelta()*INCFACTOR, DELTAMAX));
                     syn.setWeightChange(sign(gradient)*syn.getDelta());
                     syn.setValue(syn.getValue()+syn.getWeightChange());
                     syn.setGradient(gradient);
                 }
                 else if (change<0) {               //sign change
                     syn.setDelta(Math.max(syn.getDelta()*DECFACTOR, DELTAMIN));
                    // syn.setValue(syn.getValue()-syn.getWeightChange());
                     syn.setGradient(0);
                 }
                 else if  (change==0){              
                     syn.setWeightChange(sign(gradient)*syn.getDelta());
                     syn.setValue(syn.getValue()+syn.getWeightChange());
                     syn.setGradient(gradient);
                 }
            }
    }

    public  void calcUpdate(Neuron neuron) {
        for (int i=0;i<neuron.getIncomingSyn().size();i++) {
           Synapse syn = neuron.getIncomingSyn().get(i);
           syn.setGradient(syn.getGradient()+getActualGradient(neuron,syn));
       //  syn.setDelta(syn.getDelta()+syn.getPartDerivative()*learningRate+momentum*syn.getDelta());
       //  System.out.println(syn.getDelta());
        }
    }

    public void initialize(Neuron neuron) {
        for (int i=0;i<neuron.getIncomingSyn().size();i++) {
            Synapse syn = neuron.getIncomingSyn().get(i);
            syn.setDelta(DELTAZERO);
        }
    }

    private static int sign(double value) {
        if (value>0) return 1;
        else if (value<0) return -1;
        else return 0;
    }
}
    