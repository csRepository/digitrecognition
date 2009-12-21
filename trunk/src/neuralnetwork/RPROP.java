package neuralnetwork;

/**
 *
 * @author tm
 */
public class RPROP extends Propagation{
    private double DELTAMAX;
    private double DELTAMIN;
    private double DELTAZERO;
    private double DECFACTOR;
    private double INCFACTOR;

    public RPROP(double[] parameters) {
        this.DELTAMAX  = parameters[0];
        this.DELTAMIN  = parameters[1];
        this.DELTAZERO = parameters[2];
        this.DECFACTOR = parameters[3];
        this.INCFACTOR = parameters[4];
    }

    @Override
    public void changeWeights(Neuron neuron){
          for (int i=0;i<neuron.getIncomingSyn().size();i++) {
                Synapse syn = neuron.getIncomingSyn().get(i);

                // compute product of accumulated error for the weight
                // for this epoch  and the last epoch
                double change = syn.getPrevGradient()*syn.getGradient();

                double gradient = syn.getGradient(); //

              //  double weightChange = 0;
                 if(change>0) {                      //no sign change
                     syn.setUpdateValue(Math.min(syn.getUpdateValue()*INCFACTOR, DELTAMAX));
                     syn.setDelta(Math.signum(gradient)*syn.getUpdateValue());
                     syn.setValue(syn.getValue()+syn.getDelta());
                    // syn.setPrevGradient(gradient);
                 }
                 else if (change<0) {               //sign change
                     syn.setUpdateValue(Math.max(syn.getUpdateValue()*DECFACTOR, DELTAMIN));
                     syn.setValue(syn.getValue()-syn.getPrevDelta());
                     syn.setGradient(0);
                 }
                 else if  (change==0){              
                     syn.setDelta(Math.signum(gradient)*syn.getUpdateValue());
                     syn.setValue(syn.getValue()+syn.getDelta());
                   //  syn.setPrevGradient(gradient);
                 }
            }
    }

    @Override
    public  void calcUpdate(Neuron neuron) {
        for (int i=0;i<neuron.getIncomingSyn().size();i++) {
           Synapse syn = neuron.getIncomingSyn().get(i);
           syn.setGradient(syn.getGradient()+getActualGradient(neuron,syn));
        // syn.setDelta(syn.getDelta()+syn.getGradient()*learningRate+momentum*syn.getDelta());
       //  System.out.println(syn.getDelta());
        }
    }

    @Override
    public void initialize(Neuron neuron) {
        for (int i=0;i<neuron.getIncomingSyn().size();i++) {
            Synapse syn = neuron.getIncomingSyn().get(i);
            syn.setUpdateValue(DELTAZERO);
        }
    }
}
    