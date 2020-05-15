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
package org.xwiki.contrib.mentions.internal.async;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.xwiki.job.AbstractRequest;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.rendering.block.XDOM;
import org.xwiki.text.XWikiToStringBuilder;

/**
 * Mention created request, send to create a mention analysis async job.
 *
 * @version $Id$
 * @since 1.0
 * @param <T> Type of the payload, expected to be either {@link XDOM} or {@link String}.
 */
public class MentionsCreatedRequest<T> extends AbstractRequest
{
    private final DocumentReference authorReference;

    private final DocumentReference documentReference;

    private final T payload;

    /**
     * Default constructor.
     *
     * The payload is expected to be either {@link XDOM} or {@link String}.
     *
     * @param authorReference Reference of the author of the mention.
     * @param documentReference Document in which the mention occurred.
     * @param payload The payload of the document in which the mention occurred.
     */
    public MentionsCreatedRequest(DocumentReference authorReference,
        DocumentReference documentReference, T payload)
    {
        this.authorReference = authorReference;
        this.documentReference = documentReference;
        this.payload = payload;
    }

    /**
     *
     * @return Reference of the author of the mention.
     */
    public DocumentReference getAuthorReference()
    {
        return this.authorReference;
    }

    /**
     *
     * @return Document in which the mention occurred.
     */
    public DocumentReference getDocumentReference()
    {
        return this.documentReference;
    }

    /**
     * The payload is expected to be either {@link XDOM} or {@link String}.
     *
     * @return The payload  of the document in which the mention occurred.
     */
    public T getPayload()
    {
        return this.payload;
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

        MentionsCreatedRequest that = (MentionsCreatedRequest) o;

        return new EqualsBuilder()
                   .append(this.authorReference, that.authorReference)
                   .append(this.documentReference, that.documentReference)
                   .append(this.payload, that.payload)
                   .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                   .append(this.authorReference)
                   .append(this.documentReference)
                   .append(this.payload)
                   .toHashCode();
    }

    @Override
    public String toString()
    {
        return new XWikiToStringBuilder(this)
                   .append("authorReference", this.getAuthorReference())
                   .append("documentReference", this.getDocumentReference())
                   .append("payload", this.getPayload())
                   .build();
    }
}
