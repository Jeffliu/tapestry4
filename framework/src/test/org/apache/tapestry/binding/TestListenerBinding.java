// Copyright 2004 The Apache Software Foundation
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

package org.apache.tapestry.binding;

import org.apache.tapestry.IBinding;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.coerce.ValueConverter;

/**
 * Tests for {@link org.apache.tapestry.binding.ListenerBinding}, the binding type used for
 * supplying a listener method as a BSF script.
 * <p>
 * TODO: Do more, real testing of the execution of the script. Currently, that is done in one of
 * the integration tests.
 * 
 * @author Howard M. Lewis Ship
 * @since 3.1
 */
public class TestListenerBinding extends BindingTestCase
{
    /** @since 3.0 * */

    public void testListenerBindingObject()
    {
        ValueConverter vc = newValueConverter();

        IComponent c = newComponent();

        replayControls();

        IBinding b = new ListenerBinding(c, "foo", "", "", vc, null);

        assertSame(b, b.getObject());

        verifyControls();
    }
}