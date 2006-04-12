package com.primix.tapestry;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.primix.tapestry.parse.ComponentTemplate;
import com.primix.tapestry.parse.TemplateToken;
import java.util.Locale;
import com.primix.tapestry.spec.ApplicationSpecification;
import com.primix.tapestry.components.*;
import java.net.*;

/*`
 * Tapestry Web Application Framework
 * Copyright (c) 2000, 2001 by Howard Ship and Primix Solutions
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
 * Defines the core, session-persistant object used to run a Tapestry
 * application for a single client (each client will have its own instance of the engine).
 *
 * <p>The engine exists to provide core services to the pages and components
 * that make up the application.  The engine is a delegate to the
 * {@link ApplicationServlet} via the {@link #service(RequestContext)} method.
 *
 * <p>Engine instances are persisted in the {@link HttpSession} and are serializable.
 *
 * @author Howard Ship
 * @version $Id$
 */

public interface IEngine
{
    /**
    *  The name ("Home") of the default page presented when a user first accesses the
    *  application.
    *
    */

    public static final String HOME_PAGE = "Home";

    /**
    *  The name ("Exception") of the page used for reporting exceptions.
    *  
    *  <p>Such a page must have
    *  a writable JavaBeans property named 'exception' of type 
    * <code>java.lang.Throwable</code>.
    *
    */

    public static final String EXCEPTION_PAGE = "Exception";

    /**
    *  The name ("StaleLink") of the page used for reporting stale links.
    *
    */

    public static final String STALE_LINK_PAGE = "StaleLink";

    /**
    *  The name ("StaleSession") of the page used for reporting state sessions.
    *
    */

    public static final String STALE_SESSION_PAGE = "StaleSession";

    /**
    *  Forgets changes to the named page by discarding the page recorder for the page.
    *  This is used when transitioning from one part
    *  of an application to another.  All property changes for the page are lost.
    *
    *  <p>Throws an {@link ApplicationRuntimeException} if there are uncommitted changes
    *  for the recorder (in the current request cycle).
    *
    */

    public void forgetPage(String name);

    /**
    *  Returns the locale for the engine.  This locale is used when selecting
    *  templates and assets.  If the local has not been set (is null), then this
    *  method should return the default locale for the JVM.
    *
    */

    public Locale getLocale();

    /**
    *  Changes the engine's locale.  Any subsequently loaded pages will be
    *  in the new locale (though pages already loaded stay in the old locale).
    *  Generally, you should render a new page after changing the locale, to
    *  show that the locale has changed.
    *
    */

    public void setLocale(Locale value);

    /**
    *  Returns a recorder for a page.  Creates it as necessary, or reuses an existing
    *  recorder.
    *
    */

    public IPageRecorder getPageRecorder(String pageName);

    /**
    *  Returns the object used to load a page from its specification.
    *
    */

    public IPageSource getPageSource();

    /**
    *  Gets the named service, or throws an {@link
    *  ApplicationRuntimeException} if the application can't provide
    *  the named server.
    *
    *  <p>The object returned has a short lifecycle (it isn't
    *  serialized with the engine).  Repeated calls with the
    *  same name are not guarenteed to return the same object,
    *  especially in different request cycles.
    *
    */

    public IEngineService getService(String name);

    /**
    *  Returns the URL prefix that corresponds to the servlet for the application.  
    *  This is required by instances of {@link IEngineService} that need 
    *  to construct URLs for the application.  This value will include
    *  the context path.
    */

    public String getServletPrefix();

    /**
    *  Returns the context path, a string which is prepended to the names of
    *  any assets or servlets.  This may be the empty string, but won't be null.
    *
    *  <p>This value is obtained from {@link HttpServletRequest#getContextPath()}.
    */

    public String getContextPath();

    /**
    *  Returns the application specification that defines the application
    *  and its pages.
    *
    */

    public ApplicationSpecification getSpecification();

    /**
    *  Returns the source of all component specifications for the application.  
    *  The source is shared between sessions.
    *
    */

    public ISpecificationSource getSpecificationSource();

    /**
    *  Returns the source for HTML templates.
    *
    */

    public ITemplateSource getTemplateSource();

    /**
    *  Method invoked from the {@link ApplicationServlet} to perform processing of the
    *  request.
    *
    */

    public void service(RequestContext context)
    throws ServletException, IOException;

    /**
    *  Returns an object that can resolve resources and classes.
    *
    */

    public IResourceResolver getResourceResolver();

    /**
    *  Returns the visit object, an object that represents the client's visit
    *  to the application.  This is where most server-side state is stored (with
    *  the exception of persistent page properties).
    *
    *  <p>The implementation should lazily create the visit object as needed
    *  and return it.
    *
    */

    public Object getVisit();

    /**
    *  Allows the visit object to be removed; typically done when "shutting down"
    *  a user's session (by setting the visit to null).
    *
    */

    public void setVisit(Object value);

    /**
    *  Returns true if the engine has a visit object, false if the visit object
    *  has not yet been created.  This is needed because {@link #getVisit()}
    *  will create the visit object if it doesn't already exist.
    *
    */

    public boolean getHasVisit();
}