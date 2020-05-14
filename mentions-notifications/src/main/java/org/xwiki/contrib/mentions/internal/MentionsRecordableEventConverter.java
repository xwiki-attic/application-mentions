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

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.mentions.events.MentionEvent;
import org.xwiki.contrib.mentions.events.MentionEventParams;
import org.xwiki.eventstream.Event;
import org.xwiki.eventstream.RecordableEvent;
import org.xwiki.eventstream.RecordableEventConverter;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

import static java.util.Collections.singletonList;

/**
 * Define the conversion from an {@link MentionEvent} to a {@link org.xwiki.eventstream.internal.DefaultEvent}.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Singleton
@Named("mentions")
public class MentionsRecordableEventConverter implements RecordableEventConverter
{
    /**
     * Key of the parameter where the mentions specific values are put.
     */
    public static final String MENTIONS_PARAMETER_KEY = "mentions";

    @Inject
    private RecordableEventConverter defaultConverter;

    @Inject
    private DocumentReferenceResolver<String> documentReferenceResolver;

    @Override
    public Event convert(RecordableEvent recordableEvent, String source, Object data) throws Exception
    {
        // This code is called once when creating the notification in db 
        MentionEvent mentionEvent = (MentionEvent) recordableEvent;
        MentionEventParams params = mentionEvent.getParams();

        DocumentReference userDocument = this.documentReferenceResolver.resolve(params.getUserReference());
        DocumentReference document = this.documentReferenceResolver.resolve(params.getDocumentReference());

        // additional information needed later for rendering mentions notification are stored
        // in a MentionEvent object and serialized to json.
        // This object is unserialized when needed for the rendering.
        String json = serializeParameters(mentionEvent);

        Event convertedEvent = this.defaultConverter.convert(recordableEvent, source, data);
        convertedEvent.setUser(userDocument);
        convertedEvent.setDocument(document);
        convertedEvent.setType(MentionEvent.EVENT_TYPE);
        Map<String, String> parameters = initializeParameters(json);
        convertedEvent.setParameters(parameters);
        return convertedEvent;
    }

    private Map<String, String> initializeParameters(String value)
    {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(MENTIONS_PARAMETER_KEY, value);
        return parameters;
    }

    private String serializeParameters(MentionEvent recordableEvent) throws IOException
    {
        StringWriter w = new StringWriter();
        new ObjectMapper().writeValue(w, recordableEvent.getParams());
        return w.toString();
    }

    @Override
    public List<RecordableEvent> getSupportedEvents()
    {
        return singletonList(new MentionEvent(null, null));
    }
}
