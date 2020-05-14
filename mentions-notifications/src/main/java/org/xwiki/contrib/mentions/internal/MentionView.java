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
package org.xwiki.contrib.mentions.internal;

import org.xwiki.text.XWikiToStringBuilder;

import com.xpn.xwiki.doc.XWikiDocument;

/**
 * Object holding precomputed data useful to the rendering of the mentions notifications.
 *
 * @version $Id$
 * @since 1.0
 */
public class MentionView
{
    private final String authorURL;

    private final String documentURL;

    private final XWikiDocument document;

    /**
     * Default constructor.
     * @param authorURL URL of the profile page of the mention author.
     * @param documentURL URL of the document in which the mention occurred.
     * @param document Document in which the mention occurred.
     */
    public MentionView(String authorURL, String documentURL, XWikiDocument document)
    {
        this.authorURL = authorURL;
        this.documentURL = documentURL;
        this.document = document;
    }

    /**
     *
     * @return URL of the profile page of the mention author.
     */
    public String getAuthorURL()
    {
        return this.authorURL;
    }

    /**
     *
     * @return URL of the document in which the mention occurred.
     */
    public String getDocumentURL()
    {
        return this.documentURL;
    }

    /**
     *
     * @return Document in which the mention occurred.
     */
    public XWikiDocument getDocument()
    {
        return this.document;
    }

    @Override
    public String toString()
    {
        return new XWikiToStringBuilder(this)
                   .append("authorURL", this.getAuthorURL())
                   .append("documentURL", this.getDocumentURL())
                   .build();
    }
}
