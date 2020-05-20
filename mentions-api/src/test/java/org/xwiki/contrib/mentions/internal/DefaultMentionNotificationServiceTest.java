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

import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.xwiki.contrib.mentions.MentionIdentityService;
import org.xwiki.contrib.mentions.events.MentionEvent;
import org.xwiki.contrib.mentions.events.MentionEventParams;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.observation.ObservationManager;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.xwiki.contrib.mentions.events.MentionEvent.EVENT_TYPE;

/**
 * Test of {@link DefaultMentionNotificationService}.
 *
 * @version $Id$
 * @since 1.0
 */
@ComponentTest
public class DefaultMentionNotificationServiceTest
{
    @InjectMockComponents
    private DefaultMentionNotificationService notificationService;

    @MockComponent
    private MentionIdentityService identityService;

    @MockComponent
    private ObservationManager observationManager;

    @Test
    void sendNotif()
    {
        DocumentReference authorReference = new DocumentReference("xwiki", "XWiki", "Author");
        DocumentReference documentReference = new DocumentReference("xwiki", "XWiki", "Doc");
        String mentionedIdentitiy = "U2";

        HashSet<String> identity = new HashSet<>();
        identity.add("xwiki:XWiki.U2");
        when(this.identityService.resolveIdentity("U2")).thenReturn(identity);

        this.notificationService.sendNotif(authorReference, documentReference, mentionedIdentitiy);

        MentionEvent event = new MentionEvent(identity,
            new MentionEventParams()
                .setUserReference(authorReference.toString())
                .setDocumentReference(documentReference.toString())
        );
        verify(this.observationManager).notify(event, "org.xwiki.contrib:mentions-notifications", EVENT_TYPE);
    }
}