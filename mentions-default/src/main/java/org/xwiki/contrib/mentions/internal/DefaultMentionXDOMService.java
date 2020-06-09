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

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.mentions.MentionXDOMService;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.MacroBlock;
import org.xwiki.rendering.block.XDOM;
import org.xwiki.rendering.parser.ParseException;
import org.xwiki.rendering.parser.Parser;

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

    @Inject
    private Logger logger;

    @Inject
    @Named("xwiki/2.1")
    private Parser parser;

    @Inject
    private DocumentReferenceResolver<String> documentReferenceResolver;

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
    public Map<DocumentReference, Long> countByIdentifier(List<MacroBlock> mentions)
    {
        Map<DocumentReference, Long> ret = new HashMap<>();
        for (MacroBlock block : mentions) {
            String identifier = block.getParameter(IDENTIFIER_PARAM_NAME);
            DocumentReference reference = this.documentReferenceResolver.resolve(identifier);
            ret.merge(reference, 1L, Long::sum);
        }
        return ret;
    }

    @Override
    public Optional<XDOM> parse(String payload)
    {
        Optional<XDOM> oxdom;
        try {
            XDOM xdom = this.parser.parse(new StringReader(payload));
            oxdom = Optional.of(xdom);
        } catch (ParseException e) {
            this.logger
                .warn("Failed to parse the payload [{}]. Cause [{}].", payload, ExceptionUtils.getRootCauseMessage(e));
            oxdom = Optional.empty();
        }
        return oxdom;
    }
}
