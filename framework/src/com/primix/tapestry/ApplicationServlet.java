/*
 * Tapestry Web Application Framework
 * Copyright (c) 2000, 2001 by Howard Ship and Primix
 *
 * Primix
 * 311 Arsenal Street
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
 * included    with this distribution, you may find a copy at the FSF web
 * site at 'www.gnu.org' or 'www.fsf.org', or you may write to the
 * Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139 USA.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied waranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 */

package com.primix.tapestry;

import javax.servlet.http.*;
import java.io.*;
import javax.servlet.*;
import com.primix.tapestry.spec.*;
import com.primix.tapestry.parse.*;
import com.primix.tapestry.util.exception.*;
import com.primix.tapestry.util.xml.*;
import org.apache.log4j.*;

/**
 * Links a servlet container with a Tapestry application.  The servlet has some
 * responsibilities related to bootstrapping the application (in terms of
 * logging, reading the {@link ApplicationSpecification specification}, etc.).
 * It is also responsible for creating or locating the {@link IEngine} and delegating
 * incoming requests to it.
 *
 * <p>In some servlet containers (notably
 * <a href="www.bea.com"/>WebLogic</a>,
 * it is necessary to invoke {@link HttpSession#setAttribute(String,Object)}
 * in order to force a persistent value to be replicated to the other
 * servers in the cluster.  Tapestry applications usually only have a single
 * persistent value, the {@link IEngine engine}.  For persistence to
 * work in such an environment, the
 * JVM system property <code>com.primix.tapestry.store-engine</code>
 * must be set to <code>true</code>.  This will force the application
 * servlet to restore the engine into the {@link HttpSession} at the
 * end of each request cycle.
 *
 * <p>The application servlet also has a default implementation of
 * {@link #setupLogging} that configures logging for Log4J.  Subclasses
 * with more sophisticated logging needs will need to overide this
 * method.
 *
 * <p>This class is derived from the original class
 * <code>com.primix.servlet.GatewayServlet</code>
 * part of the <b>ServletUtils</b> framework available from
 * <a href="http://www.gjt.org/servlets/JCVSlet/list/gjt/com/primix/servlet">The Giant
 * Java Tree</a>.
 *
 * @version $Id$
 * @author Howard Ship
 */


