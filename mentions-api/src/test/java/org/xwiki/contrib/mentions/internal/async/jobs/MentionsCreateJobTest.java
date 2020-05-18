package org.xwiki.contrib.mentions.internal.async.jobs;/*
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.xwiki.contrib.mentions.MentionIdentityService;
import org.xwiki.contrib.mentions.MentionXDOMService;
import org.xwiki.contrib.mentions.events.MentionEvent;
import org.xwiki.contrib.mentions.events.MentionEventParams;
import org.xwiki.contrib.mentions.internal.async.MentionsCreatedRequest;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.observation.ObservationManager;
import org.xwiki.rendering.block.MacroBlock;
import org.xwiki.rendering.block.NewLineBlock;
import org.xwiki.rendering.block.ParagraphBlock;
import org.xwiki.rendering.block.XDOM;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.DateProperty;
import com.xpn.xwiki.objects.LargeStringProperty;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.xwiki.contrib.mentions.events.MentionEvent.EVENT_TYPE;

/**
 * Test of {@link MentionsCreateJob}
 *
 * @version $Id$
 * @since 1.0
 */
@ComponentTest
public class MentionsCreateJobTest
{
    @InjectMockComponents
    private MentionsCreateJob job;

    @Mock
    private XWikiDocument document;

    @MockComponent
    protected ObservationManager observationManager;

    @MockComponent
    private MentionXDOMService xdomService;

    @MockComponent
    private MentionIdentityService identityService;

    @Test
    void runInternal()
    {
        DocumentReference authorReference = new DocumentReference("xwiki", "XWiki", "U2");
        DocumentReference documentReference = new DocumentReference("xwiki", "XWiki", "Doc");
        HashMap<String, String> mentionParams = new HashMap<>();
        mentionParams.put("identifier", "XWiki.U1");
        MacroBlock mention = new MacroBlock("mention", mentionParams, false);
        XDOM xdom = new XDOM(singletonList(new ParagraphBlock(asList(
            new NewLineBlock(),
            new MacroBlock("macro0", new HashMap<>(), false),
            new NewLineBlock(),
            mention
        ))));

        when(this.document.getAuthorReference()).thenReturn(authorReference);
        when(this.document.getDocumentReference()).thenReturn(documentReference);
        when(this.document.getXDOM()).thenReturn(xdom);
        when(this.xdomService.listMentionMacros(xdom)).thenReturn(singletonList(mention));

        Set<String> value = new HashSet<>();
        value.add("xwiki:XWiki.U1");
        when(this.identityService.resolveIdentity("XWiki.U1")).thenReturn(value);

        this.job.initialize(new MentionsCreatedRequest(this.document));
        this.job.runInternal();

        Set<String> targets = new HashSet<>();
        targets.add("xwiki:XWiki.U1");
        MentionEvent event = new MentionEvent(targets,
            new MentionEventParams().setUserReference("xwiki:XWiki.U2").setDocumentReference("xwiki:XWiki.Doc"));
        verify(this.observationManager).notify(event, "org.xwiki.contrib:mentions-notifications", "mentions.mention");
    }

    @Test
    void runInternalNoMention()
    {
        DocumentReference authorReference = new DocumentReference("xwiki", "XWiki", "U2");
        DocumentReference documentReference = new DocumentReference("xwiki", "XWiki", "Doc");

        XDOM xdom = new XDOM(singletonList(new ParagraphBlock(asList(
            new NewLineBlock(),
            new MacroBlock("macro0", new HashMap<>(), false),
            new NewLineBlock()

        ))));
        when(this.xdomService.listMentionMacros(xdom)).thenReturn(emptyList());

        when(this.document.getAuthorReference()).thenReturn(authorReference);
        when(this.document.getDocumentReference()).thenReturn(documentReference);
        when(this.document.getXDOM()).thenReturn(xdom);

        Set<String> value = new HashSet<>();
        value.add("xwiki:XWiki.U1");
        when(this.identityService.resolveIdentity("XWiki.U1")).thenReturn(value);

        this.job.initialize(new MentionsCreatedRequest(this.document));
        this.job.runInternal();

        verify(this.observationManager, never()).notify(any(), any(), any());
    }

    @Test
    void runInternalAWMFields() throws Exception
    {
        DocumentReference authorReference = new DocumentReference("xwiki", "XWiki", "U2");
        DocumentReference documentReference = new DocumentReference("xwiki", "XWiki", "Doc");

        XDOM xdom = new XDOM(singletonList(new ParagraphBlock(asList(
            new NewLineBlock(),
            new MacroBlock("macro0", new HashMap<>(), false),
            new NewLineBlock()

        ))));
        when(this.xdomService.listMentionMacros(xdom)).thenReturn(emptyList());

        when(this.document.getAuthorReference()).thenReturn(authorReference);
        when(this.document.getDocumentReference()).thenReturn(documentReference);
        when(this.document.getXDOM()).thenReturn(xdom);
        Map<DocumentReference, List<BaseObject>> xObjects = new HashMap<>();
        BaseObject baseObject = new BaseObject();
        baseObject.addField("f1", new DateProperty());
        LargeStringProperty element = new LargeStringProperty();
        element.setValue("CONTENT 1");
        baseObject.addField("f2", element);

        BaseObject baseObject2 = new BaseObject();
        LargeStringProperty element1 = new LargeStringProperty();
        element1.setValue("CONTENT 2");
        baseObject2.addField("f3", element1);
        xObjects.put(documentReference, Arrays.asList(baseObject, baseObject2));
        when(this.document.getXObjects()).thenReturn(xObjects);

        Set<String> value = new HashSet<>();
        value.add("xwiki:XWiki.U1");
        when(this.identityService.resolveIdentity("XWiki.U1")).thenReturn(value);

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("identifier", "XWiki.User");
        MacroBlock mention = new MacroBlock("mention", parameters, false);
        XDOM xdom1 = new XDOM(singletonList(mention));
        XDOM xdom2 = new XDOM(singletonList(new MacroBlock("macro0", new HashMap<>(), false)));
        when(this.xdomService.parse("CONTENT 1")).thenReturn(Optional.of(xdom1));
        when(this.xdomService.parse("CONTENT 2")).thenReturn(Optional.of(xdom2));

        when(this.xdomService.listMentionMacros(xdom1)).thenReturn(singletonList(mention));
        when(this.xdomService.listMentionMacros(xdom2)).thenReturn(emptyList());

        HashSet<String> value1 = new HashSet<>();
        value1.add("xwiki:XWiki.User");
        when(this.identityService.resolveIdentity("XWiki.User")).thenReturn(value1);

        this.job.initialize(new MentionsCreatedRequest(this.document));
        this.job.runInternal();

        verify(this.xdomService).parse("CONTENT 1");
        verify(this.xdomService).parse("CONTENT 2");
        verify(this.xdomService).listMentionMacros(xdom1);
        verify(this.xdomService).listMentionMacros(xdom2);
        HashSet<String> targets = new HashSet<>();
        targets.add("xwiki:XWiki.User");
        MentionEventParams mentionEventParams = new MentionEventParams()
                                                    .setDocumentReference(documentReference.toString())
                                                    .setUserReference(authorReference.toString());
        MentionEvent mentionEvent = new MentionEvent(targets, mentionEventParams);
        verify(this.observationManager).notify(mentionEvent,
            "org.xwiki.contrib:mentions-notifications", EVENT_TYPE);
    }

    @Test
    void getType()
    {
        assertEquals("mentions-create-job", this.job.getType());
    }
}