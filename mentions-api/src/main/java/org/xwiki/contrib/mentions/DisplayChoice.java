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

/**
 * List the different way allowed to display a user mention.
 *
 * @version $Id$
 * @since 1.0
 */
public enum DisplayChoice
{
    /**
     * Displays the first and last name of the mentioned user.
     */
    FIRST_AND_LAST_NAME,

    /**
     * Displays only the first name of the mentioned user.
     */
    FIRST_NAME,

    /**
     * Displays the login of the mentioned user.
     */
    LOGIN
}
