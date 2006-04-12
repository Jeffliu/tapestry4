package net.sf.tapestry.junit;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import net.sf.tapestry.DefaultResourceResolver;
import net.sf.tapestry.IComponentStringsSource;
import net.sf.tapestry.IPage;
import net.sf.tapestry.IResourceResolver;
import net.sf.tapestry.Tapestry;
import net.sf.tapestry.engine.DefaultStringsSource;
import net.sf.tapestry.parse.SpecificationParser;
import net.sf.tapestry.spec.ApplicationSpecification;
import net.sf.tapestry.spec.ComponentSpecification;
import net.sf.tapestry.spec.IApplicationSpecification;
import net.sf.tapestry.spec.ILibrarySpecification;
import net.sf.tapestry.spec.LibrarySpecification;
import net.sf.tapestry.util.IPropertyHolder;

/**
 *  Base class for Tapestry test cases.
 *
 *  @author Howard Lewis Ship
 *  @version $Id$
 *  @since 2.2
 * 
 **/

public class TapestryTestCase extends TestCase
{
    private IResourceResolver _resolver = new DefaultResourceResolver();
    
    public TapestryTestCase(String name)
    {
        super(name);
    }

    protected IPage createPage(String specificationPath, Locale locale)
    {
        IComponentStringsSource source = new DefaultStringsSource(_resolver);
        MockEngine engine = new MockEngine();
        engine.setComponentStringsSource(source);

        MockPage result = new MockPage();
        result.setEngine(engine);
        result.setLocale(locale);

        ComponentSpecification spec = new ComponentSpecification();
        spec.setSpecificationResourcePath(specificationPath);
        result.setSpecification(spec);

        return result;
    }

    protected ComponentSpecification parseComponent(String simpleName) throws Exception
    {
        SpecificationParser parser = new SpecificationParser();

        InputStream input = getClass().getResourceAsStream(simpleName);

        return parser.parseComponentSpecification(input, simpleName);
    }


    protected IApplicationSpecification parseApp(String simpleName) throws Exception
    {
        SpecificationParser parser = new SpecificationParser();

        InputStream input = getClass().getResourceAsStream(simpleName);

        return parser.parseApplicationSpecification(input, simpleName, _resolver);
    }
    
    protected ILibrarySpecification parseLib(String simpleName) throws Exception
    {
        SpecificationParser parser = new SpecificationParser();

        InputStream input = getClass().getResourceAsStream(simpleName);

        return parser.parseLibrarySpecification(input, simpleName, _resolver);
    }
        


    protected void checkList(String propertyName, String[] expected, List actual)
    {
        int count = Tapestry.size(actual);
        
        assertEquals(propertyName + " element count", expected.length, count);
        
        for (int i = 0; i < count; i++)
        {
            assertEquals("propertyName[" + i + "]", expected[i], actual.get(i));
        }                  
    }


    protected void checkProperty(IPropertyHolder h, String propertyName, String expectedValue)
    {
        assertEquals("Property " + propertyName + ".",
            expectedValue,
            h.getProperty(propertyName));
    }

    protected void checkException(Throwable ex, String string)
    {
        if (ex.getMessage().indexOf(string) >= 0)
            return;

        throw new AssertionFailedError("Exception " + ex + " does not contain sub-string '" + string + "'.");
    }

}