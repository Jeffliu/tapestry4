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

package org.apache.tapestry;

import org.apache.tapestry.form.FormEventType;
import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.valid.IValidationDelegate;

/**
 *  A generic way to access a component which defines an HTML form.  This interface
 *  exists so that the {@link IRequestCycle} can invoke the
 *  {@link #rewind(IMarkupWriter, IRequestCycle)} method (which is used to deal with
 *  a Form that uses the direct service).  In release 1.0.5, more responsibility
 *  for forms was moved here.
 *
 *  @author Howard Lewis Ship
 *  @version $Id$
 *  @since 1.0.2
 **/

public interface IForm extends IAction
{

    /**
     *  Attribute name used with the request cycle; allows other components to locate
     *  the IForm.
     *
     *  @since 1.0.5
     * 
     **/

    public static final String ATTRIBUTE_NAME = "org.apache.tapestry.active.Form";

    /**
     *  Invoked by the {@link IRequestCycle} to allow a form that uses
     *  the direct service, to respond to the form submission.
     *
     **/

    public void rewind(IMarkupWriter writer, IRequestCycle cycle);

    /**
     *  Adds an additional event handler.  The type determines when the
     *  handler will be invoked, {@link FormEventType#SUBMIT}
     *  is most typical.
     *
     * @since 1.0.5
     * 
     **/

    public void addEventHandler(FormEventType type, String functionName);

    /**
     *  Constructs a unique identifier (within the Form).  The identifier
     *  consists of the component's id, with an index number added to
     *  ensure uniqueness.
     *
     *  <p>Simply invokes {@link #getElementId(IFormComponent, String)} with the component's id.
     *
     *
     *  @since 1.0.5
     * 
     **/

    public String getElementId(IFormComponent component);

    /**
     *  Constructs a unique identifier from the base id.  If possible, the
     *  id is used as-is.  Otherwise, a unique identifier is appended
     *  to the id.
     *
     *  <p>This method is provided simply so that some components
     *  ({@link org.apache.tapestry.form.ImageSubmit}) have more specific control over
     *  their names.
     * 
     *  <p>Invokes {@link IFormComponent#setName(String)} with the result, as well
     *  as returning it.
     * 
     *  @throws StaleLinkException if, when the form itself is rewinding, the
     *  element id allocated does not match the expected id (as allocated when the form rendered).
     *  This indicates that the state of the application has changed between the time the
     *  form renderred and the time it was submitted.
     *
     *  @since 1.0.5
     *
     **/

    public String getElementId(IFormComponent component, String baseId);

    /**
     * Returns the name of the form.
     *
     *  @since 1.0.5
     *
     **/

    public String getName();

    /**
     *  Returns true if the form is rewinding (meaning, the form was the subject
     *  of the request cycle).
     *
     *  @since 1.0.5
     *
     **/

    public boolean isRewinding();

    /**
     *  Returns the validation delegate for the form.
     *  Returns null if the form does not have a delegate.
     * 
     *  @since 1.0.8
     * 
     **/

    public IValidationDelegate getDelegate();
    
    /**
     *  May be invoked by a component to force the encoding type of the
     *  form to a particular value.
     * 
     *  @see org.apache.tapestry.form.Upload
     *  @throws ApplicationRuntimeException if the current encoding type is not null
     *  and doesn't match the suggested encoding type
     *  @since 3.0
     * 
     **/
    
    public void setEncodingType(String encodingType);
    
    
    /**
     * Adds a hidden field value to be stored in the form. This ensures that all
     * of the &lt;input type="hidden"&gt; (or equivalent) are grouped together, 
     * which ensures that the output HTML is valid (ie. doesn't 
     * have &lt;input&gt; improperly nested with &lt;tr&gt;, etc.).
     * 
     * <p>
     * It is acceptible to add multiple hidden fields with the same name.
     * They will be written in the order they are received.
     * 
     * @since 3.0
     */

	public void addHiddenValue(String name, String value);
}