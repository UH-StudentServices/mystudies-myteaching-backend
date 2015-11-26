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

package fi.helsinki.opintoni.service;

import fi.helsinki.opintoni.dto.SessionDto;
import fi.helsinki.opintoni.security.SecurityUtils;
import fi.helsinki.opintoni.service.converter.SessionConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static fi.helsinki.opintoni.exception.http.ForbiddenException.forbiddenException;

@Service
public class SessionService {

    private final SessionConverter sessionConverter;
    private final SecurityUtils securityUtils;

    @Autowired
    public SessionService(SessionConverter sessionConverter, SecurityUtils securityUtils) {
        this.sessionConverter = sessionConverter;
        this.securityUtils = securityUtils;
    }

    public SessionDto getCurrentSession(Long userId) {
        return securityUtils.getAppUser()
            .map(appUser -> sessionConverter.toDto(appUser, userId))
            .orElseThrow(forbiddenException("No session"));
    }
}
