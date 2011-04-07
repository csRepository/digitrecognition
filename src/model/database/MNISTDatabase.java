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
import util.*;

/**
 * Class with methods for handling reading images/labels from MNIST data files.
 */
public class MNISTDatabase  {

    private static MNISTImagesFile trainImgF,testImgF;
    private static MNISTLabelsFile trainLblF,testLblF;
    private String trainImgFPath = GPathReader.getTrainImagesPath();
    private String trainLblFPath = GPathReader.getTrainLabelsPath();
    private String testImgFPath  = GPathReader.getTestImagesPath();
    private String testLblFPath  = GPathReader.getTestLabelsPath();

    private double[][][] trainImages, testImages;
    private double[][] trainLabels, testLabels;

  private static final MNISTDatabase INSTANCE = new MNISTDatabase();

      private MNISTDatabase() {}

      public static MNISTDatabase getInstance() {
          return INSTANCE;
      }

      public void initTrainData() {
         try {
            trainImgF = new MNISTImagesFile(trainImgFPath,"r");
            trainLblF = new MNISTLabelsFile(trainLblFPath,"r");
          } catch (FileNotFoundException e) {
                System.err.println("File not found: " + e);
          } catch (IOException e) {
                System.err.println("IO Exception: " + e);
          }
      }

      public void initTestData() {
         try {
            testImgF = new MNISTImagesFile(testImgFPath,"r");
            testLblF = new MNISTLabelsFile(testLblFPath,"r");
          } catch (FileNotFoundException e) {
                System.err.println("File not found: " + e);
          } catch (IOException e) {
                System.err.println("IO Exception: " + e);
          }
      }


     public int[][] readImage(String type,int nr) {
        MNISTImagesFile imgFile = null;
        if (type.equals("train"))
            imgFile = trainImgF;
         else if (type.equals("test"))
            imgFile = testImgF;
         else {
             System.out.println("Brak zbioru " + "'" + type + "'" + " w bazie");
             System.exit(1);
         }
        imgFile.setCurr(nr);      //obraz
        return imgFile.readData();
    }

    /**
     * Gets image from array (iamges are stored in memory)
     * @param type type of image
     * @param nr image number
     * @return int[][]
     */
    public double[][] getImage(String type, int nr) {
         if (type.equals("train")) {
             return trainImages[nr];
         }
         else if (type.equals("test")) {
             return testImages[nr];
         }
         return null;

    }

     public int readLabel(String type,int nr) {
        MNISTLabelsFile lblFile;
        if (type.equals("train"))
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

    public double[][][] getTestImages() {
        return testImages;
    }

    public void setTestImages(double[][][] testImages) {
        this.testImages = testImages;
    }

    public double[][] getTestLabels() {
        return testLabels;
    }

    public void setTestLabels(double[][] testLabels) {
        this.testLabels = testLabels;
    }

    public double[][][] getTrainImages() {
        return trainImages;
    }

    public void setTrainImages(double[][][] trainImages) {
        this.trainImages = trainImages;
    }

    public double[][] getTrainLabels() {
        return trainLabels;
    }

    public void setTrainLabels(double[][] trainLabels) {
        this.trainLabels = trainLabels;
    }

    /**
     * @return the testLblFPath
     */
    public String getTestLblFPath() {
        return testLblFPath;
    }

    public void setTestImgFPath(String testImgFPath) {
        this.testImgFPath = testImgFPath;
    }

    public void setTestLblFPath(String testLblFPath) {
        this.testLblFPath = testLblFPath;
    }

    public void setTrainImgFPath(String trainImgFPath) {
        this.trainImgFPath = trainImgFPath;
    }

    public void setTrainLblFPath(String trainLblFPath) {
        this.trainLblFPath = trainLblFPath;
    }

    public static MNISTImagesFile getTestImgF() {
        
        return testImgF;
    }

    public static MNISTLabelsFile getTestLblF() {
        return testLblF;
    }

    public static MNISTImagesFile getTrainImgF() {
        return trainImgF;
    }

    public static MNISTLabelsFile getTrainLblF() {
        return trainLblF;
    }




}