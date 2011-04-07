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

/**
 *
 * @author tm
 */
public class DefaultController extends AbstractController {

    public static final String TRAIN_IMG_FILE_PATH = "DatabaseTrainImgPath";
    public static final String TRAIN_IMG_NUMBER = "DatabaseTrainImgNumber";
    public static final String TEST_IMG_NUMBER = "DatabaseTestImgNumber";

    public void setDatabaseTrainImgPath(String path) {
        setModelProperty(TRAIN_IMG_FILE_PATH, path);
    }

    public void setDatabaseTrainImgNumber(Integer newNumber) {
        setModelProperty(TRAIN_IMG_NUMBER, newNumber);
    }

    public void setDatabaseTestImgNumber(Integer newNumber) {
        setModelProperty(TEST_IMG_NUMBER, newNumber);
    }






}
