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

package model;


import model.database.MNISTDatabase;
import controller.DefaultController;
import java.awt.Image;

/**
 *
 * @author tm
 */
public class DatabaseModel extends AbstractModel{

    private MNISTDatabase dataModel;
    private Integer trainImgNumber;
    private Integer testImgNumber;
    private Image trainImage;
    private Image testImage;

    public DatabaseModel(MNISTDatabase dataModel) {
        this.dataModel = dataModel;
        this.trainImgNumber = 0;
        this.testImgNumber = 0;
    }

    public void setDatabaseTrainImgPath(String path) {
        String oldPath = dataModel.getTrainImgFPath();
        dataModel.setTrainImgFPath(path);
        firePropertyChange(DefaultController.TRAIN_IMG_FILE_PATH, oldPath, path);
    }
    
    public void setDatabaseTrainLblPath(String path) {
        dataModel.setTrainLblFPath(path);
    }

    public void setDatabaseTestImgPath(String path) {
        dataModel.setTestImgFPath(path);
    }

    public void setDatabaseTestLblPath(String path) {
        dataModel.setTestLblFPath(path);
    }

     public void setDatabaseTestImgNumber(Integer newValue) {
         Integer oldValue = this.testImgNumber;
         this.testImgNumber = newValue;
         firePropertyChange(DefaultController.TEST_IMG_NUMBER, oldValue, newValue);

    }

    public void setDatabaseTrainImgNumber(Integer newValue) {
         Integer oldValue = this.trainImgNumber;
         this.trainImgNumber = newValue;
         firePropertyChange(DefaultController.TRAIN_IMG_NUMBER, oldValue, newValue);

    }

    public Image getTrainImage() {
        return trainImage;
    }

    public void setTrainImage(Image image) {
        this.trainImage = image;
    }

 


}
