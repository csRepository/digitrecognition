/**
 *  Copyright 2010 Główczyński Tomasz
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 */

package model.neuralnetwork;

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
