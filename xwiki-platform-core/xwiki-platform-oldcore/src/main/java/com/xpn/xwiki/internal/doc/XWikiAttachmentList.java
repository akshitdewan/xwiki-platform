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
 */
package com.xpn.xwiki.internal.doc;

import java.util.ArrayList;

import java.util.Collection;
import java.util.ListIterator;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.doc.XWikiDocument;

import com.xpn.xwiki.internal.AbstractNotifyOnUpdateList;

/**
 * AttachmentList that holds elements in order of filename.
 * 
 * @version $Id$
 * @param <E>
 * @since 10.0RC1
 */
public class XWikiAttachmentList extends AbstractNotifyOnUpdateList<XWikiAttachment>
{

    private final Map<String, XWikiAttachment> map = new ConcurrentSkipListMap<String, XWikiAttachment>();

    private final XWikiDocument document;

    /**
     * Initializes the map.
     * 
     * @since 10.0RC1
     */
    public XWikiAttachmentList(XWikiDocument document)
    {
        super(new ArrayList<XWikiAttachment>());
        this.document = document;
    }

    /**
     * Adds attachment to the list in order of filename.
     * 
     * @param attachment XWikiAttachment to add to the list
     * @since 10.0RC1
     */
    @Override
    public boolean add(XWikiAttachment attachment)
    {
        return set(attachment) != null;
    }

    /**
     * Adds attachment to the list in order of filename.
     * 
     * @param index index is ignored as list is reordered based on filename
     * @param attachment XWikiAttachment to add to the list
     * @since 10.0RC1
     */
    @Override
    public void add(int index, XWikiAttachment attachment)
    {
        add(attachment);
    }

    /**
     * {@inheritDoc}
     * 
     * @since 10.0RC1
     */
    @Override
    public void clear()
    {
        updateMap();
    }

    /**
     * Adds all attachments to the list in order of filename.
     * 
     * @param index index is ignored as list is reordered based on filename
     * @param c Collection that contains XWikiAttachment objects
     * @since 10.0RC1
     */
    @Override
    public boolean addAll(Collection<? extends XWikiAttachment> c)
    {
        for (XWikiAttachment x : c) {
            map.put(x.getFilename(), x);
            added(x);
        }
        updateMap();
        return true;
    }

    /**
     * Adds all attachments to the list in order of filename.
     * 
     * @param index index is ignored as list is reordered based on filename
     * @param c Collection that contains XWikiAttachment objects
     * @since 10.0RC1
     */
    @Override
    public boolean addAll(int index, Collection<? extends XWikiAttachment> c)
    {
        return addAll(c);
    }

    /**
     * {@inheritDoc}
     * 
     * @since 10.0RC1
     */
    @Override
    public XWikiAttachment remove(int index)
    {
        XWikiAttachment removedAttachment = map.remove(this.list.get(index).getFilename());
        if (removedAttachment != null) {
            removedAttachment = super.remove(index);
            document.setMetaDataDirty(true);
        }
        return removedAttachment;
    }

    /**
     * Removes XWikiAttachment.
     * 
     * @param attachment XWikiAttachment to remove.
     * @return true unless the attachment is not found
     * @since 10.0RC1
     */
    @Override
    public boolean remove(Object attachment)
    {
        XWikiAttachment xwikiAttachment = (XWikiAttachment) attachment;
        if(map.put(xwikiAttachment.getFilename(), xwikiAttachment) == null)
            return false;
        map.remove(attachment);
        updateMap();
        return true;
    }

    /**
     * Adds or replaces attachment with the same filename as the parameter.
     * 
     * @param attachment the attachment to add to the list
     * @return the attachment that was added to the list in order of filename
     * @since 10.0RC1
     */
    public XWikiAttachment set(XWikiAttachment attachment)
    {
        map.put(attachment.getFilename(), attachment);
        added(attachment);
        updateMap();
        return attachment;
    }

    /**
     * Adds or replaces attachment with the same filename as the parameter.
     * 
     * @param index this parameter is not used but is needed to override the method
     * @param attachment the attachment to add to the list
     * @return the attachment that was added to the list in order of filename
     * @since 10.0RC1
     */
    @Override
    public XWikiAttachment set(int index, XWikiAttachment attachment)
    {
        return set(attachment);
    }

    /**
     * @param filename the filename of the attachment to be returned.
     * @return attachment with the given filename or null if not found.
     * @since 10.0RC1
     */
    public XWikiAttachment getByFilename(String filename)
    {
        return map.get(filename);
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        boolean changed = false;
        for (XWikiAttachment x : (Collection<? extends XWikiAttachment>) c) {
            if (this.list.contains(x)) {
                if(remove(x)) {
                    changed = true;
                }
            }
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        boolean changed = false;
        for (XWikiAttachment x : this.list) {
            if (!((Collection<? extends XWikiAttachment>) (c)).contains(x)) {
                if(remove(x)) {
                    changed = true;
                }
            }
        }
        return changed;
    }

    /** Called when the list is updated. The method will be called at least once, but may be called several times */
    public void onUpdate()
    {
        document.setMetaDataDirty(true);
    }
    
    public void updateMap()
    {
        document.setMetaDataDirty(true);
        this.list = new ArrayList<>(map.values());
    }

    /**
     * @param element XWikiAttachment that was added to the list
     * @since 10.0RC1
     */
    protected void added(XWikiAttachment element)
    {
        element.setDoc(document);
    }

}
