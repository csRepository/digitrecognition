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
