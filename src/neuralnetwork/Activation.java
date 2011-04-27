/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package neuralnetwork;

/**
 *
 * @author tm
 */
public class Activation {
    
    private static double primeTerm;

    public static double sigmoid (double u)
    {
        return 1.0 / (1 + Math.exp(-1.0 * u));
    }


    public static double sigmDerivative(double d) {
        return d * (1.0 - d) + primeTerm;
    }

     /**
     * @return the primeTerm
     */
    public static double getPrimeTerm() {
        return primeTerm;
    }

    /**
     * @param aPrimeTerm the primeTerm to set
     */
    public static void setPrimeTerm(double aPrimeTerm) {
        primeTerm = aPrimeTerm;
    }
}
