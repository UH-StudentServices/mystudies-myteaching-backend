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

package fi.helsinki.opintoni.audit;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fi.helsinki.opintoni.config.audit.AuditEventConverter;
import fi.helsinki.opintoni.domain.PersistentAuditEvent;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuditEventConverterTest {

    private final AuditEventConverter auditEventConverter = new AuditEventConverter();

    @Test
    public void thatNullAuditEventsAreConverted() {
        assertThat(auditEventConverter.convertToAuditEvent(null).isEmpty()).isTrue();
    }

    @Test
    public void thatAuditEventsAreConverted() {
        List<PersistentAuditEvent> persistentAuditEvents = getPersistentAuditEvents();

        List<AuditEvent> auditEvents = auditEventConverter.convertToAuditEvent(persistentAuditEvents);

        assertThat(auditEvents).hasSize(1);

        AuditEvent auditEvent = auditEvents.get(0);

        assertThat(auditEvent.getPrincipal()).isEqualTo("principal");
        assertThat(auditEvent.getType()).isEqualTo("type");
        assertThat(auditEvent.getData()).isEmpty();
        assertThat(auditEvent.getTimestamp()).isEqualTo(getJodaLocalDateTime().toDate());
    }

    private List<PersistentAuditEvent> getPersistentAuditEvents() {

        PersistentAuditEvent persistentAuditEvent = new PersistentAuditEvent();
        persistentAuditEvent.setAuditEventDate(getJodaLocalDateTime());
        persistentAuditEvent.setAuditEventType("type");
        persistentAuditEvent.setData(Maps.newHashMap());
        persistentAuditEvent.setId(1L);
        persistentAuditEvent.setPrincipal("principal");

        List<PersistentAuditEvent> persistentAuditEvents = Lists.newArrayList();
        persistentAuditEvents.add(persistentAuditEvent);

        return persistentAuditEvents;
    }

    private LocalDateTime getJodaLocalDateTime() {
        return new LocalDateTime(2015, 10, 12, 8, 0);
    }

    @Test
    public void thatNullDataIsConverted() {
        assertThat(auditEventConverter.convertDataToStrings(null).isEmpty()).isTrue();
    }

    @Test
    public void thatDataIsConvertedToStrings() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getId()).thenReturn("ABCDEFGHIJK");

        Map<String, Object> data = Maps.newHashMap();
        data.put("key", "value");
        data.put("authenticationDetails", new WebAuthenticationDetails(request));

        Map<String, String> stringsByKey = auditEventConverter.convertDataToStrings(data);

        assertThat(stringsByKey).hasSize(3);
        assertThat(stringsByKey.get("key")).isEqualTo("value");
        assertThat(stringsByKey.get("remoteAddress")).isEqualTo("127.0.0.1");
        assertThat(stringsByKey.get("sessionId")).isEqualTo("ABCDEFGHIJK");
    }
}
