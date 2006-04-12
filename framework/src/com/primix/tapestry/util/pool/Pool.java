package com.primix.foundation.pool;

import java.util.*; 

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
 *  A Pool is used to pool instances of a useful class.  It uses
 *  keys, much like a {@link Map}, to identify a list of pooled objects.
 *  Getting an object from the Pool atomically removes it from the
 *  pool.  It can then be re-added later.
 *
 *  <p>The implementation of Pool is threadsafe.
 *
 *  <p>TBD:  More info about objects in the pool, culling objects
 *  from the pool, etc.
 *
 *  @author Howard Ship
 *  @version $Id$
 *
 */

public class Pool
{
    private static final int MAP_SIZE = 23;

    /**
     *  Creates a new Pool using the default map size.  Creation of the map is deferred.
     *
     */

    public Pool()
    {
    }

    /**
     *  Creates a new Pool using the specified map size.  The map is created immediately.
     *
     */

    public Pool(int mapSize)
    {
        map = new HashMap(mapSize);
    }


    /**
     *  A map of PoolLists, keyed on an arbitrary object.
     *
     */

    private Map map;

    /**
     *  Returns a previously pooled object with the given key, or null if no
     *  such object exists.  Getting an object from a Pool removes it from the Pool,
     *  but it can later be re-added with {@link #add(Object,Object)}.
     *
     */

    public Object get(Object key)
    {
        PoolList list;

        if (map == null)
            return null;


        synchronized (map)
        {
            list = (PoolList)map.get(key);
        }

        if (list == null)
            return null;

        return list.get();
    }

    /**
     *  Adds an object to the pool for later retrieval.
     *
     */

    public void add(Object key, Object object)
    {
        PoolList list;

        if (map == null)
        {
            synchronized(this)
            {
                if (map == null)
                    map = new HashMap(MAP_SIZE);
            }
        }

        synchronized(map)
        {
            list = (PoolList)map.get(key);

            if (list == null)
            {
                list = new PoolList();
                map.put(key, list);
            }

            list.add(object);
        }
    }

    /**
     *  Removes all previously pooled objects from this Pool.
     *
     */

    public void clear()
    {
        if (map != null)
        {
            synchronized(map)
            {
                map.clear();
            }
        }
    }
}