abstract public class ApplicationServlet
	extends HttpServlet
{
	private static final Category CAT =
		Category.getInstance(ApplicationServlet.class);
	
	/**
	 *  The application specification, which is read once and kept in memory
	 *  thereafter.
	 *
	 */
	
	private ApplicationSpecification specification;
	
	/**
	 * The name under which the {@link IEngine engine} is stored within the
	 * {@link HttpSession}.
	 *
	 */
	
	private String attributeName;
	
	/**
	 * Gets the class loader for the servlet.  Generally, this class (ApplicationServlet)
	 * is loaded by the system class loader, but the servlet and other classes in the
	 * application are loaded by a child class loader.  Only the child has the full view
	 * of all classes and package resources.
	 *
	 */
	
	private ClassLoader classLoader = getClass().getClassLoader();
	
	/**
	 * If true, then the engine is stored as an attribute of the HttpSession
	 * after every request.
	 *
	 * @since 0.2.12
	 */
	
	private static boolean storeEngine =
		Boolean.getBoolean("com.primix.tapestry.store-engine");
	
	/**
	 * Handles the GET and POST requests. Performs the following:
	 * <ul>
	 * <li>Construct a {@link RequestContext}
	 * <li>Invoke {@link #getEngine(RequestContext)} to get the {@link IEngine}
	 * <li>Invoke {@link IEngine#service(RequestContext)} on the application
	 * </ul>
	 */
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException
	{
		RequestContext context;
		IEngine engine;
		
		// Create a context from the various bits and pieces.
		
		context = new RequestContext(this, request, response);
		
		try
		{
			
			// The subclass provides the delegate.
			
			engine = getEngine(context);
			
			if (engine == null)
				throw new ServletException(
					"Could not locate an engine to service this request.");
			
			boolean dirty = engine.service(context);
			
			if (dirty && storeEngine)
			{
				if (CAT.isDebugEnabled())
					CAT.debug("Storing " + engine + " into session as " + attributeName);
				
				try
				{
					context.setSessionAttribute(attributeName, engine);
				}
				catch (IllegalStateException ex)
				{
					// Ignore, the session been's invalidated.
					
					if (CAT.isDebugEnabled())
						CAT.debug("Session invalidated.");
				}
			}
			
		}
		catch (ServletException ex)
		{
			log("ServletException", ex);
			
			show(ex);
			
			// Rethrow it.
			
			throw ex;
		}
		catch (IOException ex)
		{
			log("IOException", ex);
			
			show(ex);
			
			// Rethrow it.
			
			throw ex;
		}
	}
	
	protected void show(Exception ex)
	{
		System.err.println(
			"\n\n**********************************************************\n\n");
		
		new ExceptionAnalyzer().reportException(ex, System.err);
		
		System.err.println(
			"\n**********************************************************\n");
		
	}
	
	
	/**
	 * Respond the same to a POST as to a GET.
	 */
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException
	{
		doGet(request, response);
	}
	
	/**
	 *  Returns the application specification, which is read
	 *  by the {@link #init(ServletConfig)} method.
	 *
	 */
	
	public ApplicationSpecification getApplicationSpecification()
	{
		return specification;
	}
	
	/**
	 *  Retrieves the {@link IEngine} instance for this session
	 *  from the {@link HttpSession}, or invokes
	 *  {@link #createEngine(RequestContext)} to create the
	 *  application instance.
	 *
	 * <p>If the engine does not need to be stored in the {@link HttpSession}
	 * (not possible with the framework provided implementations)
	 * then this method should be overided as appropriate.
	 *
	 */
	
	protected IEngine getEngine(RequestContext context)
		throws ServletException
	{
		IEngine engine;
		
		engine = (IEngine)context.getSessionAttribute(attributeName);
		
		if (engine == null)
		{
			engine = createEngine(context);
			
			context.setSessionAttribute(attributeName, engine);
		}
		
		
		return engine;
	}
	
	/**
	 *  Reads the application specification when the servlet is
	 *  first initialized.  All {@link IEngine engine instances}
	 *  will have access to the specification via the servlet.
	 *
	 */
	
	public void init(ServletConfig config)
		throws ServletException
	{
		String path;
		ServletContext servletContext;
		String resource;
		InputStream stream;
		SpecificationParser parser;
		
		super.init(config);
		
		setupLogging();
		
		path = getApplicationSpecificationPath();
		
		// Make sure we locate the specification using our
		// own class loader.
		
		stream = getClass().getResourceAsStream(path);
		
		if (stream == null)
			throw new ServletException(
				"Could not locate application specification " + path + ".");
		
		parser = new SpecificationParser();
		
		try
		{
			if (CAT.isDebugEnabled())
				CAT.debug("Loading application specification from " + path);
			
			specification = parser.parseApplicationSpecification(stream, path);
		}
		catch (DocumentParseException ex)
		{
			show(ex);
			
			throw new ServletException(
				"Unable to read application specification " +
					path + ".",  ex);
		}
		
		attributeName = "com.primix.tapestry.engine." + specification.getName();
	}
	
	/**
	 *  Invoked from {@link #init(ServletConfig)} before the specification is loaded to
	 *  setup log4j logging.  This implemention is sufficient for testing, but should
	 *  be overiden in production applications.
	 *
	 *  <ul>
	 *  <li>Gets the JVM system property <code>com.primix.tapestry.root-logging-priority</code>,
	 *  and (if non-null), converts it to an {@link Priority} and assigns it to the root
	 *  {@link Category}.
	 *  <li>Gets the JVM system property <code></code> and uses it as the pattern
	 * for a {@link PatternLayout}.  If the property is not defined, then the
	 * default pattern <code>%c{1} [%p] %m%n</code> is used.
	 *  <li>Configures a single {@link Appender} for the root {@link Category}, a {@link FileAppender}
	 *  to <code>System.out</code>, using the pattern specified.
	 *  </ul>
	 *
	 *  @since 0.2.9
	 */
	
	protected void setupLogging()
		throws ServletException
	{
		Priority priority = Priority.ERROR;
		
		String value = System.getProperty("com.primix.tapestry.root-logging-priority");
		
		if (value != null)
			priority = Priority.toPriority(value, Priority.ERROR);
		
		Category root = Category.getRoot();
		root.setPriority(priority);

		String pattern = System.getProperty("com.primix.tapestry.log-pattern",
										  "%c{1} [%p] %m%n");
		
		Layout layout = new PatternLayout(pattern);
		Appender rootAppender = new FileAppender(layout, System.out);
		root.addAppender(rootAppender);
	}
	
	/**
	 *  Implemented in subclasses to identify the resource path
	 *  of the application specification.
	 *
	 */
	
	abstract protected String getApplicationSpecificationPath();
	
	/**
	 *  Invoked by {@link #getEngine(RequestContext)} to create
	 *  the {@link IEngine} instance specific to the
	 *  application, if not already in the
	 *  {@link HttpSession}.
	 *
	 *  <p>The {@link IEngine} instance returned is stored into the
	 *  {@link HttpSession}.
	 *
	 *  <p>This implementation instantiates a new engine as specified
	 *  by {@link ApplicationSpecification#getEngineClassName()}.
	 *
	 */
	
	protected IEngine createEngine(RequestContext context)
		throws ServletException
	{
		try
		{
			String className = specification.getEngineClassName();
			
			if (className == null)
				throw new ServletException("Application specification does not specify an engine class name.");
			
			if (CAT.isDebugEnabled())
				CAT.debug("Creating engine from class " + className);
			
			Class engineClass = Class.forName(className, true, classLoader);
			
			IEngine result = (IEngine)engineClass.newInstance();
			
			if (CAT.isDebugEnabled())
				CAT.debug("Created engine " + result);
			
			return result;
		}
		catch (Exception ex)
		{
			throw new ServletException(ex);
		}
	}
}

