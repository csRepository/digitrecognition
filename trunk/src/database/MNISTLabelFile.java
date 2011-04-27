package database;

import java.io.*;

public class MNISTLabelFile extends RandomAccessFile {
  private int count;
  private int curr;
  private String fileName;

  public MNISTLabelFile(String fileName, String mode) throws IOException, FileNotFoundException {
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



