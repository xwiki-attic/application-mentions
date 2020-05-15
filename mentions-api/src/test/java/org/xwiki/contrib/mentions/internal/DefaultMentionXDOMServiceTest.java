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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.xwiki.rendering.block.GroupBlock;
import org.xwiki.rendering.block.MacroBlock;
import org.xwiki.rendering.block.NewLineBlock;
import org.xwiki.rendering.block.ParagraphBlock;
import org.xwiki.rendering.block.XDOM;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test of {@link DefaultMentionXDOMService}.
 *
 * @version $Id$
 * @since 1.0
 */
@ComponentTest
public class DefaultMentionXDOMServiceTest
{
    @InjectMockComponents
    private DefaultMentionXDOMService xdomService;
    
    @Test
    void listMentionMacros()
    {
        List<MacroBlock> actual = this.xdomService.listMentionMacros(new XDOM(singletonList(new ParagraphBlock(asList(
            new NewLineBlock(),
            new GroupBlock(singletonList(
                new MacroBlock("mention", new HashMap<>(), true)
            ))
        )))));
        assertEquals(1, actual.size());
        assertEquals(new MacroBlock("mention", new HashMap<>(), true), actual.get(0));
    }

    @Test
    void countByIdentifierEmpty()
    {
        Map<String, Long> actual = this.xdomService.countByIdentifier(emptyList());
        assertTrue(actual.isEmpty());
    }

    @Test
    void countByIdentifierOne()
    {
        Map<String, Long> actual = this.xdomService.countByIdentifier(singletonList(
            initMentionMacro("A")
        ));
        HashMap<String, Long> expected = new HashMap<>();
        expected.put("A", 1L);
        assertEquals(expected, actual);
    }

    @Test
    void countByIdentifierTwo()
    {
        Map<String, Long> actual = this.xdomService.countByIdentifier(asList(
            initMentionMacro("A"),
            initMentionMacro("A")
        ));
        HashMap<String, Long> expected = new HashMap<>();
        expected.put("A", 2L);
        assertEquals(expected, actual);
    }

    @Test
    void countByIdentifierThree()
    {
        Map<String, Long> actual = this.xdomService.countByIdentifier(asList(
            initMentionMacro("A"),
            initMentionMacro("B"),
            initMentionMacro("A")
        ));
        HashMap<String, Long> expected = new HashMap<>();
        expected.put("B", 1L);
        expected.put("A", 2L);
        assertEquals(expected, actual);
    }

    private MacroBlock initMentionMacro(String identifier)
    {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("identifier", identifier);
        return new MacroBlock("mention", parameters, false);
    }
}