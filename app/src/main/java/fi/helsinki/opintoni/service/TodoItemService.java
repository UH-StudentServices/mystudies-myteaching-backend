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

import fi.helsinki.opintoni.domain.TodoItem;
import fi.helsinki.opintoni.dto.TodoItemDto;
import fi.helsinki.opintoni.repository.TodoItemRepository;
import fi.helsinki.opintoni.repository.UserRepository;
import fi.helsinki.opintoni.service.converter.TodoItemConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TodoItemService {

    private final TodoItemRepository todoItemRepository;
    private final UserRepository userRepository;
    private final TodoItemConverter todoItemConverter;

    @Autowired
    public TodoItemService(TodoItemRepository todoItemRepository, UserRepository userRepository, TodoItemConverter
        todoItemConverter) {
        this.todoItemRepository = todoItemRepository;
        this.userRepository = userRepository;
        this.todoItemConverter = todoItemConverter;
    }

    public TodoItemDto insert(final Long userId, final TodoItemDto todoItemDto) {
        TodoItem todoItem = new TodoItem();
        todoItem.user = userRepository.findOne(userId);
        todoItem.content = todoItemDto.content;
        return todoItemConverter.toDto(todoItemRepository.save(todoItem));
    }

    public void delete(final Long todoItemId) {
        todoItemRepository.delete(todoItemId);
    }

    public List<TodoItemDto> findByUserId(Long userId) {
        return todoItemRepository.findByUserId(userId)
            .stream()
            .map(todoItemConverter::toDto)
            .collect(Collectors.toList());
    }

    public TodoItemDto update(Long todoItemId, TodoItemDto todoItemDto) {
        TodoItem todoItem = todoItemRepository.findOne(todoItemId);
        todoItem.content = todoItemDto.content;
        todoItem.status = TodoItem.Status.valueOf(todoItemDto.status);
        return todoItemConverter.toDto(todoItemRepository.save(todoItem));
    }
}
