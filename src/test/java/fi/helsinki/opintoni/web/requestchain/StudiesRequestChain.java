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

import fi.helsinki.opintoni.server.StudiesServer;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StudiesRequestChain<T> implements NestedRequestChain<T> {

    private final StudiesServer studiesServer;
    private final T parentBuilder;

    public StudiesRequestChain(T parentBuilder, StudiesServer studiesServer) {
        this.studiesServer = studiesServer;
        this.parentBuilder = parentBuilder;
    }

    public StudiesRequestChain<T> expectCoursePageUrls(List<String> courseIds, Locale locale) throws Exception {
        studiesServer.expectCoursePageUrlsRequest(courseIds, locale);
        return this;
    }

    public StudiesRequestChain<T> expectCoursePageUrls(Map<String, String> coursePageUrlsByCourseId, Locale locale) throws Exception {
        studiesServer.expectCoursePageUrlsRequest(coursePageUrlsByCourseId, locale);
        return this;
    }

    @Override
    public T and() {
        return parentBuilder;
    }
}
