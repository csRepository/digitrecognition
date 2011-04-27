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

import model.AbstractModel;
import view.AbstractView;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 *
 * @author tm
 */
public class AbstractController implements PropertyChangeListener{

    private ArrayList<AbstractView> registeredViews;
    private ArrayList<AbstractModel> registeredModels;

    public AbstractController() {
        this.registeredModels = new ArrayList<AbstractModel>();
        this.registeredViews = new ArrayList<AbstractView>();
    }

    public void addModel(AbstractModel model) {
        registeredModels.add(model);
        model.addPropertyChangeListner(this);
        System.out.println(registeredModels.size());
    }

    public void addView(AbstractView view) {
        registeredViews.add(view);
    }

    public void removeModel(AbstractModel model) {
        registeredModels.remove(model);
    }

    public void removeView(AbstractView view) {
        registeredViews.remove(view);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        for (AbstractView view:registeredViews) {
            view.modelPropertyChange(evt);
        }
    }

    protected void setModelProperty(String propertyName, Object newValue) {
        for (AbstractModel model:registeredModels) {
            try {
                Method method = model.getClass().getMethod("set" + propertyName,
                        new Class[]{newValue.getClass()});
                method.invoke(model, newValue);
            } catch (Exception ex) {
            }
        }
    }

}
