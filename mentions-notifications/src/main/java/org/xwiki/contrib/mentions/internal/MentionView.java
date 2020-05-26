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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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
    private String authorURL;

    private String documentURL;

    private XWikiDocument document;

    private String location;

    public String getAuthorURL()
    {
        return this.authorURL;
    }

    public MentionView setAuthorURL(String authorURL)
    {
        this.authorURL = authorURL;
        return this;
    }

    public String getDocumentURL()
    {
        return this.documentURL;
    }

    public MentionView setDocumentURL(String documentURL)
    {
        this.documentURL = documentURL;
        return this;
    }

    public XWikiDocument getDocument()
    {
        return this.document;
    }

    public MentionView setDocument(XWikiDocument document)
    {
        this.document = document;
        return this;
    }

    public String getLocation()
    {
        return this.location;
    }

    public MentionView setLocation(String location)
    {
        this.location = location;
        return this;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MentionView that = (MentionView) o;

        return new EqualsBuilder()
                   .append(this.authorURL, that.authorURL)
                   .append(this.documentURL, that.documentURL)
                   .append(this.document, that.document)
                   .append(this.location, that.location)
                   .isEquals();
    }

    @Override public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                   .append(this.authorURL)
                   .append(this.documentURL)
                   .append(this.document)
                   .append(this.location)
                   .toHashCode();
    }

    @Override
    public String toString()
    {
        return new XWikiToStringBuilder(this)
                   .append("authorURL", this.getAuthorURL())
                   .append("documentURL", this.getDocumentURL())
                   .append("location", this.getLocation())
                   .build();
    }
}
