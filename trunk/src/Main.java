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


import model.database.MNISTDatabase;
import controller.DatabaseTabController;
import controller.DefaultController;
import controller.MainViewConnector;
import model.DatabaseModel;
import view.DatabaseTabView;
import view.MainFrame;
import view.TestTabView;
import view.TrainTabView;
import util.NeuralUtil;



/**
 *
 * @author tm
 */
public class Main {

    protected static MainFrame mainFrame;

    public static void main(String[] args) {

        NeuralUtil.readConfigFile(new String[] {"parameters.xml"});
        MNISTDatabase dataMNIST = MNISTDatabase.getInstance();
        DatabaseModel dm = new DatabaseModel(dataMNIST);

        DefaultController dc = new DefaultController();


//        try {
//            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
//        } catch (Exception ex) { }


        
        DatabaseTabController dtc = new DatabaseTabController(dataMNIST);

        //Runnable run = new Runnable() {

          //  @Override
            //public void run() {
                mainFrame = new MainFrame();
                mainFrame.setVisible(true);

            //}
        //};
        //EventQueue.invokeLater(run);

        MainViewConnector mvc = new MainViewConnector(mainFrame);

        final DatabaseTabView dataTabView = new DatabaseTabView(dtc, mvc);
        final TrainTabView trainTabView = new TrainTabView();
        final TestTabView testTabView = new TestTabView();

        mainFrame.addTab("Database", dataTabView);
        mainFrame.addTab("Train network", trainTabView);
        mainFrame.addTab("Test network", testTabView);

        dc.addModel(dm);
        dc.addView(mainFrame);

    }
}
