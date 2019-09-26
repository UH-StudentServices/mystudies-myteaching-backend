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

package fi.helsinki.opintoni.util;

import fi.helsinki.opintoni.integration.coursepage.CoursePageCourseImplementation;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class DateTimeUtilTest  {

    private static final String EXPECTED_DATE_STRING = "2015-05-01T00:00:00.000Z";

    @Test
    public void thatLastSemesterStringIsObtainedCorrectlyBeforeEndOfYear() {
        LocalDate d = LocalDate.of(2015, 10, 10);
        assertThat(DateTimeUtil.getSemesterStartDateString(d)).isEqualTo(EXPECTED_DATE_STRING);
    }

    @Test
    public void thatLastSemesterStringIsObtainedCorrectlyAfterEndOfYear() {
        LocalDate d = LocalDate.of(2016, 4, 10);
        assertThat(DateTimeUtil.getSemesterStartDateString(d)).isEqualTo(EXPECTED_DATE_STRING);
    }

    @Test
    public void thatLastSemesterStringIsObtainedCorrectlyAtBeginnigOfAugust() {
        LocalDate d = LocalDate.of(2015, 8, 1);
        assertThat(DateTimeUtil.getSemesterStartDateString(d)).isEqualTo(EXPECTED_DATE_STRING);
    }

    @Test
    public void thatLastSemesterStringIsObtainedCorrectlyAtFirstOfMay() {
        LocalDate d = LocalDate.of(2016, 5, 1);
        assertThat(DateTimeUtil.getSemesterStartDateString(d)).isEqualTo(EXPECTED_DATE_STRING);
    }

    @Test
    public void thatLastSemesterStringIsObtainedCorrectlyAfterFirstOfMay() {
        LocalDate d = LocalDate.of(2016, 7, 1);
        assertThat(DateTimeUtil.getSemesterStartDateString(d)).isEqualTo(EXPECTED_DATE_STRING);
    }

    @Test
    public void forManuallyInspectingASerializedCoursePageCourseImplementationFromRedis()  {
        FileInputStream fileInStream = null;
        try {
            // on the server see the password in /opt/opintoni/config/application.properties and:
            // redis-cli --raw -h localhost -a 'password' get "coursePage::129620052_fi" > /tmp/file.bin
            // then copy the file over to local disk /tmp
            fileInStream = new FileInputStream("/tmp/file.bin");
            ObjectInputStream ois = new ObjectInputStream(fileInStream);
            CoursePageCourseImplementation myClass2 = (CoursePageCourseImplementation) ois.readObject();
            // Put a breakpoint on the next line and debug this test.
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
