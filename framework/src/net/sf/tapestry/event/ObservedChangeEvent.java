package net.sf.tapestry.event;

import java.io.Serializable;
import java.util.EventObject;

import net.sf.tapestry.IComponent;
import net.sf.tapestry.Tapestry;

/**
 * Event which describes a change to a particular {@link IComponent}.
 *
 * @author Howard Ship
 * @version $Id$
 * 
 **/

public class ObservedChangeEvent extends EventObject
{
    private IComponent _component;
    private String _propertyName;
    private Object _newValue;

    public ObservedChangeEvent(IComponent component, String propertyName, char newValue)
    {
        this(component, propertyName, new Character(newValue));
    }

    public ObservedChangeEvent(IComponent component, String propertyName, byte newValue)
    {
        this(component, propertyName, new Byte(newValue));
    }

    public ObservedChangeEvent(IComponent component, String propertyName, short newValue)
    {
        this(component, propertyName, new Short(newValue));
    }

    public ObservedChangeEvent(IComponent component, String propertyName, int newValue)
    {
        this(component, propertyName, new Integer(newValue));
    }

    public ObservedChangeEvent(IComponent component, String propertyName, long newValue)
    {
        this(component, propertyName, new Long(newValue));
    }

    public ObservedChangeEvent(IComponent component, String propertyName, double newValue)
    {
        this(component, propertyName, new Double(newValue));
    }

    public ObservedChangeEvent(IComponent component, String propertyName, float newValue)
    {
        this(component, propertyName, new Float(newValue));
    }

    /**
     *  Creates the event.  The new value must be null, or be a serializable object.
     *  (It is declared as Object as a concession to the Java 2 collections framework, where
     *  the implementations are serializable but the interfaces (Map, List, etc.) don't
     *  extend Serializable ... so we wait until runtime to check).
     *
     *  @param component The component (not necessarily a page) whose property changed.
     *  @param propertyName the name of the property which was changed.
     *  @param newValue The new value of the property. 
     *
     *  @throws IllegalArgumentException if propertyName is null, or
     *  if the new value is not serializable
     *
     **/

    public ObservedChangeEvent(IComponent component, String propertyName, Object newValue)
    {
        super(component);

        if (propertyName == null)
            throw new IllegalArgumentException(
                Tapestry.getString("ObservedChangeEvent.null-property-name", component));

        _component = component;
        _propertyName = propertyName;
        _newValue = newValue;
    }

    public ObservedChangeEvent(IComponent component, String propertyName, boolean newValue)
    {
        this(component, propertyName, new Boolean(newValue));
    }

    public IComponent getComponent()
    {
        return _component;
    }

    public Object getNewValue()
    {
        return _newValue;
    }

    public String getPropertyName()
    {
        return _propertyName;
    }

}