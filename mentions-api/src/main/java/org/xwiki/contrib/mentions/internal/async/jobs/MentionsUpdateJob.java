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
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.mentions.MentionIdentityService;
import org.xwiki.contrib.mentions.MentionXDOMService;
import org.xwiki.contrib.mentions.events.MentionEvent;
import org.xwiki.contrib.mentions.events.MentionEventParams;
import org.xwiki.contrib.mentions.internal.async.MentionsUpdatedRequest;
import org.xwiki.contrib.mentions.internal.async.MentionsUpdatedStatus;
import org.xwiki.job.AbstractJob;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.rendering.block.MacroBlock;
import org.xwiki.rendering.block.XDOM;

import com.xpn.xwiki.doc.XWikiDocument;

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

    @Inject
    private MentionIdentityService identityService;

    @Inject
    private MentionXDOMService xdomService;

    @Override
    protected void runInternal()
    {
        MentionsUpdatedRequest request = this.getRequest();
        XDOM oldXdom = request.getCtx().getDoc().getXDOM();
        XWikiDocument newDoc = request.getDoc();
        XDOM newXdom = newDoc.getXDOM();
        DocumentReference authorReference = newDoc.getAuthorReference();
        DocumentReference documentReference = newDoc.getDocumentReference();

        handle(oldXdom, newXdom, authorReference, documentReference);
    }

    private void handle(XDOM oldXdom, XDOM newXdom, DocumentReference authorReference,
        DocumentReference documentReference)
    {
        List<MacroBlock> oldMentions = this.xdomService.listMentionMacros(oldXdom);
        List<MacroBlock> newMentions = this.xdomService.listMentionMacros(newXdom);

        Map<String, Long> oldCounts = this.xdomService.countByIdentifier(oldMentions);
        Map<String, Long> newCounts = this.xdomService.countByIdentifier(newMentions);

        // for each user, we check its number of mentions and compare it to the same number on the 
        // old document (or 0 if the user wan't mentionned before).
        // If the number increased, a notification is send.
        newCounts.forEach((k, v) -> {
            Long oldCount = oldCounts.getOrDefault(k, 0L);
            if (v > oldCount) {
                MentionEventParams params = new MentionEventParams()
                                                .setUserReference(authorReference.toString())
                                                .setDocumentReference(documentReference.toString());
                MentionEvent event = new MentionEvent(this.identityService.resolveIdentity(k), params);
                MentionsUpdateJob.this.observationManager
                    .notify(event, "org.xwiki.contrib:mentions-notifications", MentionEvent.EVENT_TYPE);
            }
        });
    }

    @Override
    public String getType()
    {
        return ASYNC_REQUEST_TYPE;
    }
}
