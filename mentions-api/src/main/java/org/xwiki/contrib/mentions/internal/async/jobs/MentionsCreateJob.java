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

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Named;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.mentions.events.MentionEvent;
import org.xwiki.contrib.mentions.events.MentionEventParams;
import org.xwiki.contrib.mentions.internal.async.MentionsCreatedRequest;
import org.xwiki.contrib.mentions.internal.async.MentionsCreatedStatus;
import org.xwiki.job.AbstractJob;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.MacroBlock;
import org.xwiki.rendering.block.XDOM;

import static org.xwiki.contrib.mentions.internal.async.jobs.MentionsCreateJob.ASYNC_REQUEST_TYPE;

/**
 * Handles asynchronously the identification of new mentions in document's body, comments, annotations and AWM fields.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Named(ASYNC_REQUEST_TYPE)
public class MentionsCreateJob extends AbstractJob<MentionsCreatedRequest, MentionsCreatedStatus>
{
    /**
     * The name of the job.
     */
    public static final String ASYNC_REQUEST_TYPE = "mentions-create-job";

    @Inject
    private DocumentReferenceResolver<String> documentReferenceResolver;

    private static boolean matchMentionMacro(Block block)
    {
        return block instanceof MacroBlock && Objects.equals(((MacroBlock) block).getId(), "mention");
    }

    @Override
    protected void runInternal()
    {
        MentionsCreatedRequest request = this.getRequest();
        XDOM xdom = request.getXDOM();
        DocumentReference authorReference = request.getAuthorReference();
        List<Block> blocks = xdom.getBlocks(MentionsCreateJob::matchMentionMacro, Block.Axes.DESCENDANT);
        for (Block block : blocks) {
            MacroBlock macro = (MacroBlock) block;
            // TODO: deal with group members.
            // TODO: deal with targets outside the system.
            Set<String> identity = Stream.of(macro.getParameter("identifier"))
                                       .map(it -> this.documentReferenceResolver.resolve(it))
                                       .filter(Objects::nonNull)
                                       .map(DocumentReference::toString)
                                       .collect(Collectors.toSet());
            MentionEventParams params = new MentionEventParams()
                                            .setUserReference(authorReference.toString())
                                            .setDocumentReference(request.getDocumentReference().toString());
            MentionEvent event = new MentionEvent(identity, params);
            this.observationManager.notify(event, "org.xwiki.contrib:mentions-notifications", MentionEvent.EVENT_TYPE);
        }
    }

    @Override
    public String getType()
    {
        return ASYNC_REQUEST_TYPE;
    }
}
