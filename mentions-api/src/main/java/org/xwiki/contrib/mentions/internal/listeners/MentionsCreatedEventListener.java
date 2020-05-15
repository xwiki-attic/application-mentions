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
package org.xwiki.contrib.mentions.internal.listeners;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.bridge.event.DocumentCreatedEvent;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.mentions.internal.async.MentionsCreatedRequest;
import org.xwiki.job.JobException;
import org.xwiki.job.JobExecutor;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.observation.AbstractEventListener;
import org.xwiki.observation.event.Event;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.LargeStringProperty;

import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.xwiki.contrib.mentions.internal.async.jobs.MentionsCreateJob.ASYNC_REQUEST_TYPE;

/**
 * Listen to entities creation. 
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Singleton
@Named("MentionsCreatedEventListener")
public class MentionsCreatedEventListener extends AbstractEventListener
{
    private static final List<DocumentCreatedEvent> EVENTS = singletonList(new DocumentCreatedEvent());

    @Inject
    private Logger logger;

    @Inject
    private JobExecutor jobExecutor;

    /**
     * Default constructor.
     */
    public MentionsCreatedEventListener()
    {
        super("MentionsCreatedEventListener", EVENTS);
    }

    @Override
    public void onEvent(Event event, Object source, Object data)
    {
        this.logger.debug("Event [{}] received from [{}] with data [{}].",
            DocumentCreatedEvent.class.getName(), source, data);

        /* 
        TODO: we are in a block thread, better move all this identification logic somewhere asynchronous ?
        Must do that once the logic is somehow stable to be sure that the content store the in request is relevant.  
         */

        try {
            // doc is the only required elements for the rest of the analysis, 
            // can be broadcasted as is on an async job. 
            XWikiDocument doc = (XWikiDocument) source;

            if (!doc.isMinorEdit()) {
                handleDocumentBody(doc);

                // look for new fields with potential mentions in the xobjects 
                traverseXObjects(doc);
            }
        } catch (JobException e) {
            this.logger.warn(
                "Failed to create a Job for the Event [{}] received from [{}] with data [{}]. Cause: [{}]",
                DocumentCreatedEvent.class.getName(), source, data, getRootCauseMessage(e));
        }
    }

    private void traverseXObjects(XWikiDocument doc)
    {
        for (Map.Entry<DocumentReference, List<BaseObject>> entry : doc.getXObjects().entrySet()) {
            for (BaseObject baseObject : entry.getValue()) {
                for (Object o : baseObject.getProperties()) {
                    if (o instanceof LargeStringProperty) {
                        handleAWMFields(doc, (LargeStringProperty) o);
                    }
                }
            }
        }
    }

    private void handleDocumentBody(XWikiDocument doc) throws JobException
    {
        if (doc.getXDOM().getChildren() != null) {
            this.jobExecutor.execute(ASYNC_REQUEST_TYPE,
                new MentionsCreatedRequest<>(doc.getAuthorReference(), doc.getDocumentReference(), doc.getXDOM()));
        }
    }

    private void handleAWMFields(XWikiDocument doc, LargeStringProperty o)
    {
        String value = o.getValue();
        try {
            MentionsCreatedRequest<String> request = new MentionsCreatedRequest<>(doc.getAuthorReference(),
                doc.getDocumentReference(), value);
            this.jobExecutor.execute(ASYNC_REQUEST_TYPE, request);
        } catch (JobException e) {
            this.logger.warn(
                "Failed to create a Job for the field [{}] of the Event [{}] received from [{}]. Cause: [{}]",
                o.getName(), DocumentCreatedEvent.class.getName(), doc, getRootCauseMessage(e));
        }
    }
}
