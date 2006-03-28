// Copyright 2004, 2005 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package org.apache.tapestry.timetracker.page;

import java.util.Date;
import java.util.List;

import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.form.BeanPropertySelectionModel;
import org.apache.tapestry.form.DatePicker;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.form.PropertySelection;
import org.apache.tapestry.html.BasePage;
import org.apache.tapestry.timetracker.dao.ProjectDao;
import org.apache.tapestry.timetracker.model.Project;


/**
 * Manages entering tasks.
 *
 * @author jkuhnert
 */
public abstract class TaskEntryPage extends BasePage
{
    
    @Component(type = "PropertySelection", id = "projectChoose",
            bindings = { "model=projectModel", "value=selectedProject",
            "displayName=message:choose.project"})
    public abstract PropertySelection getProjectSelection();
    
    @InjectObject("service:timetracker.dao.ProjectDao")
    public abstract ProjectDao getProjectDao();
    
    public abstract Project getSelectedProject();
    
    @Component(type = "DatePicker", id = "startPicker",
            bindings = {"value=startTime"})
    public abstract DatePicker getStartPicker();
    
    public abstract Date getStartTime();
    
    /**
     * Selection model for projects.
     * @return
     */
    public IPropertySelectionModel getProjectModel()
    {
        List<Project> projects = getProjectDao().listProjects();
        projects.add(0, new Project(-1, "Choose.."));
        return new BeanPropertySelectionModel(projects, "name");
    }
}