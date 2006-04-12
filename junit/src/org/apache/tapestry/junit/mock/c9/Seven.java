/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation", "Tapestry" 
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache" 
 *    or "Tapestry", nor may "Apache" or "Tapestry" appear in their 
 *    name, without prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE TAPESTRY CONTRIBUTOR COMMUNITY
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.tapestry.junit.mock.c9;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;

/**
 *  One giant test page to test all kinds of persistent properties.  Eight
 *  scalar types and an object type.
 *
 *  @author Howard Lewis Ship
 *  @version $Id$
 *  @since 3.0
 **/

public abstract class Seven extends BasePage
{
    abstract public boolean getBooleanValue();
    abstract public byte getByteValue();
    abstract public char getCharValue();
    abstract public double getDoubleValue();
    abstract public float getFloatValue();
    abstract public int getIntValue();
    abstract public long getLongValue();
    abstract public short getShortValue();
    abstract public String getStringValue();
    abstract public void setBooleanValue(boolean booleanValue);
    abstract public void setByteValue(byte byteValue);
    abstract public void setCharValue(char charValue);
    abstract public void setDoubleValue(double doubleValue);
    abstract public void setFloatValue(float floatValue);
    abstract public void setIntValue(int intValue);
    abstract public void setLongValue(long longValue);
    abstract public void setShortValue(short shortValue);
    abstract public void setStringValue(String stringValue);

    public void finishLoad()
    {
        setBooleanValue(true);
        setByteValue((byte) 'A');
        setShortValue((short) 97);
        setCharValue('Z');
        setDoubleValue(3.2);
        setFloatValue(-22.7f);
        setIntValue(100);
        setLongValue(32000000);
        setStringValue("Magic");
    }

    public void update(IRequestCycle cycle)
    {
        setBooleanValue(false);
        setByteValue((byte) 'Q');
        setShortValue((short) 21);
        setCharValue('f');
        setDoubleValue(9.87);
        setFloatValue(-202.2f);
        setIntValue(3097);
        setLongValue(132000001);
        setStringValue("Marker");
    }

}