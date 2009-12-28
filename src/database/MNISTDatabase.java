package database;

import java.io.*;

/**
 * Class with methods for handling reading images/labels from MNIST data files.
 * @author tm
 */
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
            MNISTImageFile imgFile = null;
            if (kind.equals("train"))
                imgFile = trainImgF;
             else if (kind.equals("test"))
                imgFile = testImgF;
             else {
                 System.out.println("Brak zbioru " + "'" + kind + "'" + " w bazie");
                 System.exit(1);
             }
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





