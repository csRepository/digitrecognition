package database;

import java.io.*;

public class MNISTImageFile extends RandomAccessFile {
  public int count;
  public int rows;
  public int cols;
  public int curr;
  public String fn;
  public MNISTImageFile(String fn, String mode) throws IOException, FileNotFoundException {
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
  public int cols() { return cols; }
  public int rows() { return rows;}
}



