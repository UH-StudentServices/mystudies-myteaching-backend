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

package fi.helsinki.opintoni.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.function.Supplier;

public abstract class AbstractResource {

    protected final <T> ResponseEntity<T> response(T result) {
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    protected final <T> ResponseEntity<T> response(Supplier<T> resultSupplier) {
        return response(resultSupplier.get());
    }

    protected final ResponseEntity noContentResponse() {
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
