import java.util.ArrayList;

public class Neuron {
	
	private double value;				//wartosc neuronu
	public ArrayList<Synapse> inlinks;              // lista zawierająca sysnapsy przychodzące do neuronu
	public ArrayList<Synapse> outlinks;             // lista zawierająca sysnapsy wychodzące z neuronu
	private double sum;
	//private double output;                        //wartosc wyjsciowa neuronu po f.aktywacji
	
	public Neuron() {
		inlinks = new ArrayList<Synapse>();
		outlinks = new ArrayList<Synapse>();
	}
	
	
	/**
	 * Metoda nadaje wartosc dla danego neuronu
	 * @param value     wartosc
	 */
	public void setValue(double value) {
		this.value = value;
	}

        /**
         * Pobiera wartość neuronu
         * @return double
         */
	public double getValue() {
		return this.value;
	}

        /**
         * Wyliczenie warości wyjścia neuronu
         */
	public void computeOutput()
	  {
	    sum=0.0;
	    for (int i=0; i< inlinks.size(); i++) {
	    	sum += inlinks.get(i).getValue() * inlinks.get(i).from.getValue();
	    }
	  value = 1.0/(1.0 + Math.exp(-sum)); // sigmoid function
	  }
		
	 
}
