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
package org.xwiki.contrib.mentions.internal;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.mentions.MentionIdentityService;
import org.xwiki.contrib.mentions.MentionNotificationService;
import org.xwiki.contrib.mentions.events.MentionEvent;
import org.xwiki.contrib.mentions.events.MentionEventParams;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.observation.ObservationManager;

import static org.xwiki.contrib.mentions.events.MentionEvent.EVENT_TYPE;

/**
 * Default implementation of {@link org.xwiki.contrib.mentions.MentionNotificationService}.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Singleton
public class DefaultMentionNotificationService implements MentionNotificationService
{
    @Inject
    private MentionIdentityService identityService;

    @Inject
    private ObservationManager observationManager;

    @Override
    public void sendNotif(DocumentReference authorReference, DocumentReference documentReference,
        String mentionedIdentity)
    {
        MentionEventParams params = new MentionEventParams()
                                        .setUserReference(authorReference.toString())
                                        .setDocumentReference(documentReference.toString());
        MentionEvent event = new MentionEvent(this.identityService.resolveIdentity(mentionedIdentity), params);
        this.observationManager.notify(event, "org.xwiki.contrib:mentions-notifications", EVENT_TYPE);
    }
}
