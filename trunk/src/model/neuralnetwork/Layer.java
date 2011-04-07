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

import java.util.ArrayList;

/**
 * Implements the Neural Network layer. Each layer contains
 * a list of the neurons.
 * @author Glowczynski Tomasz
 */
public class Layer extends ArrayList<Neuron> {
	
	//private ArrayList neurons;

	public Layer (int n) {
		//neurons = new ArrayList();
		for(int i=0;i<n;i++)
                {
                    add(new Neuron());
                }
	}
	/**
         * Zwraca referencje do obiektu Neuron
         * @param n numer neuronu
         * @return Neuron
         */
	public Neuron getNeuron(int n) {
		return get(n);
	  }
}
