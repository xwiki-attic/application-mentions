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
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Test of {@link DefaultMentionIdentityService}.
 *
 * @version $Id$
 * @since 1.0
 */
@ComponentTest
public class DefaultMentionIdentityServiceTest
{
    @InjectMockComponents
    private DefaultMentionIdentityService identityService;

    @MockComponent
    private DocumentReferenceResolver<String> documentReferenceResolver;

    @Test
    void resolveIdentityNull()
    {
        Set<String> actual = this.identityService.resolveIdentity(null);
        
        assertTrue(actual.isEmpty());
    }

    @Test
    void resolveIdentityNotFound()
    {
        when(this.documentReferenceResolver.resolve("U1")).thenReturn(null);
        
        Set<String> actual = this.identityService.resolveIdentity("U1");
        
        assertTrue(actual.isEmpty());
    }

    @Test
    void resolveIdentity()
    {
        when(this.documentReferenceResolver.resolve("U1"))
            .thenReturn(new DocumentReference("xwiki", "XWiki", "U1"));
        
        Set<String> actual = this.identityService.resolveIdentity("U1");
        
        Set<Object> expected = new HashSet<>();
        expected.add("xwiki:XWiki.U1");
        assertEquals(expected, actual);
    }
}