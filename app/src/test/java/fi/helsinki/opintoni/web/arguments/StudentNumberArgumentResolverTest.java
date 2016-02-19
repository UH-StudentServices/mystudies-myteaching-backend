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
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.util.ReflectionUtils;

import static org.assertj.core.api.Assertions.*;

public class StudentNumberArgumentResolverTest extends SpringTest {

    @Autowired
    private SecurityUtils securityUtils;

    private StudentNumberArgumentResolver resolver;

    @Before
    public void setUp() {
        resolver = new StudentNumberArgumentResolver(securityUtils);
    }

    @Test
    public void supportsStringParameterWithStudentNumberAnnotation() throws Exception {
        MethodParameter param = getMethodParam("helloStudent", String.class);
        assertThat(resolver.supportsParameter(param)).isTrue();
    }

    @Test
    public void doesNotSupportStringParameterWithoutStudentNumberAnnotation() throws Exception {
        MethodParameter param = getMethodParam("hello", String.class);
        assertThat(resolver.supportsParameter(param)).isFalse();
    }

    @Test(expected = ForbiddenException.class)
    public void doesNotSupportParameterOfWrongTypeWithStudentNumberAnnotation() throws Exception {
        MethodParameter param = getMethodParam("helloError", Long.class);
        assertThat(resolver.supportsParameter(param)).isFalse();
    }

    @Test
    public void resolveArgumentForStudent() throws Exception {
        configureStudentSecurityContext();

        MethodParameter param = getMethodParam("helloStudent", String.class);
        assertThat("010189791").isEqualTo(resolver.resolveArgument(param, null, null, null));
    }

    @Test(expected = ForbiddenException.class)
    public void resolveArgumentForTeacher() throws Exception {
        configureTeacherSecurityContext();

        MethodParameter param = getMethodParam("helloStudent", String.class);
        resolver.resolveArgument(param, null, null, null);
    }

    private static MethodParameter getMethodParam(String methodName, Class<?> argType) {
        return new MethodParameter(ReflectionUtils.findMethod(MyController.class, methodName, argType), 0);
    }

    private static class MyController {
        public void helloStudent(@StudentNumber String studentNumber) {
        }

        public void hello(String studentNumber) {
        }

        public void helloError(@StudentNumber Long studentNumber) {
        }
    }
}
