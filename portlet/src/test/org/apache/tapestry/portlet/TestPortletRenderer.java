// Copyright 2005 The Apache Software Foundation
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

package org.apache.tapestry.portlet;

import org.apache.hivemind.test.HiveMindTestCase;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.request.ResponseOutputStream;
import org.easymock.MockControl;

/**
 * Tests for {@link org.apache.tapestry.portlet.PortletRendererImpl}.
 * 
 * @author Howard M. Lewis Ship
 * @since 3.1
 */
public class TestPortletRenderer extends HiveMindTestCase
{
    private IMarkupWriter newWriter()
    {
        return (IMarkupWriter) newMock(IMarkupWriter.class);
    }

    private IPage newPage(IMarkupWriter writer, ResponseOutputStream output)
    {
        MockControl control = newControl(IPage.class);
        IPage page = (IPage) control.getMock();

        page.getResponseWriter(output);
        control.setReturnValue(writer);

        return page;
    }

    private IRequestCycle newCycle(String pageName, IPage page)
    {
        MockControl control = newControl(IRequestCycle.class);
        IRequestCycle cycle = (IRequestCycle) control.getMock();

        cycle.activate(pageName);
        
        cycle.getPage();
        control.setReturnValue(page);

        return cycle;
    }

    public void testSuccess()
    {
        ResponseOutputStream output = new ResponseOutputStream(null);
        IMarkupWriter writer = newWriter();
        IPage page = newPage(writer, output);
        IRequestCycle cycle = newCycle("ZePage", page);

        cycle.renderPage(writer);
        
        writer.close();

        replayControls();

        PortletRenderer r = new PortletRendererImpl();

        r.renderPage(cycle, "ZePage", output);

        verifyControls();
    }
}