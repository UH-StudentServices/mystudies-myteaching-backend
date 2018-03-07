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

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static fi.helsinki.opintoni.exception.http.NotFoundException.notFoundException;

public class DtoService {

    public <T, R> R getDto(Long identifier,
                           Function<Long, Optional<T>> getter,
                           Function<T, R> converter) {
        return getter.apply(identifier)
            .map(converter)
            .orElseThrow(notFoundException("Not found"));
    }

    public <T, R> R getDto(Supplier<T> getter,
        Function<T, R> converter) {
        return converter.apply(getter.get());
    }

    public <T, R> List<R> getDtos(Long identifier,
                                  Function<Long, List<T>> getter,
                                  Function<T, R> converter) {
        return getter.apply(identifier).stream()
            .map(converter)
            .collect(Collectors.toList());
    }
}
