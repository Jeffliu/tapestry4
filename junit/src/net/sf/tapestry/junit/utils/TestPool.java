package net.sf.tapestry.junit.utils;

import net.sf.tapestry.ApplicationRuntimeException;
import net.sf.tapestry.junit.TapestryTestCase;
import net.sf.tapestry.util.pool.IPoolable;
import net.sf.tapestry.util.pool.IPoolableAdaptor;
import net.sf.tapestry.util.pool.Pool;

/**
 *  Tests {@link net.sf.tapestry.util.pool.Pool}.
 *
 *  @author Howard Lewis Ship
 *  @version $Id$
 *  @since 2.4
 *
 **/

public class TestPool extends TapestryTestCase
{
    public static class OrdinaryBean
    {
    }

    public static class BadBean
    {
        private BadBean()
        {
        }
    }          

    public static class NotifyBean implements IPoolable
    {
        public boolean _reset;
        public boolean _discarded;

        public void discardFromPool()
        {
            _discarded = true;
        }

        public void resetForPool()
        {
            _reset = true;
        }
    }

    public static class AdaptedBean
    {
        public boolean _reset;
        public boolean _discarded;
    }

    public static class Adaptor implements IPoolableAdaptor
    {
        public void discardFromPool(Object object)
        {
            AdaptedBean bean = (AdaptedBean) object;

            bean._discarded = true;
        }

        public void resetForPool(Object object)
        {
            AdaptedBean bean = (AdaptedBean) object;

            bean._reset = true;
        }

    }

    public TestPool(String name)
    {
        super(name);
    }

    public void testStore()
    {
        Pool p = new Pool(false);

        assertEquals(0, p.getKeyCount());
        assertEquals(0, p.getPooledCount());

        String key = "KEY";
        String value = "VALUE";

        p.store(key, value);

        assertEquals(1, p.getKeyCount());
        assertEquals(1, p.getPooledCount());
    }

    public void testStoreAndRetrieve()
    {
        String key = "KEY";
        Pool p = new Pool(false);

        for (int i = 0; i < 3; i++)
            p.store(key, new OrdinaryBean());

        Object last = null;

        for (int i = 0; i < 4; i++)
        {
            Object bean = p.retrieve(key);

            if (i == 3)
                assertNull(bean);
            else
                assertNotNull(bean);

            assertTrue("Different bean than last retrieved. ", !(last == bean));

            last = bean;
        }
    }

    public void testNotifications()
    {
        String key = "KEY";

        Pool p = new Pool(false);
        p.setWindow(1);

        NotifyBean bean = new NotifyBean();

        assertEquals(false, bean._reset);
        assertEquals(false, bean._discarded);

        p.store(key, bean);

        assertEquals(true, bean._reset);
        assertEquals(false, bean._discarded);

        p.executeCleanup();

        assertTrue(bean._discarded);

        assertNull(p.retrieve(key));
    }

    public void testAdaptedNotifications()
    {
        String key = "KEY";

        Pool p = new Pool(false);
        p.registerAdaptor(AdaptedBean.class, new Adaptor());
        p.setWindow(1);

        AdaptedBean bean = new AdaptedBean();

        assertEquals(false, bean._reset);
        assertEquals(false, bean._discarded);

        p.store(key, bean);

        assertEquals(true, bean._reset);
        assertEquals(false, bean._discarded);

        p.executeCleanup();

        assertTrue(bean._discarded);

        assertNull(p.retrieve(key));
    }

    public void testClear()
    {
        String key = "KEY";

        Pool p = new Pool(false);
        p.setWindow(1);

        NotifyBean[] bean = new NotifyBean[3];

        for (int i = 0; i < 3; i++)
        {
            bean[i] = new NotifyBean();

            p.store(key, bean[i]);
        }

        assertEquals(3, p.getPooledCount());

        p.clear();

        for (int i = 0; i < 3; i++)
            assertEquals(true, bean[i]._discarded);

        assertEquals(0, p.getPooledCount());

        assertNull(p.retrieve(key));
    }

    /**
     *  Test retrieve and store of StringBuffer.
     * 
     **/
    
    public void testStringBuffer()
    {
        Pool p = new Pool(false);
        
        StringBuffer buffer = (StringBuffer)p.retrieve(StringBuffer.class);
        
        assertNotNull(buffer);
        
        buffer.append("foo");
        
        p.store(buffer);
        
        assertEquals(0, buffer.length());
        
        StringBuffer buffer2 = (StringBuffer)p.retrieve(StringBuffer.class);
        
        assertSame(buffer, buffer2);
        
        StringBuffer buffer3 = (StringBuffer)p.retrieve(StringBuffer.class);
        
        assertEquals(false, buffer == buffer3);
    }
    
    /**
     *  Test retrieve of a bean that can't be instantiated.
     * 
     **/
    
    public void testBadBean()
    {
        Pool p = new Pool(false);
        
        try
        {
            p.retrieve(BadBean.class);
            
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            checkException(ex, "Unable to instantiate new instance of class net.sf.tapestry.junit.utils.TestPool$BadBean.");
        }
    }
}
