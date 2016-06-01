/*
 * This file is part of MystudiesMyteaching application.
 *
 * MystudiesMyteaching application is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MystudiesMyteaching application is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MystudiesMyteaching application.  If not, see <http://www.gnu.org/licenses/>.
 */

package fi.helsinki.opintoni.integration.unisport;

import fi.helsinki.opintoni.SpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class UnisportRestClientTest extends SpringTest {

    private static final String STUDENT_PRINCIPAL = "opiskelija@helsinki.fi";
    private static final long UNISPORT_USER_ID = 123;
    private static final String UNISPORT_EVENT_NAME = "Testikurssin tapahtuma";

    @Autowired
    private UnisportClient unisportRestClient;

    @Test
    public void thatUnisportUserIsReturned() {
        unisportServer.expectAuthorization();
        Optional<UnisportUser> userByPrincipal = unisportRestClient.getUnisportUserByPrincipal(STUDENT_PRINCIPAL, new Locale("fi"));
        assertThat(userByPrincipal.get().user).isEqualTo(UNISPORT_USER_ID);
    }

    @Test
    public void thatUserReservationsAreReturned() {
        unisportServer.expectUserReservations();
        UnisportUserReservations userReservations = unisportRestClient.getUserReservations(UNISPORT_USER_ID, new Locale("fi"));
        assertThat(userReservations.reservations.get(1).events.get(1).name).isEqualTo(UNISPORT_EVENT_NAME);
    }
}
