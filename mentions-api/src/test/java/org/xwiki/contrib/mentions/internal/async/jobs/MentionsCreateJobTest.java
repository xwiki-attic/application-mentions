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

import java.util.HashMap;
import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.xwiki.contrib.mentions.events.MentionEvent;
import org.xwiki.contrib.mentions.events.MentionEventParams;
import org.xwiki.contrib.mentions.internal.async.MentionsCreatedRequest;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.observation.ObservationManager;
import org.xwiki.rendering.block.MacroBlock;
import org.xwiki.rendering.block.NewLineBlock;
import org.xwiki.rendering.block.ParagraphBlock;
import org.xwiki.rendering.block.XDOM;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test of {@link MentionsCreateJob}
 *
 * @version $Id$
 * @since 1.0
 */
@ComponentTest
class MentionsCreateJobTest
{
    @InjectMockComponents
    private MentionsCreateJob job;

    @Mock
    private XWikiDocument document;

    @Mock
    private XWikiContext context;

    @MockComponent
    private DocumentReferenceResolver<String> documentReferenceResolver;

    @MockComponent
    protected ObservationManager observationManager;

    @Test
    void runInternal()
    {
        DocumentReference authorReference = new DocumentReference("xwiki", "XWiki", "U2");
        DocumentReference mentionnedReference = new DocumentReference("xwiki", "XWiki", "U1");
        DocumentReference documentReference = new DocumentReference("xwiki", "XWiki", "Doc");
        HashMap<String, String> mentionParams = new HashMap<>();
        mentionParams.put("identifier", "XWiki.U1");
        XDOM xdom = new XDOM(singletonList(new ParagraphBlock(asList(
            new NewLineBlock(),
            new MacroBlock("macro0", new HashMap<>(), false),
            new NewLineBlock(),
            new MacroBlock("mention", mentionParams, false)
        ))));
        
        when(this.documentReferenceResolver.resolve("XWiki.U1")).thenReturn(mentionnedReference);

        this.job.initialize(new MentionsCreatedRequest(authorReference, documentReference, xdom));
        this.job.runInternal();

        HashSet<String> targets = new HashSet<>();
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

        this.job.initialize(new MentionsCreatedRequest(authorReference, documentReference, xdom));
        this.job.runInternal();

        verify(this.observationManager, never()).notify(any(), any(), any());
    }

    @Test
    void getType()
    {
        assertEquals("mentions-create-job", this.job.getType());
    }
}