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

package fi.helsinki.opintoni.service.news;

import fi.helsinki.opintoni.integration.newsfeeds.GuideNewsClient;
import fi.helsinki.opintoni.integration.oodi.OodiClient;
import fi.helsinki.opintoni.integration.oodi.OodiStudyRight;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class GuideNewsServiceTest {

    @Mock
    private OodiClient oodiClient;

    @Mock
    private GuideNewsClient guideNewsClient;

    private GuideNewsService guideNewsService;

    @Before
    public void init() {

        guideNewsService = new GuideNewsService(oodiClient, guideNewsClient);
    }

    @Test
    public void testGetStudyRightElementCodes() {
        OodiStudyRight oodiStudyRight = new OodiStudyRight();
        addElement(oodiStudyRight, 15, "shouldBeIgnored");
        addElement(oodiStudyRight, 20, "MH1");
        addElement(oodiStudyRight, 20, "shouldAlsoBeIgnored");
        addElement(oodiStudyRight, 30, "major1");
        addElement(oodiStudyRight, 30, "major2");
        addElement(oodiStudyRight, 20, "MH2");
        List<String> codes = guideNewsService.getStudyRightElementCodes(oodiStudyRight);

        assertThat(codes).hasSize(6)
                .contains("MH1", "MH2", "MH1major1", "MH1major2", "MH2major1", "MH2major2");

    }

    private void addElement(OodiStudyRight oodiStudyRight, int id, String code) {
        OodiStudyRight.Element element = new OodiStudyRight.Element();
        element.id = id;
        element.code = code;
        oodiStudyRight.elements.add(element);
    }
}