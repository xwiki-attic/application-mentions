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
package org.xwiki.contrib.mentions;

import org.xwiki.component.annotation.Role;
import org.xwiki.stability.Unstable;

/**
 * A component that aims at generating unique anchors in a page.
 *
 * @version $Id$
 * @since 1.1
 */
@Role
@Unstable
public interface MentionAnchorGenerator
{
    /**
     * Generate a new anchor for the given mention identifier.
     * Note that this generated anchor should be unique as much as possible.
     *
     * @param identifier the target of the mention.
     * @return a {@code String} to be used as an anchor in the mention.
     */
    String getNextAnchor(String identifier);
}
