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

public class MNISTLabelsFile extends RandomAccessFile {
  private int count;
  private int curr;
  private String fileName;

  public MNISTLabelsFile(String fileName, String mode) throws IOException, FileNotFoundException {
    super(fileName,mode);
    this.fileName = fileName;
    if (this.readInt()!=2049) {
      System.err.println("MNIST Label Files must have magic number of 2049.");
      System.exit(0);
    }
    curr=0;
    count=this.readInt();
  }

  public String status() {
    return curr() + "/" + count;
  }

  public int curr() { return curr;}

  public int readData() {
    int dat=0;
    try {
      dat=readUnsignedByte();
    } catch (IOException e) { 
      System.err.println(e);
    }
    setCurr(curr);
    return dat;
  }

  public void setCurr(int curr) {
    try {
      if (curr>0 && curr<=count) {
	this.curr=curr;
	seek(8+curr-1);
      } else {
	System.err.println(curr + " is not in the range 0 to " + count);
	System.exit(0);
      }
    } catch (IOException e) { 
      System.err.println(e);
    } 
  }

  public String name() { return fileName;}
}



