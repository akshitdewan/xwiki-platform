/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 */
package com.xpn.xwiki.internal.observation.remote.converter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.xwiki.component.annotation.Component;
import org.xwiki.observation.event.DocumentDeleteEvent;
import org.xwiki.observation.event.DocumentSaveEvent;
import org.xwiki.observation.event.DocumentUpdateEvent;
import org.xwiki.observation.event.Event;
import org.xwiki.observation.remote.LocalEventData;
import org.xwiki.observation.remote.RemoteEventData;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;

/**
 * Convert all document event to remote events and back to local events.
 * <p>
 * It also make sure the context contains the proper information like the user or the wiki.
 * 
 * @version $Id$
 * @since 2.0M3
 */
@Component("document")
public class DocumentEventConverter extends AbstractXWikiEventConverter
{
    /**
     * The events supported by this converter.
     */
    private Set<Class< ? extends Event>> events = new HashSet<Class< ? extends Event>>()
    {
        {
            add(DocumentDeleteEvent.class);
            add(DocumentSaveEvent.class);
            add(DocumentUpdateEvent.class);
        }
    };

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.observation.remote.converter.LocalEventConverter#toRemote(org.xwiki.observation.remote.LocalEventData,
     *      org.xwiki.observation.remote.RemoteEventData)
     */
    public boolean toRemote(LocalEventData localEvent, RemoteEventData remoteEvent)
    {
        if (this.events.contains(localEvent.getEvent())) {
            // fill the remote event
            remoteEvent.setEvent((Serializable) localEvent.getEvent());
            remoteEvent.setSource(serializeXWikiDocument((XWikiDocument) localEvent.getSource()));
            remoteEvent.setData(serializeXWikiContext((XWikiContext) localEvent.getData()));

            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.observation.remote.converter.RemoteEventConverter#fromRemote(org.xwiki.observation.remote.RemoteEventData,
     *      org.xwiki.observation.remote.LocalEventData)
     */
    public boolean fromRemote(RemoteEventData remoteEvent, LocalEventData localEvent)
    {
        if (this.events.contains(remoteEvent.getEvent())) {
            // fill the local event
            localEvent.setEvent((Event) remoteEvent.getEvent());
            localEvent.setSource(unserializeDocument(remoteEvent.getSource()));
            localEvent.setData(unserializeXWikiContext(remoteEvent.getData()));

            return true;
        }

        return false;
    }
}
