package util;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author tm
 */
public class WeightsFileWriter extends DataOutputStream {

    String fileName;

    public WeightsFileWriter(String filename, int networkSize) throws IOException {
        super(new BufferedOutputStream(
                  new FileOutputStream(filename)
              )
        );

            writeInt(2222);
            writeInt(networkSize);
    }

    public void writeData(double data[]) {
        try {
            for (int i = 0; i < data.length; i++)
                    writeDouble(data[i]);
        }
        catch (IOException ex) {
            System.err.print(ex);
            System.exit(0);
        }
    }
}
