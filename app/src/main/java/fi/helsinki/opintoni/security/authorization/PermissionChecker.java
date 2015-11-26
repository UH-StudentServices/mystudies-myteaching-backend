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

package fi.helsinki.opintoni.security.authorization;

import fi.helsinki.opintoni.domain.Ownership;
import fi.helsinki.opintoni.exception.http.ForbiddenException;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.repository.CommonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PermissionChecker {

    @Autowired
    private CommonRepository commonRepository;

    public void verifyPermission(Long userId, Long entityId, Class<? extends Ownership> entityClass) {
        Ownership ownership = commonRepository.find(entityClass, entityId);

        if (ownership == null) {
            throwNotFound(entityId, entityClass);
        }

        verifyOwner(userId, ownership);
    }

    public boolean hasPermission(Long userId, Long entityId, Class<? extends Ownership> entityClass) {
        Ownership ownership = commonRepository.find(entityClass, entityId);

        if (ownership == null) {
            throwNotFound(entityId, entityClass);
        }

        return isOwner(userId, ownership);
    }

    public void verifyPermission(Long userId, List<Long> entityIds, Class<? extends Ownership> entityClass) {
        List<? extends Ownership> entities = commonRepository.find(entityClass, entityIds);
        entities.forEach(entity -> verifyOwner(userId, entity));
    }

    private void throwNotFound(Long entityId, Class<? extends Ownership> entityClass) {
        throw new NotFoundException(String.format("Resource %s with entityId %d", entityClass.getName(), entityId));
    }

    private void verifyOwner(Long userId, Ownership ownership) {
        if (!isOwner(userId, ownership)) {
            throw new ForbiddenException("Access denied");
        }
    }

    private boolean isOwner(Long userId, Ownership ownership) {
        return ownership.getOwnerId().equals(userId);
    }

}