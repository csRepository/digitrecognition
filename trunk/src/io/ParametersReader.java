/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package io;

import javax.xml.xpath.XPathConstants;

/**
 *
 * @author tm
 */
public class ParametersReader {

    XPathReader reader;

    public ParametersReader() {
         reader = new XPathReader("parameters.xml");
    }

    public double getLearningRate() {
        String expression = "/network/algorithm[name='backprop']/parameter[@attr='learning_rate']/text()";
        double learningRate = (Double) reader.read(expression,XPathConstants.NUMBER);
        return learningRate;
    }

    public double getMomentum() {
        String expression = "/network/algorithm[name='backprop']/parameter[@attr='momentum']/text()";
        double momentum = (Double) reader.read(expression,XPathConstants.NUMBER);
        return momentum;
    }

    public int getHiddNeuronsCount() {
        String expression = "/network/structure/hidden/text()";
        int count =  Integer.parseInt((String) reader.read(expression,XPathConstants.STRING));
        return count;
    }

}
