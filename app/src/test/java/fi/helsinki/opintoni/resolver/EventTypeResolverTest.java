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

package fi.helsinki.opintoni.resolver;

import fi.helsinki.opintoni.SpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EventTypeResolverTest extends SpringTest {

    @Autowired
    private EventTypeResolver eventTypeResolver;

    @Test
    public void thatExamIsResolved() {
        assertTrue(eventTypeResolver.isExam(8));
        assertTrue(eventTypeResolver.isExam(16));
        assertTrue(eventTypeResolver.isExam(19));
        assertTrue(eventTypeResolver.isExam(20));
        assertTrue(eventTypeResolver.isExam(23));
        assertTrue(eventTypeResolver.isExam(27));
        assertFalse(eventTypeResolver.isExam(5));
    }
}
