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

package fi.helsinki.opintoni.web.requestchain;

import fi.helsinki.opintoni.server.CoursePageServer;

import java.util.Locale;

public class CourseImplementationRequestChain<T> implements NestedRequestChain<T> {

    private final CoursePageServer coursePageServer;
    private final T parentBuilder;
    private final String courseImplementationId;
    private final Locale locale;

    public CourseImplementationRequestChain(T parentBuilder,
                                            CoursePageServer coursePageServer,
                                            String courseImplementationId,
                                            Locale locale) {
        this.parentBuilder = parentBuilder;
        this.coursePageServer = coursePageServer;
        this.courseImplementationId = courseImplementationId;
        this.locale = locale;
    }

    protected CourseImplementationRequestChain<T> expectImplementation() {
        coursePageServer.expectCourseImplementationRequest(courseImplementationId, locale);
        return this;
    }

    protected CourseImplementationRequestChain<T> expectImplementation(
        String responseFile) {
        coursePageServer.expectCourseImplementationRequest(courseImplementationId, responseFile, locale);
        return this;
    }

    @Override
    public T and() {
        return parentBuilder;
    }

}
