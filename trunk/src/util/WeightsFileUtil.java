/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import neuralnetwork.NeuralNet;
import neuralnetwork.Neuron;

/**
 *
 * @author tm
 */
public class WeightsFileUtil {

    public static double[] readWeights(NeuralNet net, String fileName) throws IOException {
        WeightsFileReader wFiler = null;
        int nIn = net.getLayer(0).size();
        int nHidd = net.getLayer(1).size();
        int nOut = net.getLayer(2).size();
        int weightsCount = nIn*(nHidd-1)+nHidd*nOut;
        double dataread[] = null;

        try {
            wFiler = new WeightsFileReader(fileName);
                dataread = new double[wFiler.getCount()];
                if (dataread.length == weightsCount)
                    dataread = wFiler.readData(dataread.length);
                else {
                    System.out.println("Plik wag nie zgadza się z architekturą sieci.");
                    System.exit(0);
                }
        } catch (IOException ex) {
            System.err.println("Blad I/O pliku: " + ex);
        }
        finally {
            wFiler.close();
        }
        return dataread;
    }

    public static void writeWeights(NeuralNet net, String algorithm) throws IOException {
         int w = -1;
         WeightsFileWriter wFile = null;
         int nIn = net.getLayer(0).size();
         int nHidd = net.getLayer(1).size();
         int nOut = net.getLayer(2).size();
         try {

             double data[] = new double[nIn*(nHidd-1)+nHidd*nOut];
             wFile = new WeightsFileWriter("weights/"+ algorithm + "_" + (nIn-1) + "x" + (nHidd-1)
                     + "x" + nOut + "_" + getDateTime() + ".dat", data.length);
             for (int i=1; i<net.getLayers().size();i++) {
                    for (int j=0;j<net.getLayer(i).size();j++) {
                        Neuron neuron = net.getLayer(i).getNeuron(j);
                        for (int k=0;k<neuron.getIncomingSyn().size();k++)
                           data[++w] = neuron.getIncomingSyn().get(k).getValue();
                    }
             }
             wFile.writeData(data);
        } catch (IOException ex) {
            System.err.println("Blad I/O pliku: " + ex);
        } finally {
           wFile.close();
        }
    }

    private static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy_HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }

}
