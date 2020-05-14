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
package org.xwiki.contrib.mentions.internal.descriptors;

import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.mentions.events.MentionEvent;
import org.xwiki.eventstream.RecordableEventDescriptor;

/**
 * Description of the user mentions notification. 
 * Used for instance in the notifications settings. 
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Singleton
@Named(MentionEvent.EVENT_TYPE)
public class MentionEventDescriptor implements RecordableEventDescriptor
{
    @Override
    public String getEventType()
    {
        return MentionEvent.EVENT_TYPE;
    }

    @Override
    public String getApplicationName()
    {
        return "mentions.application.name";
    }

    @Override
    public String getDescription()
    {
        return "mentions.mention.event.desciption";
    }

    @Override
    public String getApplicationIcon()
    {
        return "at";
    }
}
