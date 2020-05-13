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
package org.xwiki.contrib.mentions.internal.async.jobs;

import javax.inject.Named;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.mentions.internal.async.MentionsUpdatedRequest;
import org.xwiki.contrib.mentions.internal.async.MentionsUpdatedStatus;
import org.xwiki.job.AbstractJob;

import static org.xwiki.contrib.mentions.internal.async.jobs.MentionsUpdateJob.ASYNC_REQUEST_TYPE;

/**
 * Handles asynchronously the identification of new mentions in document's body, comments, annotations and AWM fields.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Named(ASYNC_REQUEST_TYPE)
public class MentionsUpdateJob extends AbstractJob<MentionsUpdatedRequest, MentionsUpdatedStatus>
{
    /**
     * The name of the job.
     */
    public static final String ASYNC_REQUEST_TYPE = "mentions-update-job";

    @Override
    protected void runInternal() throws Exception
    {
        MentionsUpdatedRequest request = this.getRequest();
        /* 
        TODO: add a traversal of the updated document, compare to the previous version 
        and send only notifications for new mentions. 
        */
    }

    @Override
    public String getType()
    {
        return ASYNC_REQUEST_TYPE;
    }
}
