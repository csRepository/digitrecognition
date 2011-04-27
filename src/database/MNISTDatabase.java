package database;

import java.io.*;
import util.*;

/**
 * Class with methods for handling reading images/labels from MNIST data files.
 */
public class MNISTDatabase  {

    private static MNISTImageFile trainImgF,testImgF;
    private static MNISTLabelFile trainLblF,testLblF;
    private String trainImgFPath = GPathReader.getTrainImagesPath();
    private String trainLblFPath = GPathReader.getTrainLabelsPath();
    private String testImgFPath  = GPathReader.getTestImagesPath();
    private String testLblFPath  = GPathReader.getTestLabelsPath();

  private static final MNISTDatabase INSTANCE = new MNISTDatabase();

  private MNISTDatabase() {
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

  public static MNISTDatabase getInstance() {
      return INSTANCE;
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
        /**
     * @return the trainImgFPath
     */
    public String getTrainImgFPath() {
        return trainImgFPath;
    }

    /**
     * @return the trainLblFPath
     */
    public String getTrainLblFPath() {
        return trainLblFPath;
    }

    /**
     * @return the testImgFPath
     */
    public String getTestImgFPath() {
        return testImgFPath;
    }

    /**
     * @return the testLblFPath
     */
    public String getTestLblFPath() {
        return testLblFPath;
    }
}





