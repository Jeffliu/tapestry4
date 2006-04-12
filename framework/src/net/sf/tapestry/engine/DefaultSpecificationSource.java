package net.sf.tapestry.engine;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.tapestry.ApplicationRuntimeException;
import net.sf.tapestry.IMarkupWriter;
import net.sf.tapestry.INamespace;
import net.sf.tapestry.IRenderDescription;
import net.sf.tapestry.IResourceResolver;
import net.sf.tapestry.ISpecificationSource;
import net.sf.tapestry.Tapestry;
import net.sf.tapestry.parse.SpecificationParser;
import net.sf.tapestry.spec.ApplicationSpecification;
import net.sf.tapestry.spec.ComponentSpecification;
import net.sf.tapestry.spec.IApplicationSpecification;
import net.sf.tapestry.spec.ILibrarySpecification;
import net.sf.tapestry.spec.LibrarySpecification;
import net.sf.tapestry.util.StringSplitter;
import net.sf.tapestry.util.xml.DocumentParseException;

/**
 *  Default implementation of {@link ISpecificationSource} that
 *  expects to use the normal class loader to locate component
 *  specifications from within the classpath.
 *
 * <p>Caches specifications in memory forever, or until {@link #reset()} is invoked.
 *
 * <p>An instance of this class acts like a singleton and is shared by multiple sessions,
 * so it must be threadsafe.
 *
 * @author Howard Lewis Ship
 * @version $Id$
 * 
 **/

public class DefaultSpecificationSource implements ISpecificationSource, IRenderDescription
{
    private static final Log LOG = LogFactory.getLog(DefaultSpecificationSource.class);

    private IResourceResolver _resolver;
    private IApplicationSpecification _specification;

    private SpecificationParser _parser;

    private INamespace _applicationNamespace;
    private INamespace _frameworkNamespace;

    /**
     *  Contains previously parsed component specifications.
     *
     **/

    private Map _componentCache = new HashMap();

    /**
     *  Contains previously parsed page specifications.
     * 
     *  @since 2.2
     * 
     **/

    private Map _pageCache = new HashMap();

    /**
     *  Contains previously parsed library specifications, keyed
     *  on specification resource path.
     * 
     *  @since 2.2
     * 
     **/

    private Map _libraryCache = new HashMap();

    /**
     *  Contains {@link INamespace} instances, keyed on id (which will
     *  be null for the application specification).
     * 
     **/

    private Map _namespaceCache = new HashMap();

    public DefaultSpecificationSource(IResourceResolver resolver, IApplicationSpecification specification)
    {
        _resolver = resolver;
        _specification = specification;
    }

    /**
     *  Clears the specification cache.  This is used during debugging.
     *
     **/

    public void reset()
    {
        _componentCache.clear();
        _pageCache.clear();
        _libraryCache.clear();
        _namespaceCache.clear();

        _applicationNamespace = null;
        _frameworkNamespace = null;
    }

    /**
     *  Gets a specification.  The type is either a component specification
     *  path, or an alias to a component (registerred in the application
     *  specification).  The former always starts with a slash, the latter
     *  never does.
     *
     *  @deprecated To be removed in 2.3, use {@link #getComponentSpecification(String)}.
     * 
     **/

    public ComponentSpecification getSpecification(String type)
    {
        return getComponentSpecification(type);
    }

    protected ComponentSpecification parseSpecification(String resourcePath, boolean asPage)
    {
        ComponentSpecification result = null;

        if (LOG.isDebugEnabled())
            LOG.debug("Parsing component specification " + resourcePath);

        InputStream inputStream = openSpecification(resourcePath);

        SpecificationParser parser = getParser();

        try
        {
            if (asPage)
                result = _parser.parsePageSpecification(inputStream, resourcePath);
            else
                result = _parser.parseComponentSpecification(inputStream, resourcePath);
        }
        catch (DocumentParseException ex)
        {
            throw new ApplicationRuntimeException(
                Tapestry.getString("DefaultSpecificationSource.unable-to-parse-specification", resourcePath),
                ex);
        }
        finally
        {
            close(inputStream);
        }

        result.setSpecificationResourcePath(resourcePath);

        return result;
    }

    protected ILibrarySpecification parseLibrarySpecification(String resourcePath)
    {
        if (LOG.isDebugEnabled())
            LOG.debug("Parsing library specification " + resourcePath);

        InputStream inputStream = openSpecification(resourcePath);

        SpecificationParser parser = getParser();

        try
        {
            return getParser().parseLibrarySpecification(inputStream, resourcePath, _resolver);
        }
        catch (DocumentParseException ex)
        {
            throw new ApplicationRuntimeException(
                Tapestry.getString("DefaultSpecificationSource.unable-to-parse-specification", resourcePath),
                ex);
        }
        finally
        {
            close(inputStream);
        }
    }

    /** @since 2.2 **/

