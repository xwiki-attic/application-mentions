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
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Named;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.mentions.MentionNotificationService;
import org.xwiki.contrib.mentions.MentionXDOMService;
import org.xwiki.contrib.mentions.internal.MentionLocation;
import org.xwiki.contrib.mentions.internal.async.MentionsCreatedRequest;
import org.xwiki.contrib.mentions.internal.async.MentionsCreatedStatus;
import org.xwiki.job.AbstractJob;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.rendering.block.MacroBlock;
import org.xwiki.rendering.block.XDOM;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.LargeStringProperty;

import static org.xwiki.contrib.mentions.internal.MentionLocation.AWM_FIELD;
import static org.xwiki.contrib.mentions.internal.MentionLocation.DOCUMENT;
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
    private MentionNotificationService notificationService;

    @Inject
    private MentionXDOMService xdomService;

    @Override
    protected void runInternal()
    {
        MentionsCreatedRequest request = this.getRequest();
        XWikiDocument doc = request.getDoc();
        DocumentReference authorReference = doc.getAuthorReference();
        DocumentReference documentReference = doc.getDocumentReference();

        handleMentions(doc.getXDOM(), authorReference, documentReference, DOCUMENT);

        traverseXObjects(doc.getXObjects(), authorReference, documentReference);
    }

    private void handleMentions(XDOM xdom, DocumentReference authorReference,
        DocumentReference documentReference, MentionLocation location)
    {
        List<MacroBlock> blocks = this.xdomService.listMentionMacros(xdom);

        Map<String, Long> counts = this.xdomService.countByIdentifier(blocks);

        counts.keySet()
            .forEach(identifier -> this.notificationService.sendNotif(authorReference, documentReference, identifier,
                location));
    }

    private void traverseXObjects(Map<DocumentReference, List<BaseObject>> xObjects, DocumentReference authorReference,
        DocumentReference documentReference)
    {
        for (Entry<DocumentReference, List<BaseObject>> entry : xObjects.entrySet()) {
            for (BaseObject baseObject : entry.getValue()) {
                if (baseObject != null) {
                    for (Object o : baseObject.getProperties()) {
                        if (o instanceof LargeStringProperty) {
                            this.xdomService.parse(((LargeStringProperty) o).getValue())
                                .ifPresent(xdom -> handleMentions(xdom, authorReference, documentReference, AWM_FIELD));
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getType()
    {
        return ASYNC_REQUEST_TYPE;
    }
}
