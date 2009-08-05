package database;

//------------------------------------------------------------
//   File: MNISTtoPPM.java
//   Written for JDK 1.1 API 
//   Author: Douglas Eck  deck@unm.edu
//   Description:
//   This is a simple program which takes an MNIST image file, 
//   an MNIST label file, and a desired image number and 
//   prints that image to stdout as a ppm file. It also prints
//   the number that the file is supposed to represent to 
//   stderr.
//------------------------------------------------------------

import java.io.*;


public class MNISTDatabase  {

    private static MNISTImageFile trainImgF,testImgF;
    private static MNISTLabelFile trainLblF,testLblF;
    private String trainImgFPath = "../digitDatabase/train-images-idx3-ubyte";
    private String trainLblFPath = "../digitDatabase/train-labels-idx1-ubyte";
    private String testImgFPath = "../digitDatabase/t10k-images-idx3-ubyte";
    private String testLblFPath = "../digitDatabase/t10k-labels-idx1-ubyte";

  public MNISTDatabase() {  
      try {
	trainImgF = new MNISTImageFile(trainImgFPath,"r");
	trainLblF = new MNISTLabelFile(trainLblFPath,"r");
        testImgF = new MNISTImageFile(testImgFPath,"r");
	testLblF = new MNISTLabelFile(testLblFPath,"r");
      } catch (FileNotFoundException e) { 
	System.err.println("File not found: " + e);
	System.exit(0);
      } catch (IOException e) {
	System.err.println("IO Exception: " + e);
	System.exit(0);
      }
    }
          public int[][] readImage(String kind,int nr) {
            MNISTImageFile imgFile;
            if (kind.equals("training"))
                imgFile = trainImgF;
             else
                imgFile = testImgF;
                imgFile.setCurr(nr);      //obraz
                return imgFile.readData();
        }
         public int readLabel(String kind,int nr) {
            MNISTLabelFile lblFile;
            if (kind.equals("training"))
                lblFile = trainLblF;
             else
                lblFile = testLblF;
                lblFile.setCurr(nr);      //etykieta
                System.out.println();
                return lblFile.readData();
        }
}