    private InputStream openSpecification(String resourcePath)
    {
        URL URL = _resolver.getResource(resourcePath);

        if (URL == null)
        {
            throw new ApplicationRuntimeException(
                Tapestry.getString("DefaultSpecificationSource.unable-to-locate-specification", resourcePath));
        }

        try
        {
            return URL.openStream();
        }
        catch (IOException ex)
        {
            throw new ApplicationRuntimeException(
                Tapestry.getString("DefaultSpecificationSource.unable-to-open-specification", resourcePath),
                ex);
        }
    }

    /** @since 2.2 **/

    private void close(InputStream stream)
    {
        try
        {
            if (stream != null)
                stream.close();
        }
        catch (IOException ex)
        {
            // Ignore it.
        }
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer("DefaultSpecificationSource@");
        buffer.append(Integer.toHexString(hashCode()));

        buffer.append('[');

        if (_componentCache != null)
        {
            synchronized (_componentCache)
            {
                buffer.append(_componentCache.keySet());
            }
        }

        buffer.append(']');

        return buffer.toString();
    }

    /** @since 1.0.6 **/

    public synchronized void renderDescription(IMarkupWriter writer)
    {
        writer.print("DefaultSpecificationSource[");

        writeCacheDescription(writer, "page", _pageCache);
        writer.beginEmpty("br");
        writer.println();

        writeCacheDescription(writer, "component", _componentCache);
        writer.print("]");
        writer.println();
    }

    private void writeCacheDescription(IMarkupWriter writer, String name, Map cache)
    {
        Set keySet = cache.keySet();

        writer.print(Tapestry.size(keySet));
        writer.print(" cached ");
        writer.print(name);
        writer.print(" specifications:");

        boolean first = true;

        Iterator i = keySet.iterator();
        while (i.hasNext())
        {
            String key = (String) i.next();

            if (first)
            {
                writer.begin("ul");
                first = false;
            }

            writer.begin("li");
            writer.print(key);
            writer.end();
        }

        if (!first)
            writer.end(); // <ul>
    }

    /**
     *  Gets a component specification.
     * 
     *  @param resourcePath the complete resource path to the specification.
     *  @throws ApplicationRuntimeException if the specification cannot be obtained.
     * 
     **/

    public synchronized ComponentSpecification getComponentSpecification(String resourcePath)
    {
        ComponentSpecification result = (ComponentSpecification) _componentCache.get(resourcePath);

        if (result == null)
        {
            result = parseSpecification(resourcePath, false);

            _componentCache.put(resourcePath, result);
        }

        return result;
    }

    public synchronized ComponentSpecification getPageSpecification(String resourcePath)
    {
        ComponentSpecification result = (ComponentSpecification) _pageCache.get(resourcePath);

        if (result == null)
        {
            result = parseSpecification(resourcePath, true);

            _pageCache.put(resourcePath, result);
        }

        return result;
    }

    public synchronized ILibrarySpecification getLibrarySpecification(String resourcePath)
    {
        ILibrarySpecification result = (LibrarySpecification) _libraryCache.get(resourcePath);

        if (result == null)
        {
            result = parseLibrarySpecification(resourcePath);
            _libraryCache.put(resourcePath, result);
        }

        return result;
    }

    public synchronized INamespace getNamespace(String id)
    {
        INamespace result = (INamespace) _namespaceCache.get(id);

        if (result == null)
        {
            result = findNamespace(id);

            _namespaceCache.put(id, result);
        }

        return result;
    }

    /** @since 2.2 **/

    private synchronized SpecificationParser getParser()
    {
        if (_parser == null)
            _parser = new SpecificationParser();

        return _parser;
    }

    public synchronized INamespace getApplicationNamespace()
    {
        if (_applicationNamespace == null)
            _applicationNamespace = new Namespace(null, null, _specification, this);

        return _applicationNamespace;
    }

    public synchronized INamespace getFrameworkNamespace()
    {
        if (_frameworkNamespace == null)
        {
            ILibrarySpecification ls = getLibrarySpecification("/net/sf/tapestry/Framework.library");

            _frameworkNamespace = new Namespace(INamespace.FRAMEWORK_NAMESPACE, null, ls, this);
        }

        return _frameworkNamespace;
    }

    /** 
     * 
     *  Finds or creates the namespace.
     * 
     *  @param id the id, or id path, of the namespace.
     *  @return the namespace,
     *  @throws ApplicationRuntimeException if the namespace does not exist
     *  @since 2.2 
     * 
     **/

    private INamespace findNamespace(String id)
    {
        StringSplitter splitter = new StringSplitter('.');

        String idPath[] = splitter.splitToArray(id);

        INamespace n = getApplicationNamespace();

        for (int i = 0; i < idPath.length; i++)
        {
            n = n.getChildNamespace(idPath[i]);
        }

        return n;
    }
}