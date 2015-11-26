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

import static org.junit.Assert.assertFalse;

public class KeywordTest {

    @Test
    public void thatKeywordsAreEqual() {
        Keyword aKeyword = new Keyword();
        aKeyword.title = "Title";

        Keyword anotherKeyword = new Keyword();
        anotherKeyword.title = "Title";

        new EqualsTester()
            .addEqualityGroup(aKeyword, anotherKeyword)
            .testEquals();
    }

    @Test
    public void thatKeywordsAreNotEqual() {
        Keyword aKeyword = new Keyword();
        aKeyword.title = "Title 1";

        Keyword anotherKeyword = new Keyword();
        anotherKeyword.title = "Title 2";

        assertFalse(aKeyword.equals(anotherKeyword));
    }
}
