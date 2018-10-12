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

import fi.helsinki.opintoni.config.AppConfiguration;
import fi.helsinki.opintoni.dto.EventDto;
import fi.helsinki.opintoni.integration.coursepage.CoursePageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventTypeResolver {

    private final AppConfiguration appConfiguration;

    @Autowired
    public EventTypeResolver(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }

    public boolean isExam(Integer oodiTypeCode) {
        return appConfiguration.getIntegerValues("courses.examTypeCodes").contains(oodiTypeCode);
    }

    public EventDto.Type getEventTypeByOodiTypeCode(Integer oodiTypeCode) {
        return isExam(oodiTypeCode) ? EventDto.Type.EXAM : EventDto.Type.DEFAULT;
    }

    public EventDto.Type getEventTypeByCoursePageEvent(CoursePageEvent coursePageEvent) {
        return coursePageEvent.exam ? EventDto.Type.EXAM : EventDto.Type.DEFAULT;
    }
}
