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

package org.apache.tapestry.enhance;

import java.lang.reflect.Modifier;
import java.util.Collections;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ErrorLog;
import org.apache.hivemind.Location;
import org.apache.hivemind.service.MethodSignature;
import org.apache.hivemind.test.HiveMindTestCase;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.spec.AssetSpecification;
import org.apache.tapestry.spec.IAssetSpecification;
import org.apache.tapestry.spec.IComponentSpecification;
import org.easymock.MockControl;

/**
 * Tests for {@link org.apache.tapestry.enhance.InjectAssetWorker}.
 * 
 * @author Howard M. Lewis Ship
 * @since 4.0
 */
public class TestInjectAssetWorker extends HiveMindTestCase
{
    private IComponentSpecification newSpec(String assetName, String propertyName, Location location)
    {
        IAssetSpecification as = new AssetSpecification();
        as.setPropertyName(propertyName);
        as.setLocation(location);

        MockControl control = newControl(IComponentSpecification.class);
        IComponentSpecification spec = (IComponentSpecification) control.getMock();

        spec.getAssetNames();
        control.setReturnValue(Collections.singletonList(assetName));

        spec.getAsset(assetName);
        control.setReturnValue(as);

        return spec;
    }

    public void testNoWork()
    {
        IComponentSpecification spec = newSpec("fred", null, null);
        EnhancementOperation op = (EnhancementOperation) newMock(EnhancementOperation.class);

        replayControls();

        new InjectAssetWorker().performEnhancement(op, spec);

        verifyControls();
    }

    public void testSuccess()
    {
        Location l = newLocation();
        IComponentSpecification spec = newSpec("fred", "barney", l);
        MockControl control = newControl(EnhancementOperation.class);
        EnhancementOperation op = (EnhancementOperation) control.getMock();

        op.getPropertyType("barney");
        control.setReturnValue(IAsset.class);

        op.claimProperty("barney");

        op.addField("_$barney", IAsset.class);

        op.getAccessorMethodName("barney");
        control.setReturnValue("getBarney");

        op.addMethod(
                Modifier.PUBLIC,
                new MethodSignature(IAsset.class, "getBarney", null, null),
                "return _$barney;",
                l);

        op.extendMethodImplementation(
                IComponent.class,
                EnhanceUtils.FINISH_LOAD_SIGNATURE,
                "_$barney = getAsset(\"fred\");");

        replayControls();

        new InjectAssetWorker().performEnhancement(op, spec);

        verifyControls();
    }

    public void testFailure()
    {
        Location l = newLocation();
        Throwable ex = new ApplicationRuntimeException(EnhanceMessages.claimedProperty("barney"));

        MockControl control = newControl(EnhancementOperation.class);
        EnhancementOperation op = (EnhancementOperation) control.getMock();

        IComponentSpecification spec = newSpec("fred", "barney", l);

        ErrorLog log = (ErrorLog) newMock(ErrorLog.class);

        op.getPropertyType("barney");
        control.setReturnValue(IComponent.class);

        op.claimProperty("barney");
        control.setThrowable(ex);

        op.getBaseClass();
        control.setReturnValue(BaseComponent.class);

        log.error(EnhanceMessages.errorAddingProperty("barney", BaseComponent.class, ex), l, ex);

        replayControls();

        InjectAssetWorker w = new InjectAssetWorker();

        w.setErrorLog(log);

        w.performEnhancement(op, spec);

        verifyControls();
    }

    public void testWrongPropertyType()
    {
        MockControl control = newControl(EnhancementOperation.class);
        EnhancementOperation op = (EnhancementOperation) control.getMock();

        op.getPropertyType("barney");
        control.setReturnValue(IComponent.class);

        op.claimProperty("barney");

        replayControls();

        InjectAssetWorker w = new InjectAssetWorker();
        try
        {
            w.injectAsset(op, "fred", "barney", null);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                    "Property barney is type org.apache.tapestry.IComponent, which is not compatible with the expected type, org.apache.tapestry.IAsset.",
                    ex.getMessage());
        }

        verifyControls();

    }
}