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

package org.apache.tapestry.spec;

import java.util.Collection;

import org.apache.hivemind.Locatable;
import org.apache.hivemind.LocationHolder;
import org.apache.tapestry.util.IPropertyHolder;

/**
 * Defines a contained component.  This includes the information needed to
 * get the contained component's specification, as well as any bindings
 * for the component.

 * @author glongman@intelligentworks.com
 * @version $Id$
 */
public interface IContainedComponent extends IPropertyHolder, LocationHolder, Locatable
{
    /**
     *  Returns the named binding, or null if the binding does not
     *  exist.
     *
     **/
    public abstract IBindingSpecification getBinding(String name);
    /**
     *  Returns an umodifiable <code>Collection</code>
     *  of Strings, each the name of one binding
     *  for the component.
     *
     **/
    public abstract Collection getBindingNames();
    public abstract String getType();
    public abstract void setBinding(String name, IBindingSpecification spec);
    public abstract void setType(String value);
    /**
     * 	Sets the String Id of the component being copied from.
     *  For use by IDE tools like Spindle.
     * 
     *  @since 1.0.9
     **/
    public abstract void setCopyOf(String id);
    /**
     * 	Returns the id of the component being copied from.
     *  For use by IDE tools like Spindle.
     * 
     *  @since 1.0.9
     **/
    public abstract String getCopyOf();

    /**
     * Returns whether the contained component will inherit 
     * the informal parameters of its parent. 
     * 
     * @since 3.0
     **/
    public abstract boolean getInheritInformalParameters();

    /**
     * Sets whether the contained component will inherit 
     * the informal parameters of its parent. 
     * 
     * @since 3.0
     */
    public abstract void setInheritInformalParameters(boolean value);
}