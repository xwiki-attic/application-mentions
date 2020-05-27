package org.xwiki.contrib.mentions.test.ui;/*
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

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.platform.notifications.test.po.NotificationsUserProfilePage;
import org.xwiki.test.docker.junit5.TestReference;
import org.xwiki.test.docker.junit5.UITest;
import org.xwiki.test.ui.TestUtils;
import org.xwiki.test.ui.po.ViewPage;

import static org.xwiki.test.ui.po.BootstrapSwitch.State.ON;

/**
 * Test of the mentions application UI.
 *
 * @version $Id$
 * @since 1.1
 */
@UITest(properties = { "xwikiDbHbmCommonExtraMappings=notification-filter-preferences.hbm.xml" },
    extraJARs = { "org.xwiki.platform:xwiki-platform-notifications-filters-default" })
public class MentionsIT
{
    public static final String U1_USERNAME = "U1";

    public static final String USERS_PWD = "password";

    public static final String U2_USERNAME = "U2";

    public static final String APPLICATION_ID = "mentions.application.name";

    public static final String ALTER_FORMAT = "alert";

    @FunctionalInterface
    private interface RunnableErr
    {
        void run() throws Exception;
    }

    @Test
    @Order(1)
    void basic(TestUtils setup, TestReference reference) throws Exception
    {
        actAsSuperAdmin(setup, () -> {
            // create the users.
            setup.createUser(U1_USERNAME, USERS_PWD, null);
            setup.createUser(U2_USERNAME, USERS_PWD, null);
        });

        acAsUser(setup, U2_USERNAME, USERS_PWD, () -> {
            // activate the notifications.
            NotificationsUserProfilePage.gotoPage(U2_USERNAME)
                .setApplicationState(APPLICATION_ID, ALTER_FORMAT, ON);
        });

        acAsUser(setup, U1_USERNAME, USERS_PWD, () -> {
            // mention U2
            ViewPage viewPage = setup.gotoPage(reference);
            viewPage.editWiki()
                .setContent("{{mention identifier=\"xwiki:XWiki.U1\" displayChoice=\"FIRST_AND_LAST_NAME\" }}");
        });

        acAsUser(setup, U2_USERNAME, USERS_PWD, () -> {
            setup.gotoPage(new DocumentReference("xwiki", "XWiki", U2_USERNAME));
            // check that a notif is well received
            // TODO: check notif received
        });
    }

    private void acAsUser(TestUtils setup, String username, String password, RunnableErr r) throws Exception
    {
        setup.login(username, password);
        r.run();
    }

    private void actAsSuperAdmin(TestUtils setup, RunnableErr r) throws Exception
    {
        setup.loginAsSuperAdmin();
        r.run();
    }
}
