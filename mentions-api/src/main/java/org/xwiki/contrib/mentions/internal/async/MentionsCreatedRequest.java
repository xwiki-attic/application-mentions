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

import org.xwiki.job.AbstractRequest;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;

/**
 * Mention created request, send to create a mention analysis async job.
 *
 * @version $Id$
 * @since 1.0
 */
public class MentionsCreatedRequest extends AbstractRequest
{
    private final XWikiDocument document;

    private final XWikiContext ctx;

    /**
     * Default constructor.
     *
     * @param document The created document.
     * @param ctx The context of the creation.
     */
    public MentionsCreatedRequest(XWikiDocument document, XWikiContext ctx)
    {
        this.document = document;
        this.ctx = ctx;
    }

    /**
     *
     * @return the created document.
     */
    public XWikiDocument getDocument()
    {
        return document;
    }

    /**
     *
     * @return the context of the creation.
     */
    public XWikiContext getCtx()
    {
        return ctx;
    }
}
