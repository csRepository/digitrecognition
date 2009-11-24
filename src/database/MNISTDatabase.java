package database;

import java.io.*;


public class MNISTDatabase  {

    private static MNISTImageFile trainImgF,testImgF;
    private static MNISTLabelFile trainLblF,testLblF;
    public String trainImgFPath = "digitDatabase/train-images-idx3-ubyte";
    public String trainLblFPath = "digitDatabase/train-labels-idx1-ubyte";
    public String testImgFPath = "digitDatabase/t10k-images-idx3-ubyte";
    public String testLblFPath = "digitDatabase/t10k-labels-idx1-ubyte";

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
            if (kind.equals("train"))
                imgFile = trainImgF;
             else
                imgFile = testImgF;
                imgFile.setCurr(nr);      //obraz
                return imgFile.readData();
        }
         public int readLabel(String kind,int nr) {
            MNISTLabelFile lblFile;
            if (kind.equals("train"))
                lblFile = trainLblF;
             else
                lblFile = testLblF;
                lblFile.setCurr(nr);      //etykieta
                return lblFile.readData();
        }
}





