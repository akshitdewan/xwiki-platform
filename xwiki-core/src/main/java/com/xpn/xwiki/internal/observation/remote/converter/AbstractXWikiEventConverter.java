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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xwiki.bridge.DocumentName;
import org.xwiki.component.annotation.Requirement;
import org.xwiki.context.Execution;
import org.xwiki.observation.remote.converter.AbstractEventConverter;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.LazyXWikiDocument;
import com.xpn.xwiki.doc.XWikiDocument;

/**
 * Provide some serialization tools for old apis like {@link XWikiDocument} and {@link XWikiContext}.
 * 
 * @version $Id$
 * @since 2.0RC1
 */
public abstract class AbstractXWikiEventConverter extends AbstractEventConverter
{
    private static final String CONTEXT_WIKI = "contextwiki";

    private static final String CONTEXT_USER = "contextuser";

    private static final String DOC_NAME = "docname";

    private static final String DOC_VERSION = "docversion";

    private static final String DOC_LANGUAGE = "doclanguage";

    private static final String ORIGDOC_VERSION = "origdocversion";

    private static final String ORIGDOC_LANGUAGE = "origdoclanguage";

    /**
     * Used to set some proper context informations.
     */
    @Requirement
    private Execution execution;

    /**
     * @param context the XWiki context to serialize
     * @return the serialized version of the context
     */
    protected Serializable serializeXWikiContext(XWikiContext context)
    {
        HashMap<String, Serializable> remoteDataMap = new HashMap<String, Serializable>();

        remoteDataMap.put(CONTEXT_WIKI, context.getDatabase());
        remoteDataMap.put(CONTEXT_USER, context.getUser());

        return remoteDataMap;
    }

    /**
     * @param remoteData the serialized version of the context
     * @return the XWiki context
     */
    protected XWikiContext unserializeXWikiContext(Serializable remoteData)
    {
        Map<String, Serializable> remoteDataMap = (Map<String, Serializable>) remoteData;

        XWikiContext context = (XWikiContext) this.execution.getContext().getProperty("xwikicontext");
        context.setDatabase((String) remoteDataMap.get(CONTEXT_WIKI));
        context.setUser((String) remoteDataMap.get(CONTEXT_USER));

        return context;
    }

    /**
     * @param document the document to serialize
     * @return the serialized version of the document
     */
    protected Serializable serializeXWikiDocument(XWikiDocument document)
    {
        HashMap<String, Serializable> remoteDataMap = new HashMap<String, Serializable>();

        remoteDataMap.put(DOC_NAME, new DocumentName(document.getWikiName(), document.getSpaceName(),
            document.getPageName()));

        if (!document.isNew()) {
            remoteDataMap.put(DOC_VERSION, document.getVersion());
            remoteDataMap.put(DOC_LANGUAGE, document.getLanguage());
        }

        XWikiDocument originalDocument = document.getOriginalDocument();

        if (!originalDocument.isNew()) {
            remoteDataMap.put(ORIGDOC_VERSION, originalDocument.getVersion());
            remoteDataMap.put(ORIGDOC_LANGUAGE, originalDocument.getLanguage());
        }

        return remoteDataMap;
    }

    /**
     * @param remoteData the serialized version of the document
     * @return the document
     */
    protected XWikiDocument unserializeDocument(Serializable remoteData)
    {
        Map<String, Serializable> remoteDataMap = (Map<String, Serializable>) remoteData;

        DocumentName docName = (DocumentName) remoteDataMap.get(DOC_NAME);

        XWikiDocument doc;
        if (remoteDataMap.get(DOC_VERSION) == null) {
            doc = new XWikiDocument(docName.getWiki(), docName.getSpace(), docName.getPage());
        } else {
            doc = new LazyXWikiDocument();
            doc.setDatabase(docName.getWiki());
            doc.setSpace(docName.getSpace());
            doc.setName(docName.getPage());
            doc.setLanguage((String) remoteDataMap.get(DOC_LANGUAGE));
            doc.setVersion((String) remoteDataMap.get(DOC_VERSION));
        }

        XWikiDocument origDoc;
        if (remoteDataMap.get(ORIGDOC_VERSION) == null) {
            origDoc = new XWikiDocument(docName.getWiki(), docName.getSpace(), docName.getPage());
        } else {
            origDoc = new LazyXWikiDocument();
            origDoc.setDatabase(docName.getWiki());
            origDoc.setSpace(docName.getSpace());
            origDoc.setName(docName.getPage());
            origDoc.setLanguage((String) remoteDataMap.get(ORIGDOC_LANGUAGE));
            origDoc.setVersion((String) remoteDataMap.get(ORIGDOC_VERSION));
        }

        doc.setOriginalDocument(origDoc);

        return doc;
    }
}
