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
package org.xwiki.contrib.mentions.script;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.mentions.MentionAnchorGenerator;
import org.xwiki.contrib.mentions.MentionsConfiguration;
import org.xwiki.script.service.ScriptService;
import org.xwiki.stability.Unstable;

/**
 * Script service for the Mentions application.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Singleton
@Unstable
@Named("mentions")
public class MentionsScriptService implements ScriptService
{
    @Inject
    private MentionsConfiguration configuration;

    @Inject
    private MentionAnchorGenerator anchorGenerator;

    /**
     *
     * @see MentionsConfiguration#getMentionsColor()
     * @return the mentions color configuration value.
     */
    public String getMentionsColor()
    {
        return this.configuration.getMentionsColor();
    }

    /**
     *
     * @see MentionsConfiguration#getSelfMentionsColor()
     * @return the mentions colors configuration value for the current user.
     */
    public String getSelfMentionsColor()
    {
        return this.configuration.getSelfMentionsColor();
    }

    /**
     * Generate a new anchor for the given mention identifier.
     * Note that this generated anchor should be unique as much as possible.
     *
     * @param identifier the target of the mention.
     * @return a {@code String} to be used as an anchor in the mention.
     */
    public String generateAnchor(String identifier)
    {
        return this.anchorGenerator.getNextAnchor(identifier);
    }
}
