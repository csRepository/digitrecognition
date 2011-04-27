package algorithms;

/**
 * Factory to make a instance of any implemented algorithm.
 * @author Glowczynski Tomasz
 */
public class AlgorithmsFactory {

    /**
     * Returns instance of one of Propagation subclasses
     * @param name  algorithm name
     * @param parameters algorithm class parameters
     * @return  algorithm class instance
     */
    public static Propagation getInstance(String name, double[] parameters) {
        return 
            name.equalsIgnoreCase("BackPropagation")      ? new BackPropagation     (parameters) :
            name.equalsIgnoreCase("ResilentPropagation")  ? new ResilentPropagation (parameters) :
            name.equalsIgnoreCase("QuickPropagation")     ? new QuickPropagation    (parameters) :
            name.equalsIgnoreCase("SuperSAB")             ? new SuperSAB            (parameters) :
            name.equalsIgnoreCase("DeltaBarDelta")        ? new DeltaBarDelta       (parameters) :
        null;
    }
}
