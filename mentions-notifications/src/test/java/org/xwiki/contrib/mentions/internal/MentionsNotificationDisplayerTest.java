package org.xwiki.contrib.mentions.internal;/*
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Provider;
import javax.script.ScriptContext;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.xwiki.bridge.DocumentAccessBridge;
import org.xwiki.contrib.mentions.MentionsNotificationsObjectMapper;
import org.xwiki.contrib.mentions.events.MentionEvent;
import org.xwiki.contrib.mentions.events.MentionEventParams;
import org.xwiki.eventstream.Event;
import org.xwiki.eventstream.internal.DefaultEvent;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.notifications.CompositeEvent;
import org.xwiki.rendering.block.XDOM;
import org.xwiki.script.ScriptContextManager;
import org.xwiki.template.TemplateManager;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.xwiki.contrib.mentions.internal.MentionLocation.DOCUMENT;
import static org.xwiki.contrib.mentions.internal.MentionsRecordableEventConverter.MENTIONS_PARAMETER_KEY;

/**
 * Test of {@link MentionsNotificationDisplayer}.
 *
 * @version $Id$
 * @since 1.0
 */
@ComponentTest
public class MentionsNotificationDisplayerTest
{
    /**
     * TODO:
     * - test default event avec mauvais json 
     */
    @InjectMockComponents
    private MentionsNotificationDisplayer displayer;

    @Mock
    private XWikiContext context;

    @MockComponent
    private ScriptContextManager scriptContextManager;

    @MockComponent
    private DocumentReferenceResolver<String> documentReferenceResolver;

    @MockComponent
    private Provider<XWikiContext> contextProvider;

    @MockComponent
    private DocumentAccessBridge documentAccess;

    @MockComponent
    private TemplateManager templateManager;

    @MockComponent
    private MentionsNotificationsObjectMapper objectMapper;

    @Test
    void renderNotification() throws Exception
    {
        ScriptContext scriptContext = mock(ScriptContext.class);
        when(this.scriptContextManager.getScriptContext()).thenReturn(scriptContext);
        DocumentReference userReference = new DocumentReference("xwiki", "XWiki", "U1");
        when(this.documentReferenceResolver.resolve("xwiki:XWiki.U1"))
            .thenReturn(userReference);
        DocumentReference documentReference = new DocumentReference("xwiki", "XWiki", "Doc");
        when(this.documentReferenceResolver.resolve("xwiki:XWiki.Doc"))
            .thenReturn(documentReference);

        when(this.contextProvider.get()).thenReturn(this.context);

        XWikiDocument userDocument = mock(XWikiDocument.class);
        when(this.documentAccess.getDocumentInstance(userReference)).thenReturn(userDocument);
        XWikiDocument pageDocument = mock(XWikiDocument.class);
        when(this.documentAccess.getDocumentInstance(documentReference)).thenReturn(pageDocument);
        when(userDocument.getExternalURL("view", this.context)).thenReturn("http://wiki/user/1");
        when(pageDocument.getExternalURL("view", this.context)).thenReturn("http://wiki/page/1");

        DefaultEvent mentionEvent = new DefaultEvent();
        HashMap<String, String> eventParameters = new HashMap<>();
        String mpValue = "{ \"userReference\": \"xwiki:XWiki.U1\", \"documentReference\": \"xwiki:XWiki.Doc\" }";
        eventParameters.put(MENTIONS_PARAMETER_KEY, mpValue);
        mentionEvent.setParameters(eventParameters);

        when(this.templateManager.execute("mentions/mention.vm")).thenReturn(new XDOM(emptyList()));

        MentionEventParams mentionEventParams =
            new MentionEventParams().setDocumentReference(documentReference.toString())
                .setUserReference(userReference.toString())
                .setLocation(DOCUMENT);
        when(this.objectMapper.unserialize(mpValue)).thenReturn(Optional.of(mentionEventParams));

        this.displayer.renderNotification(new CompositeEvent(mentionEvent));

        Map<Event, MentionView> paramsMap = new HashMap<>();
        MentionView mentionView = new MentionView()
                                      .setAuthorURL("http://wiki/user/1")
                                      .setDocumentURL("http://wiki/page/1")
                                      .setDocument(pageDocument);
        paramsMap.put(mentionEvent, mentionView);
        verify(scriptContext).setAttribute("compositeEventParams", paramsMap, ScriptContext.ENGINE_SCOPE);
        verify(this.templateManager).execute("mentions/mention.vm");
    }

    @Test
    void getSupportedEvents()
    {
        List<String> supportedEvents = this.displayer.getSupportedEvents();
        assertEquals(1, supportedEvents.size());
        assertEquals(MentionEvent.EVENT_TYPE, supportedEvents.get(0));
    }
}