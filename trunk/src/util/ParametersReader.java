/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import javax.xml.xpath.XPathConstants;
import org.w3c.dom.NodeList;

/**
 *
 * @author tm
 */
public class ParametersReader {

    XPathReader reader;

    public ParametersReader(String path) {
         reader = new XPathReader(path);
    }

     public String getDefaultAlgorithm() {
        String expression = "/network/parameters/algorithm/text()";
        String algorithm = (String) reader.read(expression,XPathConstants.STRING);
        return algorithm;
    }

    public int getHiddNeuronsCount() {
        String expression = "/network/parameters/hidden/text()";
        int count =  Integer.parseInt((String) reader.read(expression,XPathConstants.STRING));
        return count;
    }

    public int getTrainPatternsCount() {
        String expression = "/network/parameters/patterns/train/text()";
        int count =  Integer.parseInt((String) reader.read(expression,XPathConstants.STRING));
        return count;
    }

    public int getTestPatternsCount() {
        String expression = "/network/parameters/patterns/test/text()";
        int count =  Integer.parseInt((String) reader.read(expression,XPathConstants.STRING));
        return count;
    }

    public double getRMS() {
        String expression = "/network/parameters/error/text()";
        double rms = (Double) reader.read(expression,XPathConstants.NUMBER);
        return rms;
    }

    public int getEpochsCount() {
        String expression = "/network/parameters/epochs/text()";
        int count =  Integer.parseInt((String) reader.read(expression,XPathConstants.STRING));
        return count;
    }

    public String getUpdateMethod() {
        String expression = "/network/parameters/change_method/text()";
        String method = (String) reader.read(expression,XPathConstants.STRING);
        return method;
    }

    public double[] getParametersRP() {
        double[] parameters = new double[5];
        String expression = "/network/algorithm[name='rp']/parameter/text()";
        NodeList node = (NodeList) reader.read(expression,XPathConstants.NODESET);
        for (int i = 0; i < node.getLength(); i++) {
            double d = Double.valueOf(node.item(i).getNodeValue());
            parameters[i] = d;
        }
        return parameters;
    }

    public double[] getParametersBP() {
        double[] parameters = new double[2];
        String expression = "/network/algorithm[name='bp']/parameter/text()";
        NodeList node = (NodeList) reader.read(expression,XPathConstants.NODESET);
        for (int i = 0; i < node.getLength(); i++) {
            double d = Double.valueOf(node.item(i).getNodeValue());
            parameters[i] = d;
        }
        return parameters;
    }

    public double[] getParametersQP() {
        double[] parameters = new double[4];
        String expression = "/network/algorithm[name='qp']/parameter/text()";
        NodeList node = (NodeList) reader.read(expression,XPathConstants.NODESET);
        for (int i = 0; i < node.getLength(); i++) {
            double d = Double.valueOf(node.item(i).getNodeValue());
            parameters[i] = d;
        }
        return parameters;
    }

}
