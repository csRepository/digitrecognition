import java.util.Random;

public class Synapse {

	private double value;
	public Neuron from;
	public Neuron to;
	private Random random = new Random();

	public Synapse(Neuron f, Neuron t) {
		from = f;
		to = t;
		t.inlinks.add(this);
		f.outlinks.add(this);
		value = random.nextDouble() / 5.0;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
	
	public double getValue() {
		return this.value;
	}
	
}
