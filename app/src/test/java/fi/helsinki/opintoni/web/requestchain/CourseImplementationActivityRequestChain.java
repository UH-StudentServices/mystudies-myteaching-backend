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

import java.util.List;

public class CourseImplementationActivityRequestChain <T> implements NestedRequestChain<T> {

    private final CoursePageServer coursePageServer;
    private final T parentBuilder;
    private List<String> activityCourseImplementationIds;

    public CourseImplementationActivityRequestChain(T parentBuilder,
                                                    CoursePageServer coursePageServer,
                                                    List<String> activityCourseImplementationIds) {
        this.parentBuilder = parentBuilder;
        this.coursePageServer = coursePageServer;
        this.activityCourseImplementationIds = activityCourseImplementationIds;
    }

    public CourseImplementationActivityRequestChain<T> activity(String responseFile) {
        coursePageServer.expectCourseImplementationActivityRequest(activityCourseImplementationIds,
            responseFile);
        return this;
    }

    @Override
    public T and() {
        return parentBuilder;
    }

}

