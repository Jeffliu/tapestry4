package tutorial.portal;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.tapestry.ApplicationRuntimeException;
import net.sf.tapestry.html.BasePage;
import net.sf.tapestry.util.xml.DocumentParseException;

/**
 *  Parses the Slashdot XML file and provides headlines.
 *
 *
 *  @author Howard Lewis Ship
 *  @version $Id$
 *
 **/

public class Slashdot extends BasePage
{
    private static final Log LOG = LogFactory.getLog(Slashdot.class);

    /**
     *  The {@link List} of {@link SlashdotStory} items.  This is <em>not</em> cleared
     *  at the end of the request cycle, because it is data that can be shared
     *  between sessions.
     *
     **/

    private List stories;
    private SlashdotStory story;
    private long lastRefresh = 0;

    private static final int REFRESH_INTERVAL = 30 * 1024;

    private static String RESOURCE_PATH = "http://slashdot.org/slashdot.xml";

    public void detach()
    {
        story = null;

        super.detach();
    }

    public void setStory(SlashdotStory value)
    {
        story = value;
    }

    public SlashdotStory getStory()
    {
        return story;
    }

    public List getStories()
    {
        long now = System.currentTimeMillis();

        if (now - lastRefresh > REFRESH_INTERVAL)
        {
            if (LOG.isDebugEnabled())
                LOG.debug("Forcing refresh");

            stories = null;
        }

        if (stories == null)
            readStories();

        return stories;
    }

    private void readStories()
    {
        if (LOG.isDebugEnabled())
            LOG.debug("Reading Slashdot stories from " + RESOURCE_PATH);

        URL url = null;
        SlashdotParser parser = new SlashdotParser();

        try
        {
            url = new URL(RESOURCE_PATH);
        }
        catch (MalformedURLException ex)
        {
            throw new ApplicationRuntimeException(ex);
        }

        try
        {
            stories = parser.parseStories(url, RESOURCE_PATH);
        }
        catch (DocumentParseException ex)
        {
            throw new ApplicationRuntimeException(ex);
        }

        lastRefresh = System.currentTimeMillis();
    }

}