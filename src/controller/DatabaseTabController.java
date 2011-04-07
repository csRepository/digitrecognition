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

package controller;

import model.DatabaseModel;
import model.database.MNISTDatabase;
import view.AbstractView;
import view.DatabaseTabView;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.lang.reflect.Array;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import util.NeuralUtil;

/**
 *
 * @author tm
 */
public class DatabaseTabController {

    MNISTDatabase model;
    DatabaseTabView view;

    public DatabaseTabController(MNISTDatabase model) {
        this.model = model;
//        this.view = view;
//        view.addTrainSpinChangeListener(new TrainSpinChangeListener());
//        view.addTestSpinChangeListener(new TestSpinChangeListener());
//        view.addReadTrainDataButtonListener(new ButtonActionListener());
//        view.addReadTestDataButtonListener(new ButtonActionListener());
    }



    public class TrainSpinChangeListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            System.out.println(e.getSource());
        }

    }

     public class TestSpinChangeListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            System.out.println(e.getSource());
        }

    }

    public class ButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals("train")) {

            } else if (command.equals("test")) {

            }
        }

    }

    /*TODO add detecting cpu cores ex. Runtime.getRuntime().availableProcessors()
     * and run threads to speed up images processing
     *
    */
    public void readData(String dataType) {
       
        ArrayList array = new ArrayList();
        int imagesCount;

        if (dataType.equals("train")) {
             model.initTrainData();
            imagesCount = MNISTDatabase.getTrainImgF().getImagesCount();
        }
        else {
            model.initTestData();
            imagesCount = MNISTDatabase.getTestImgF().getImagesCount();
        }

        NeuralUtil.setPatterns(array, imagesCount,1, imagesCount);

        double[][][] images = NeuralUtil.prepareInputSet(array, model,
                 dataType, "binarize", 0.0, 1.0);

        double [][] labels = NeuralUtil.prepareOutputSet(array, 10, model, dataType);

        if (dataType.equals("train")) {
            model.setTrainImages(images);
            model.setTrainLabels(labels);
        }
        else { 
            model.setTestImages(images);
            model.setTestLabels(labels);
        }
    }

    public void readTestData() {
        model.initTestData();
    }
    
    /**
     * Convert double[][][] array to Image type. Needed for displaying it in view
     * @param number image number
     */
    public Image readImage(String dataType, Integer number) {
         double[][] image = model.getImage(dataType, number);
          BufferedImage imageB = new BufferedImage(28, 28, BufferedImage.TYPE_INT_RGB);
             WritableRaster raster = imageB.getRaster();
         if (image!=null) {
            // byte[] byteImage = new byte[image.length * image.length];
             for (int i = 0; i < image.length; i++) {
                 for (int j = 0; j < image[i].length; j++) {
                    // byteImage[counter++] = (byte)image[i][j];
                     if (image[i][j]==1) image[i][j]=255;
                     int[] colorArray = getColorForPixel((int)image[i][j]);
                     raster.setPixel(i, j, colorArray);
                 }
            }
             BufferedImage bdest =
      new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
             Graphics2D g = bdest.createGraphics();
   AffineTransform at =
      AffineTransform.getScaleInstance((double)100/imageB.getWidth(),
          (double)100/imageB.getHeight());
   g.drawRenderedImage(imageB,at);
            //return Toolkit.getDefaultToolkit().createImage(byteImage);
            return bdest;
         }
         return null;
        //wczytac dane do byte[]
        //stworzyc z tego Image
        //setModelProperty -- ustawic powyzszy Image
    }

    private int[] getColorForPixel(int rgb) {
        Color color = new Color(rgb);
        int[] colors = {color.getRed(),color.getGreen(),color.getBlue()};
        return colors;
    }

    public void readTestImage(Integer number) {

    }

}