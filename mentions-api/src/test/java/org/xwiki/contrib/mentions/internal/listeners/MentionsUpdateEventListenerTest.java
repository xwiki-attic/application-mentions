package org.xwiki.contrib.mentions.internal.listeners;/*
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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.xwiki.bridge.event.DocumentUpdatedEvent;
import org.xwiki.contrib.mentions.internal.async.MentionsUpdatedRequest;
import org.xwiki.job.JobException;
import org.xwiki.job.JobExecutor;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.test.junit5.LogCaptureExtension;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;

import ch.qos.logback.classic.Level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.xwiki.test.LogLevel.DEBUG;

/**
 * Test of {@link MentionsUpdatedEventListener}.
 *
 * @version $Id$
 * @since 1.0
 */
@ComponentTest
public class MentionsUpdateEventListenerTest
{
    @RegisterExtension
    LogCaptureExtension logCapture = new LogCaptureExtension(DEBUG);

    @InjectMockComponents
    private MentionsUpdatedEventListener listener;

    @Mock
    private XWikiDocument document;

    @Mock
    private XWikiContext context;

    @MockComponent
    private JobExecutor jobExecutor;

    @Test
    void onEvent() throws Exception
    {
        DocumentReference dr = new DocumentReference("xwiki", "XWiki", "Doc");
        DocumentUpdatedEvent event = new DocumentUpdatedEvent(dr);

        this.listener.onEvent(event, this.document, this.context);

        assertEquals(1, this.logCapture.size());
        assertEquals(Level.DEBUG, this.logCapture.getLogEvent(0).getLevel());
        assertEquals(
            "Event [org.xwiki.bridge.event.DocumentUpdatedEvent] received from [document] with data [context].",
            this.logCapture.getMessage(0));
        verify(this.jobExecutor)
            .execute("mentions-update-job", new MentionsUpdatedRequest(this.document, this.context));
    }

    @Test
    void onEventError() throws Exception
    {
        DocumentReference dr = new DocumentReference("xwiki", "XWiki", "Doc");
        DocumentUpdatedEvent event = new DocumentUpdatedEvent(dr);

        doThrow(new JobException(null, null)).when(this.jobExecutor)
            .execute("mentions-update-job", new MentionsUpdatedRequest(this.document, this.context));

        this.listener.onEvent(event, this.document, this.context);

        assertEquals(2, this.logCapture.size());
        assertEquals(Level.DEBUG, this.logCapture.getLogEvent(0).getLevel());
        assertEquals(
            "Event [org.xwiki.bridge.event.DocumentUpdatedEvent] received from [document] with data [context].",
            this.logCapture.getMessage(0));
        assertEquals(Level.WARN, this.logCapture.getLogEvent(1).getLevel());
        assertEquals(
            "Failed to create a Job for the Event [org.xwiki.bridge.event.DocumentUpdatedEvent] received from " 
                + "[document] with data [context]. Cause: [JobException: ]",
            this.logCapture.getMessage(1));
    }
}