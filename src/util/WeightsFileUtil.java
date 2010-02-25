/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import neuralnetwork.NeuralNet;
import neuralnetwork.Neuron;

/**
 * Tool class for writing and reading weights.
 * @author Glowczynski Tomasz
 */
public final class WeightsFileUtil {

    public static double[] readWeights(NeuralNet net, String fileName) throws IOException {
        WeightsFileReader wFile = null;
        int nIn = net.getLayer(0).size();
        int nHidd = net.getLayer(1).size();
        int nOut = net.getLayer(2).size();
        int weightsCount = nIn * (nHidd - 1) + nHidd * nOut;
        double dataread[] = null;

        try {
            wFile = new WeightsFileReader(fileName);
                dataread = new double[wFile.getCount()];
                if (dataread.length == weightsCount)
                    dataread = wFile.readData(dataread.length);
                else {
                    System.out.println("Plik wag nie zgadza się z architekturą sieci.");
                    System.exit(0);
                }
        } catch (IOException ex) {
            System.err.println("Blad I/O pliku: " + ex);
        }
        finally {
            wFile.close();
        }
        return dataread;
    }

    public static void writeWeights(NeuralNet net, String fileName) {
         int w = -1;
         WeightsFileWriter wFile = null;
         int nIn = net.getLayer(0).size();
         int nHidd = net.getLayer(1).size();
         int nOut = net.getLayer(2).size();
         int weightsCount = nIn * (nHidd - 1) + nHidd * nOut;
         try {
             double data[] = new double[weightsCount];
             File file = new File(fileName);
             file = file.getParentFile();
             file.mkdirs();
                 wFile = new WeightsFileWriter(fileName, weightsCount);
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
            try {
                wFile.close();
            } catch (IOException ex) {
                Logger.getLogger(WeightsFileUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public static void writeWeights(double data[], String fileName) {
         int w = -1;
         WeightsFileWriter wFile = null;
         try {
             File file = new File(fileName);
             file = file.getParentFile();
             file.mkdirs();
                 wFile = new WeightsFileWriter(fileName, data.length);
                 wFile.writeData(data);
        } catch (IOException ex) {
            System.err.println("Blad I/O pliku: " + ex);
        } finally {
            try {
                wFile.close();
            } catch (IOException ex) {
                Logger.getLogger(WeightsFileUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    private static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy_HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }

}
