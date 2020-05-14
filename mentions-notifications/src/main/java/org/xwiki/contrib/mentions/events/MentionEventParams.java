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
package org.xwiki.contrib.mentions.events;

/**
 * The parameters of the mention event.
 * This class is serialized to be retrieved for notifications rendering.
 *
 * @version $Id$
 * @since 1.0
 */
public class MentionEventParams
{
    private String userReference;

    private String documentReference;

    /**
     * Default constructor.
     */
    public MentionEventParams()
    {
    }

    /**
     *
     * @return The the user doing the mention.
     */
    public String getUserReference()
    {
        return userReference;
    }

    /**
     *
     * @param userReference the user doing the mention.
     * @return the current object.
     */
    public MentionEventParams setUserReference(String userReference)
    {
        this.userReference = userReference;
        return this;
    }

    /**
     *
     * @return the document in which then mention occurs.
     */
    public String getDocumentReference()
    {
        return documentReference;
    }

    /**
     *
     * @param documentReference the document in which then mention occurs.
     * @return the current object.
     */
    public MentionEventParams setDocumentReference(String documentReference)
    {
        this.documentReference = documentReference;
        return this;
    }
}
