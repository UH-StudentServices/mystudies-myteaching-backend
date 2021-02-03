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

package fi.helsinki.opintoni.web.rest.privateapi;

import com.codahale.metrics.annotation.Timed;
import fi.helsinki.opintoni.dto.CourseDto;
import fi.helsinki.opintoni.dto.EventDto;
import fi.helsinki.opintoni.security.AppUser;
import fi.helsinki.opintoni.security.authorization.TeacherRoleRequired;
import fi.helsinki.opintoni.service.CourseService;
import fi.helsinki.opintoni.service.EventService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping(
    value = RestConstants.PRIVATE_API_V1,
    produces = WebConstants.APPLICATION_JSON_UTF8)
public class EnrollmentResource extends AbstractResource {

    private final EventService eventService;
    private final CourseService courseService;

    @Autowired
    public EnrollmentResource(EventService eventService,
                              CourseService courseService) {
        this.eventService = eventService;
        this.courseService = courseService;
    }

    @TeacherRoleRequired
    @RequestMapping(value = "/teachers/enrollments/events", method = RequestMethod.GET)
    @Timed
    public ResponseEntity<List<EventDto>> getTeacherEvents(Principal principal, Locale locale) {
        return response(eventService.getTeacherEvents(AppUser.appUser(principal).getPersonId(), locale));
    }

    @TeacherRoleRequired
    @RequestMapping(value = "/teachers/enrollments/courses", method = RequestMethod.GET)
    @Timed
    public ResponseEntity<List<CourseDto>> getTeacherCourses(Principal principal, Locale locale) {
        return response(courseService.getTeacherCourses(AppUser.appUser(principal).getPersonId(), locale));
    }

}
