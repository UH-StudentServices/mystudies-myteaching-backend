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

package fi.helsinki.opintoni.exception;

import fi.helsinki.opintoni.exception.http.ForbiddenException;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.mockito.Mockito.mock;

@Controller
public class ExceptionThrower {

    @RequestMapping("/nullpointer")
    public void throwNullPointer() {
        throw new NullPointerException();
    }

    @RequestMapping("/forbidden")
    public void throwForbidden() {
        throw new ForbiddenException("Forbidden");
    }

    @RequestMapping("/notfound")
    public void throwNotFound() {
        throw new NotFoundException("Not found");
    }

    @RequestMapping("/validationerror")
    public void throwValidationError() throws MethodArgumentNotValidException {
        MethodParameter methodParameter = mock(MethodParameter.class);
        BindingResult bindingResult = mock(BindingResult.class);
        throw new MethodArgumentNotValidException(methodParameter, bindingResult);
    }

}
