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

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.domain.TodoItem;
import fi.helsinki.opintoni.dto.TodoItemDto;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.WebTestUtils;
import org.junit.Test;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TodoItemResourceTest extends SpringTest {

    @Test
    public void thatTodoItemsReturnCorrectResponse() throws Exception {
        mockMvc.perform(get("/api/private/v1/todoitems").with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].createdDate").value(any(Number.class)))
            .andExpect(jsonPath("$[0].content").value("Do this"))
            .andExpect(jsonPath("$[1].status").value("OPEN"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].createdDate").value(any(Number.class)))
            .andExpect(jsonPath("$[1].content").value("Do this also"))
            .andExpect(jsonPath("$[1].status").value("OPEN"))
            .andExpect(jsonPath("$[2].id").value(3))
            .andExpect(jsonPath("$[2].content").value("Really old item"));
    }

    @Test
    public void thatTodoItemIsInserted() throws Exception {
        TodoItemDto todoItemDto = new TodoItemDto();
        todoItemDto.content = "Content";
        todoItemDto.status = TodoItem.Status.OPEN.name();

        mockMvc.perform(post("/api/private/v1/todoitems").with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(todoItemDto))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.id").value(any(Number.class)))
            .andExpect(jsonPath("$.createdDate").value(any(Number.class)))
            .andExpect(jsonPath("$.content").value("Content"))
            .andExpect(jsonPath("$.status").value("OPEN"));

    }

    @Test
    public void thatTodoItemIsDeleted() throws Exception {
        mockMvc.perform(delete("/api/private/v1/todoitems/1").with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void thatTodoItemIsUpdated() throws Exception {
        TodoItemDto todoItemDto = new TodoItemDto();
        todoItemDto.content = "New content";
        todoItemDto.status = TodoItem.Status.DONE.name();

        mockMvc.perform(put("/api/private/v1/todoitems/1").with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(todoItemDto))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.id").value(any(Number.class)))
            .andExpect(jsonPath("$.createdDate").value(any(Number.class)))
            .andExpect(jsonPath("$.content").value("New content"))
            .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    public void thatTodoItemDtoIsValidated() throws Exception {
        TodoItemDto todoItemDto = new TodoItemDto();
        todoItemDto.content = "";
        todoItemDto.status = "";

        mockMvc.perform(put("/api/private/v1/todoitems/1").with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(todoItemDto))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)));
    }

}
