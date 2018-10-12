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

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import javax.validation.ConstraintViolation;

class FieldErrorMatcher<T> extends TypeSafeMatcher<ConstraintViolation<T>> {

    private final String fieldName;
    private final String annotationType;

    public FieldErrorMatcher(String fieldName, String annotationType) {
        this.fieldName = fieldName;
        this.annotationType = annotationType;
    }

    @Override
    protected boolean matchesSafely(ConstraintViolation<T> item) {
        return item.getPropertyPath().toString().equals(fieldName)
            && item.getConstraintDescriptor().getAnnotation().annotationType().getName().equals(annotationType);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(String.format("a %s validation constraint for field %s", annotationType, fieldName));
    }
}