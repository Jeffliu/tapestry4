package com.primix.tapestry;

/*
 * Tapestry Web Application Framework
 * Copyright (c) 2000 by Howard Ship and Primix Solutions
 *
 * Primix Solutions
 * One Arsenal Marketplace
 * Watertown, MA 02472
 * http://www.primix.com
 * mailto:hship@primix.com
 * 
 * This library is free software.
 * 
 * You may redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation.
 *
 * Version 2.1 of the license should be included with this distribution in
 * the file LICENSE, as well as License.html. If the license is not
 * included with this distribution, you may find a copy at the FSF web
 * site at 'www.gnu.org' or 'www.fsf.org', or you may write to the
 * Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139 USA.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 */


/**
 *  A binding is the mechanism used to provide values for parameters of
 *  specific {@link IComponent} instances. The component doesn't
 *  care where the required value comes from, it simply requires that
 *  a value be provided when needed.
 *
 * <p>Bindings are set inside the containing component's specification.
 * Bindings may be static or dynamic (though that is irrelevant to the
 * component).  Components may also use a binding to write a value
 * back through a property to some other object (typically, another component).
 *
 * @author Howard Ship
 * @version $Id$
 */
 
public interface IBinding
{
    /**
     *  Invokes {@link #getValue()}, then coerces the value to a boolean.  
     *  The following rules are used to perform the coercion:
     *  <ul>
     *  <li>null is always false
     *  <li>A <code>Boolean</code> value is self-evident
     *  <li>A <code>Number</code> value is true if non-zero
     *  <li>A <code>String</code> value is true if non-empty and contains
     *  non-whitespace characters.
     *  <li>Any array type is true if it has a non-zero length
     *  <li>Any other non-null value is true
     *  </ul>
     */
 
    public boolean getBoolean();

    /**
     *  Gets the value of the Binding using {@link #getValue} and coerces it
     *  to an <code>int</code>.  Strings will be parsed, and other
     *  <code>java.lang.Number</code> classes will have <code>intValue()</code>
     *  invoked.  
     *
     *  @throws ClassCastException if the binding's value is not of a usable class.
     *  @throws NullValueForBindingException if the binding's value is null.
     */
 
    public int getInt();

    /**
     *  Gets the value of the Binding using {@link #getValue()} and coerces it
     *  to a <code>double</code>.  Strings will be parsed, and other
     *  <code>java.lang.Number</code> classes will have <code>doubleValue()</code>
     *  invoked.
     *
     *  @throws ClassCastException if the binding's value is not of a usable class.
     *  @throws NullValueForBindingException if the binding's value is null.
     */

    public double getDouble();

    /**
     *  Invokes {@link #getValue()} and converts the result to <code>java.lang.String</code>.
     */
 
    public String getString();

    /**
     *  Returns the value of the Binding.  This is the essential method.  Other methods
     *  get this value and cast or coerce the value.
     *
     */
 
    public Object getValue();

    /**
     *  Returns true if the value is statically defined.  Static values
     *  are read-only.
     *
     */
 
    public boolean isStatic();

    /**
     *  Constructs a <code>Boolean</code> and invokes {@link #setValue(Object)}.
     *
     */
 
    public void setBoolean(boolean value);

    /**
     *  Constructs an <code>Integer</code> and invokes {@link #setValue(Object)}.
     *
     */

    public void setInt(int value);

    /**
     *  Constructs an <code>Double</code> and invokes {@link #setValue(Object)}.
     *
     */

    public void setDouble(double value);

    /**
     *  Invokes {@link #setValue(Object)}.
     *
     */

    public void setString(String value);

    /**
     *  Updates the value of the binding, if possible.
     *
     *  @exception ReadOnlyBindingException If the binding is read only.
     *
     */
 
    public void setValue(Object value);
}