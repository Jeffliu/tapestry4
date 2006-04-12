package com.primix.vlib.pages;

import com.primix.tapestry.components.*;
import com.primix.tapestry.components.validating.*;
import com.primix.tapestry.*;
import com.primix.vlib.ejb.*;
import com.primix.vlib.*;
import javax.ejb.*;
import java.util.*;
import java.rmi.*;
import javax.rmi.*;

/*
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
 * but WITHOUT ANY WARRANTY; wihtout even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 */

/**
 * Invoked from the {@link Login} page, to allow a user to register
 * into the system on-the-fly.
 *
 * @author Howard Ship
 * @version $Id$
 */


public class Register
extends BasePage
implements IErrorProperty
{
	private String error;
	private String firstName;
	private String lastName;
	private String email;
	private String password1;
	private String password2;
	private IValidationDelegate validationDelegate;

	public void detach()
	{
		error = null;
		firstName = null;
		lastName = null;
		email = null;
		password1 = null;
		password2 = null;

    	super.detach();
	}
	
	public String getError()
	{
		return error;
	}
	
	public String getFirstName()
	{
		return firstName;
	}
	
	public String getLastName()
	{
		return lastName;
	}
	
	public String getEmail()
	{
		return email;
	}
	
	public String getPassword1()
	{
		return password1;
	}
	
	public String getPassword2()
	{
		return password2;
	}
	
	public void setError(String value)
	{
		error = value;
	}
	
	public void setFirstName(String value)
	{
		firstName = value;
	}
	
	public void setLastName(String value)
	{
		lastName = value;
	}
	
	public void setEmail(String value)
	{
		email = value;
	}
	
	public void setPassword1(String value)
	{
		password1 = value;
	}
	
	public void setPassword2(String value)
	{
		password2 = value;
	}
	
	public IValidationDelegate getValidationDelegate()
	{
	    if (validationDelegate == null)
	        validationDelegate = new SimpleValidationDelegate(this);

	    return validationDelegate;
	}

    private void setErrorField(String componentId, String message)
    {
        IValidatingTextField field;

        field = (IValidatingTextField)getComponent(componentId);
        field.setError(true);

        if (error == null)
            error = message;

        resetPasswords();
    }

    private void resetPasswords()
    {
        IValidatingTextField field;
 
        password1 = null;
        password2 = null;

        field = (IValidatingTextField)getComponent("inputPassword1");
        field.refresh();

        field = (IValidatingTextField)getComponent("inputPassword2");
        field.refresh();
    }

	public IActionListener getFormListener()
	{
		return new IActionListener()
		{
			public void actionTriggered(IComponent component, IRequestCycle cycle)
			throws RequestCycleException
			{
				attemptRegister(cycle);
			}
		};
	}
	
	private void attemptRegister(IRequestCycle cycle)
	{
		IOperations bean;
		Login login;
		IPerson user;
		
        // Check for errors from the validating text fields

		if (error != null)
        {
            resetPasswords();
            return;
        }

        // Note: we know password1 and password2 are not null
        // because they are required fields.

		if (!password1.equals(password2))
		{
			setErrorField("inputPassword1",
			    "Enter the same password twice.");
			return;
		}
				
        Visit visit = (Visit)getVisit();
		bean = visit.getOperations();
		
		try
		{
			user = bean.registerNewUser(firstName, lastName, email, password1);
		}
		catch (RegistrationException e)
		{
			setError(e.getMessage());
			return;
		}
		catch (CreateException e)
		{
			throw new ApplicationRuntimeException(e);
		}
		catch (RemoteException e)
		{
			throw new ApplicationRuntimeException(e);
		}
		
		// Ask the login page to return is to the proper place.
		
		login = (Login)cycle.getPage("Login");
		login.loginUser(user, cycle);
	}
		
}