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

package fi.helsinki.opintoni.domain.validation;

class ValidationMatchers {

    private static final String NOT_BLANK = "org.hibernate.validator.constraints.NotBlank";
    private static final String SIZE = "javax.validation.constraints.Size";

    public static <T> FieldErrorMatcher<T> notBlankConstraintViolation(String fieldName) {
        return new FieldErrorMatcher<>(fieldName, NOT_BLANK);
    }

    public static <T> FieldErrorMatcher<T> sizeConstraintViolation(String fieldName) {
        return new FieldErrorMatcher<>(fieldName, SIZE);
    }

}
