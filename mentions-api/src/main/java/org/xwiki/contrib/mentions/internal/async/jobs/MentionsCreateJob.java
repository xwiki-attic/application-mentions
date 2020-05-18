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
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.mentions.MentionIdentityService;
import org.xwiki.contrib.mentions.MentionXDOMService;
import org.xwiki.contrib.mentions.events.MentionEvent;
import org.xwiki.contrib.mentions.events.MentionEventParams;
import org.xwiki.contrib.mentions.internal.async.MentionsCreatedRequest;
import org.xwiki.contrib.mentions.internal.async.MentionsCreatedStatus;
import org.xwiki.job.AbstractJob;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.rendering.block.MacroBlock;
import org.xwiki.rendering.block.XDOM;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.LargeStringProperty;

import static org.xwiki.contrib.mentions.events.MentionEvent.EVENT_TYPE;
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
    private MentionIdentityService identityService;

    @Inject
    private MentionXDOMService xdomService;

    @Override
    protected void runInternal()
    {
        MentionsCreatedRequest request = this.getRequest();
        XWikiDocument doc = request.getDoc();
        DocumentReference authorReference = doc.getAuthorReference();
        DocumentReference documentReference = doc.getDocumentReference();

        handleMentions(doc.getXDOM(), authorReference, documentReference);

        traverseXObjects(doc.getXObjects(), authorReference, documentReference);
    }

    private void handleMentions(XDOM xdom, DocumentReference authorReference,
        DocumentReference documentReference)
    {
        List<MacroBlock> blocks = this.xdomService.listMentionMacros(xdom);
        /*
         * TODO: for now, a mention is done for each occurent of the macro in the DOM that might be a little wild, but
         *  if the feature is used as expected that should not be a problem an an user can be interested to know that
         * he has been mentioned twice in different parts, espacially if two links are send, one for each mention That
         * might also not be what we want in the case of shorter texts sur as AWM fields for instances. Anyway that has
         * to be clarifier and uniformized (for instance for now, mentining a user twice in an AWM field creation send
         * 2 notifs, but adding two mentions in the edit of the same AWM field send only one notif).
         * TODO: do not forget to add test to validate one behavior or the other
         */
        for (MacroBlock macro : blocks) {
            // TODO: deal with group members.
            // TODO: deal with targets outside the system.
            String identifier = macro.getParameter("identifier");
            Set<String> identity = this.identityService.resolveIdentity(identifier);
            MentionEventParams params = new MentionEventParams()
                                            .setUserReference(authorReference.toString())
                                            .setDocumentReference(documentReference.toString());
            MentionEvent event = new MentionEvent(identity, params);
            MentionsCreateJob.this.observationManager
                .notify(event, "org.xwiki.contrib:mentions-notifications", EVENT_TYPE);
        }
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
                                .ifPresent(xdom -> handleMentions(xdom, authorReference, documentReference));
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
