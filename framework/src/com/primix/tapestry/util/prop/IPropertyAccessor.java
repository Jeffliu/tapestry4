package com.primix.foundation.prop;

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
 *  Defines the methods by which a {@link PropertyHelper} can access
 *  the properties of the class it provides property access to.
 *
 *  @author Howard Ship
 *  @version $Id$
 */


public interface IPropertyAccessor
{

	/**
	*  Returns the current value of the property in the instance.
	*
	*/

	public Object get(Object instance);

	/**
	*  Returns the type of the property.
	*
	*/

	public Class getType();

	/**
	*  Returns true if a method exists to read the current value
	*  of the property.
	*
	*/

	public boolean isReadable();

	/**
	*  Returns true if the property is readable and writable: it contains both
	*  accessor and mutator methods.
	*
	*/

	public boolean isReadWrite();

	/**
	*  Returns true if a method exists to update the current value
	*  of the property.
	*
	*/

	public boolean isWritable();

	/**
	*  Sets the value of the property in the instance.
	*
	*/

	public void set(Object instance, Object value);
}


