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

import fi.helsinki.opintoni.domain.TodoItem;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static fi.helsinki.opintoni.domain.validation.ValidationMatchers.notBlankConstraintViolation;
import static fi.helsinki.opintoni.domain.validation.ValidationMatchers.sizeConstraintViolation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;

public class TodoItemValidationTest {

    final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    final Validator validator = factory.getValidator();

    @Test
    public void thatContentIsValidated() {
        TodoItem todoItem = getTodoItem();
        Set<ConstraintViolation<TodoItem>> result = validate(todoItem);
        assertThat(result, hasItem(notBlankConstraintViolation("content")));

        todoItem.content = " ";
        result = validate(todoItem);
        assertThat(result, hasItem(notBlankConstraintViolation("content")));

        todoItem.content = StringUtils.repeat("a", 501);
        result = validate(todoItem);
        assertThat(result, hasItem(sizeConstraintViolation("content")));

        todoItem.content = "Content";
        result = validate(todoItem);
        assertThat(result, empty());
    }

    private Set<ConstraintViolation<TodoItem>> validate(TodoItem todoItem) {
        return validator.validate(todoItem);
    }


    private TodoItem getTodoItem() {
        TodoItem todoItem = new TodoItem();
        todoItem.setCreatedBy("createdBy");
        return todoItem;
    }

}
