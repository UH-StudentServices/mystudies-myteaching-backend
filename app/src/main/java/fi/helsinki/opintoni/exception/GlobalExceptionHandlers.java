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

import fi.helsinki.opintoni.exception.http.BadRequestException;
import fi.helsinki.opintoni.exception.http.CalendarFeedNotFoundException;
import fi.helsinki.opintoni.exception.http.ForbiddenException;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandlers extends ResponseEntityExceptionHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandlers.class);

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<CommonError> handleNotFound() throws Exception {
        return new ResponseEntity<>(new CommonError("Not found"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ForbiddenException.class)
    public ResponseEntity<CommonError> handleForbidden() throws Exception {
        return new ResponseEntity<>(new CommonError("Forbidden"), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<CommonError> handleBadRequest() throws Exception {
        return new ResponseEntity<>(new CommonError("Bad request"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<CommonError> handleException(Exception e) throws Exception {

        // If the exception is annotated with @ResponseStatus, rethrow it for other handlers
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
            throw e;
        }
        LOGGER.error("Caught exception", e);

        return new ResponseEntity<>(new CommonError("Something went wrong"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = CalendarFeedNotFoundException.class)
    public ResponseEntity handleCalendarFeedNotFound() throws Exception {
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<ValidationError> errorResources = fieldErrors
            .stream()
            .map(fieldError -> new ValidationError(fieldError.getField(), fieldError.getCode()))
            .collect(Collectors.toList());
        return new ResponseEntity<>(errorResources, headers, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    class CommonError {
        public final String error;

        public CommonError(String error) {
            this.error = error;
        }
    }

    class ValidationError {
        public final String field;
        public final String errorCode;

        public ValidationError(String field, String errorCode) {
            this.field = field;
            this.errorCode = errorCode;
        }
    }
}
