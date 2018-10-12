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

package fi.helsinki.opintoni.web.rest.privateapi.portfolio.keyword;

import com.google.common.testing.EqualsTester;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KeywordTest {

    @Test
    public void thatKeywordsAreEqual() {
        Keyword firstKeyword = new Keyword();
        firstKeyword.title = "Title";

        Keyword secondKeyword = new Keyword();
        secondKeyword.title = "Title";

        new EqualsTester()
            .addEqualityGroup(firstKeyword, secondKeyword)
            .testEquals();
    }

    @Test
    public void thatKeywordsAreNotEqual() {
        Keyword firstKeyword = new Keyword();
        firstKeyword.title = "Title 1";

        Keyword secondKeyword = new Keyword();
        secondKeyword.title = "Title 2";

        assertThat(firstKeyword.equals(secondKeyword)).isFalse();
    }
}
