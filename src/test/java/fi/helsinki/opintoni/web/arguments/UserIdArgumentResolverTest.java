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

package fi.helsinki.opintoni.web.arguments;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.exception.http.ForbiddenException;
import fi.helsinki.opintoni.security.SecurityUtils;
import fi.helsinki.opintoni.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.util.ReflectionUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class UserIdArgumentResolverTest extends SpringTest {

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityUtils securityUtils;

    private UserIdArgumentResolver resolver;

    @Before
    public void setUp() {
        resolver = new UserIdArgumentResolver(userService, securityUtils);
    }

    @Test
    public void supportsLongParameterWithUserIdAnnotation() throws Exception {
        MethodParameter param = getMethodParam("helloUser", Long.class);
        assertThat(resolver.supportsParameter(param)).isTrue();
    }

    @Test
    public void doesNotSupportStringParameterWithoutUserIdAnnotation() throws Exception {
        MethodParameter param = getMethodParam("hello", Long.class);
        assertThat(resolver.supportsParameter(param)).isFalse();
    }

    @Test(expected = ForbiddenException.class)
    public void doesNotSupportParameterOfWrongTypeWithUserIdAnnotation() throws Exception {
        MethodParameter param = getMethodParam("helloError", String.class);
        assertThat(resolver.supportsParameter(param)).isFalse();
    }

    @Test
    public void resolveArgumentForUser() throws Exception {
        configureTeacherSecurityContext();

        MethodParameter param = getMethodParam("helloUser", Long.class);
        assertThat(4L).isEqualTo(resolver.resolveArgument(param, null, null, null));
    }

    private static MethodParameter getMethodParam(String methodName, Class<?> argType) {
        return new MethodParameter(ReflectionUtils.findMethod(MyController.class, methodName, argType), 0);
    }

    private static class MyController {
        public void helloUser(@UserId Long userId) {
        }

        public void hello(Long userId) {
        }

        public void helloError(@UserId String userId) {
        }
    }
}
