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

import fi.helsinki.opintoni.server.CourseCmsServer;

import java.util.Locale;

import static fi.helsinki.opintoni.integration.IntegrationUtil.getSisuCourseUnitRealisationId;

public class CourseCmsRequestChain<T> implements NestedRequestChain<T> {

    private final CourseCmsServer courseCmsServer;
    private final T parentBuilder;
    private final String courseUnitRealisationId;
    private final Locale locale;

    public CourseCmsRequestChain(T parentBuilder, CourseCmsServer courseCmsServer, String courseUnitRealisationId, Locale locale) {
        this.courseCmsServer = courseCmsServer;
        this.parentBuilder = parentBuilder;
        this.courseUnitRealisationId = getSisuCourseUnitRealisationId(courseUnitRealisationId);
        this.locale = locale;
    }

    protected CourseCmsRequestChain<T> expectCourseUnitRealisation() {
        courseCmsServer.expectCourseUnitRealisationRequest(courseUnitRealisationId, locale);
        return this;
    }

    protected CourseCmsRequestChain<T> expectCourseUnitRealisation(String responseFile) {
        courseCmsServer.expectCourseUnitRealisationRequest(courseUnitRealisationId, responseFile, locale);
        return this;
    }

    @Override
    public T and() {
        return parentBuilder;
    }
}
