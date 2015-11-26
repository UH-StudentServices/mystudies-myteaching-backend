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

package fi.helsinki.opintoni.web.rest.privateapi;

import com.codahale.metrics.annotation.Timed;
import fi.helsinki.opintoni.domain.TodoItem;
import fi.helsinki.opintoni.dto.TodoItemDto;
import fi.helsinki.opintoni.security.authorization.PermissionChecker;
import fi.helsinki.opintoni.service.TimeService;
import fi.helsinki.opintoni.service.TodoItemService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.arguments.UserId;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(
    value = RestConstants.PRIVATE_API_V1 + "/todoitems",
    produces = WebConstants.APPLICATION_JSON_UTF8)
public class TodoItemResource extends AbstractResource {

    private final TodoItemService todoItemService;
    private final PermissionChecker permissionChecker;
    private final TimeService timeService;

    @Autowired
    public TodoItemResource(TodoItemService todoItemService, PermissionChecker permissionChecker, TimeService
        timeService) {
        this.todoItemService = todoItemService;
        this.permissionChecker = permissionChecker;
        this.timeService = timeService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Timed
    public ResponseEntity<List<TodoItemDto>> getAll(@UserId Long userId) {
        return response(getAllTodoItemsWithTooOldExcluded(userId));
    }

    @RequestMapping(method = RequestMethod.POST)
    @Timed
    public ResponseEntity<TodoItemDto> insert(@UserId Long userId,
                                              @Valid @RequestBody TodoItemDto todoItemDto) {
        return response(todoItemService.insert(userId, todoItemDto));
    }

    @RequestMapping(value = "/{todoItemId}", method = RequestMethod.PUT)
    @Timed
    public ResponseEntity<TodoItemDto> update(@UserId Long userId,
                                              @PathVariable("todoItemId") Long todoItemId,
                                              @Valid @RequestBody TodoItemDto todoItemDto) {
        permissionChecker.verifyPermission(userId, todoItemId, TodoItem.class);
        return response(todoItemService.update(todoItemId, todoItemDto));
    }

    @RequestMapping(value = "/{todoItemId}", method = RequestMethod.DELETE)
    @Timed
    public ResponseEntity<List<TodoItemDto>> delete(@UserId Long userId,
                                                    @PathVariable("todoItemId") Long todoItemId) {
        permissionChecker.verifyPermission(userId, todoItemId, TodoItem.class);
        return response(() -> {
            todoItemService.delete(todoItemId);
            return getAllTodoItemsWithTooOldExcluded(userId);
        });
    }

    private List<TodoItemDto> getAllTodoItemsWithTooOldExcluded(@UserId Long userId) {
        return todoItemService.findByCreatedDateAfterAndUserId(timeService.monthsAgo(4), userId);
    }
}
