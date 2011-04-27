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

package util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * 
 * @author Glowczynski Tomasz
 */
public class WeightsFileReader extends DataInputStream {

    private int count;
    private int header;

    public WeightsFileReader(String filename) throws IOException {

        super(new BufferedInputStream(
                new GZIPInputStream(
                  new FileInputStream(filename)
              ))
        );
        if (readInt()!=2222) {
              System.err.println("To nie jest poprawny plik z wagami sieci");
              System.exit(0);
        }
        count = readInt();
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
