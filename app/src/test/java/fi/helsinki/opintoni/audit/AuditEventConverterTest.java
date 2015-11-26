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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuditEventConverterTest {

    private final AuditEventConverter auditEventConverter = new AuditEventConverter();

    @Test
    public void thatNullAuditEventsAreConverted() {
        assertTrue(auditEventConverter.convertToAuditEvent(null).isEmpty());
    }

    @Test
    public void thatAuditEventsAreConverted() {
        List<PersistentAuditEvent> persistentAuditEvents = getPersistentAuditEvents();

        List<AuditEvent> auditEvents = auditEventConverter.convertToAuditEvent(persistentAuditEvents);

        assertEquals(1, auditEvents.size());

        AuditEvent auditEvent = auditEvents.get(0);

        assertEquals("principal", auditEvent.getPrincipal());
        assertEquals("type", auditEvent.getType());
        assertEquals(0, auditEvent.getData().size());
        assertEquals(getJodaLocalDateTime().toDate(), auditEvent.getTimestamp());
    }

    private List<PersistentAuditEvent> getPersistentAuditEvents() {
        List<PersistentAuditEvent> persistentAuditEvents = Lists.newArrayList();

        PersistentAuditEvent persistentAuditEvent = new PersistentAuditEvent();
        persistentAuditEvent.setAuditEventDate(getJodaLocalDateTime());
        persistentAuditEvent.setAuditEventType("type");
        persistentAuditEvent.setData(Maps.newHashMap());
        persistentAuditEvent.setId(1L);
        persistentAuditEvent.setPrincipal("principal");

        persistentAuditEvents.add(persistentAuditEvent);

        return persistentAuditEvents;
    }

    private LocalDateTime getJodaLocalDateTime() {
        return new LocalDateTime(2015, 10, 12, 8, 0);
    }

    @Test
    public void thatNullDataIsConverted() {
        assertTrue(auditEventConverter.convertDataToStrings(null).isEmpty());
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

        assertEquals(3, stringsByKey.size());
        assertEquals("value", stringsByKey.get("key"));
        assertEquals("127.0.0.1", stringsByKey.get("remoteAddress"));
        assertEquals("ABCDEFGHIJK", stringsByKey.get("sessionId"));
    }
}
