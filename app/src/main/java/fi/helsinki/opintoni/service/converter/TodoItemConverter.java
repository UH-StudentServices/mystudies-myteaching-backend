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

package fi.helsinki.opintoni.service.converter;

import fi.helsinki.opintoni.domain.TodoItem;
import fi.helsinki.opintoni.dto.TodoItemDto;
import org.springframework.stereotype.Component;

@Component
public class TodoItemConverter {

    public TodoItemDto toDto(TodoItem todoItem) {
        TodoItemDto todoItemDto = new TodoItemDto();
        todoItemDto.id = todoItem.id;
        todoItemDto.createdDate = todoItem.getCreatedDate();
        todoItemDto.content = todoItem.content;
        todoItemDto.status = todoItem.status.name();
        return todoItemDto;
    }

}
