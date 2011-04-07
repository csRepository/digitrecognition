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

package model.database;

import java.io.*;

public class MNISTImagesFile extends RandomAccessFile {

  private int count;
  private int rows;
  private int cols;
  private int curr;
  private String fn;

  /**
   * MNISTImageFile is a subclass of RandomAccesFile so we need a path to file
   * and operation type (read/write)
   * @param fn file name
   * @param mode operation type /r/w/rw
   * @throws IOException
   * @throws FileNotFoundException
   */
  public MNISTImagesFile(String fn, String mode) throws IOException, FileNotFoundException {
    super(fn,mode);
    this.fn = fn;
    if (readInt()!=2051) {
      System.err.println("MNIST Image Files must have magic number of 2051.");
      System.exit(0);
    }
    curr=0;
    count=readInt();
    rows=readInt();
    cols=readInt();
  }


  public String status() {
    return curr() + "/" + count;
  }

  public int curr() { return curr; }

  public int [][] readData() {
    int [][] dat = new int[rows][cols];
    try {
      for (int i=0;i<cols;i++)
	for (int j=0;j<rows;j++)
	  dat[i][j]=readUnsignedByte();
    } catch (IOException e) { 
      System.err.println(e);
    }
    setCurr(curr());
    return dat;
  }

  public void setCurr(int curr) {
    try {
      if (curr>0 && curr<=count) {
	seek(16+(rows*cols*(curr-1)));
	this.curr=curr;
      }
    } catch (IOException e) { 
      System.err.println(e);
    } 
  }
  public String name() { return fn;}

  /**
   * Get image width
   * @return int
   */
  public int getCols() { return cols; }

  /**
   * Get image height
   * @return int
   */
  public int getRows() { return rows;}

  /**
   * Get images count in MNIST file
   * @return
   */
  public int getImagesCount() {
      return count;
  }

}



