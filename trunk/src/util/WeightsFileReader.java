/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 *
 * @author tm
 */
public class WeightsFileReader extends DataInputStream {

    String fileName;
    int count;
    int header;


    // TODO zmienic na tablice parametrow
    public WeightsFileReader(String filename) throws IOException {

        super(new BufferedInputStream(
                  new FileInputStream(filename)
              )
        );
        if (readInt()!=2222) {
              System.err.println("To nie jest poprawny plik z wagami sieci");
              System.exit(0);
        }
        this.count = readInt();
    }

    public double[] readData(int x) {
        double[] data = new double[x];
        try {
            for (int i = 0; i < x; i++)
                    data[i] = readDouble();
        }
        catch (IOException ex) {
            System.err.print(ex);
            System.exit(0);
        }
        return data;

    }

    public int getCount() {
        return count;
    }

    public int getHeader() {
        return header;
    }


}
