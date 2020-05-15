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
import org.xwiki.bridge.event.DocumentCreatedEvent;
import org.xwiki.contrib.mentions.internal.async.MentionsCreatedRequest;
import org.xwiki.job.JobException;
import org.xwiki.job.JobExecutor;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.rendering.block.XDOM;
import org.xwiki.test.junit5.LogCaptureExtension;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import com.xpn.xwiki.doc.XWikiDocument;

import ch.qos.logback.classic.Level;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.xwiki.test.LogLevel.DEBUG;

/**
 * Test of {@link MentionsCreatedEventListener}.
 *
 * @version $Id$
 * @since 1.0
 */
@ComponentTest
public class MentionsCreatedEventListenerTest
{
    @RegisterExtension
    LogCaptureExtension logCapture = new LogCaptureExtension(DEBUG);

    @InjectMockComponents
    private MentionsCreatedEventListener listener;

    @Mock
    private XWikiDocument document;

    @MockComponent
    private JobExecutor jobExecutor;

    @Test
    void onEvent() throws Exception
    {
        DocumentReference dr = new DocumentReference("xwiki", "XWiki", "Doc");
        DocumentReference authorReference = new DocumentReference("xwiki", "XWiki", "U1");
        XDOM xdom = new XDOM(emptyList());
        DocumentCreatedEvent event = new DocumentCreatedEvent(dr);

        when(this.document.getAuthorReference()).thenReturn(authorReference);
        when(this.document.getDocumentReference()).thenReturn(dr);
        when(this.document.getXDOM()).thenReturn(xdom);

        this.listener.onEvent(event, this.document, null);
        verify(this.jobExecutor)
            .execute("mentions-create-job", new MentionsCreatedRequest<>(authorReference, dr, xdom));
        assertEquals(1, this.logCapture.size());
        assertEquals(Level.DEBUG, this.logCapture.getLogEvent(0).getLevel());
        assertEquals("Event [org.xwiki.bridge.event.DocumentCreatedEvent] received from [document] with data [null].",
            this.logCapture.getMessage(0));
    }

    @Test
    void onEventError() throws Exception
    {
        DocumentReference dr = new DocumentReference("xwiki", "XWiki", "Doc");
        DocumentReference authorReference = new DocumentReference("xwiki", "XWiki", "U1");
        XDOM xdom = new XDOM(emptyList());
        DocumentCreatedEvent event = new DocumentCreatedEvent(dr);

        when(this.document.getAuthorReference()).thenReturn(authorReference);
        when(this.document.getDocumentReference()).thenReturn(dr);
        when(this.document.getXDOM()).thenReturn(xdom);

        doThrow(new JobException(null, null)).when(this.jobExecutor)
            .execute("mentions-create-job", new MentionsCreatedRequest<>(authorReference, dr, xdom));

        this.listener.onEvent(event, this.document, null);
        assertEquals(2, this.logCapture.size());
        assertEquals(Level.DEBUG, this.logCapture.getLogEvent(0).getLevel());
        assertEquals(Level.WARN, this.logCapture.getLogEvent(1).getLevel());
        assertEquals("Event [org.xwiki.bridge.event.DocumentCreatedEvent] received from [document] with data [null].",
            this.logCapture.getMessage(0));
        assertEquals(
            "Failed to create a Job for the Event [org.xwiki.bridge.event.DocumentCreatedEvent] received from [document] with data [null]. Cause: [JobException: ]",
            this.logCapture.getMessage(1));
    }
}