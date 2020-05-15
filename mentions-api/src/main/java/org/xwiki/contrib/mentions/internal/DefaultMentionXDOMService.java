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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.mentions.MentionXDOMService;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.MacroBlock;
import org.xwiki.rendering.block.XDOM;

/**
 * Default implementation of {@link MentionXDOMService}.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Singleton
public class DefaultMentionXDOMService implements MentionXDOMService
{
    private static final String MENTION_MACRO_NAME = "mention";

    private static final String IDENTIFIER_PARAM_NAME = "identifier";

    private static boolean matchMentionMacro(Block block)
    {
        return block instanceof MacroBlock && Objects.equals(((MacroBlock) block).getId(), MENTION_MACRO_NAME);
    }

    @Override
    public List<MacroBlock> listMentionMacros(XDOM xdom)
    {
        return xdom.getBlocks(DefaultMentionXDOMService::matchMentionMacro, Block.Axes.DESCENDANT);
    }

    @Override
    public Map<String, Long> countByIdentifier(List<MacroBlock> mentions)
    {
        Map<String, Long> ret = new HashMap<>();
        for (MacroBlock block : mentions) {
            String identifier = block.getParameter(IDENTIFIER_PARAM_NAME);
            ret.merge(identifier, 1L, Long::sum);
        }
        return ret;
    }
}
