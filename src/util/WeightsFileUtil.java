package util;

import java.io.File;
import java.io.IOException;

/**
 * Tool class for writing and reading weights.
 * @author Glowczynski Tomasz
 */
public final class WeightsFileUtil {

    private WeightsFileUtil() {}

    public static double[] readWeights(double networkSize, String fileName) {
        WeightsFileReader wFile = null;
        double dataread[] = null;
        try {
            wFile = new WeightsFileReader(fileName);
            try {
                dataread = new double[wFile.getCount()];
                if (dataread.length == networkSize)
                    dataread = wFile.readData(dataread.length);
                else {
                    System.out.println("Plik wag nie zgadza się z architekturą sieci.");
                    System.exit(0);
                }
            }
            finally {
                wFile.close();
            }
        } catch (IOException ex) {
            System.err.println("Blad I/O pliku: " + ex);
        }
        return dataread;
    }

//    public static void writeWeights(NeuralNet net, String fileName) {
//         int w = -1;
//         WeightsFileWriter wFile = null;
//         int nIn = net.getLayer(0).size();
//         int nHidd = net.getLayer(1).size();
//         int nOut = net.getLayer(2).size();
//         int weightsCount = nIn * (nHidd - 1) + nHidd * nOut;
//         try {
//             double data[] = new double[weightsCount];
//             File file = new File(fileName);
//             file = file.getParentFile();
//             file.mkdirs();
//                 wFile = new WeightsFileWriter(fileName, weightsCount);
//                 for (int i=1; i<net.getLayers().size();i++) {
//                        for (int j=0;j<net.getLayer(i).size();j++) {
//                            Neuron neuron = net.getLayer(i).getNeuron(j);
//                            for (int k=0;k<neuron.getIncomingSyn().size();k++)
//                               data[++w] = neuron.getIncomingSyn().get(k).getValue();
//                        }
//                 }
//                 wFile.writeData(data);
//        } catch (IOException ex) {
//            System.err.println("Blad I/O pliku: " + ex);
//        } finally {
//            try {
//                wFile.close();
//            } catch (IOException ex) {
//                Logger.getLogger(WeightsFileUtil.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    }
    public static void writeWeights(double data[], String fileName) {
         WeightsFileWriter wFile = null;
         try {
             File file = new File(fileName);
             try {   
                file = file.getParentFile();
                file.mkdirs();
                wFile = new WeightsFileWriter(fileName, data.length);
                wFile.writeData(data);
             }
             finally {
                 wFile.close();
             }
        } catch (IOException ex) {
            System.err.println("I/O error: " + ex);
        }
    }
}
