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

package fi.helsinki.opintoni.web.rest.publicapi.portfolio;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.domain.portfolio.*;
import fi.helsinki.opintoni.repository.portfolio.*;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public abstract class PublicPortfolioTest extends SpringTest {

    protected static final String PUBLIC_STUDENT_PORTFOLIO_API_PATH = RestConstants.PUBLIC_API_V1 + "/profile/2";

    protected static final long STUDENT_PORTFOLIO_ID = 2L;
    protected static final long TEACHER_PORTFOLIO_ID = 4L;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private LanguageProficiencyRepository languageProficiencyRepository;

    @Autowired
    private SampleRepository sampleRepository;

    @Autowired
    private WorkExperienceRepository workExperienceRepository;

    @Autowired
    private DegreeRepository degreeRepository;

    @Autowired
    private ComponentVisibilityRepository componentVisibilityRepository;

    @Before
    public void saveStudentPortfolioAsPublic() {
        savePortfolioAsPublic(STUDENT_PORTFOLIO_ID);
    }

    public void saveTeacherPortfolioAsPublic() {
        savePortfolioAsPublic(TEACHER_PORTFOLIO_ID);
    }

    public void setPrivateVisibilityForEveryStudentPortfolioComponent() {
        componentVisibilityRepository.deleteAll();

        Arrays.asList(PortfolioComponent.values()).forEach(component -> {
            ComponentVisibility componentVisibility = new ComponentVisibility();
            componentVisibility.component = component;
            componentVisibility.visibility = ComponentVisibility.Visibility.PRIVATE;
            componentVisibility.portfolio = portfolioRepository.findById(STUDENT_PORTFOLIO_ID).get();
            componentVisibilityRepository.save(componentVisibility);
        });
    }

    public void setPrivateVisibilityForPortfolioComponentItems(long portfolioId) {
        List<PortfolioLanguageProficiency> proficiencies = languageProficiencyRepository.findByPortfolioId(portfolioId).stream()
            .peek(proficiency -> proficiency.visibility = ComponentVisibility.Visibility.PRIVATE)
            .collect(toList());
        languageProficiencyRepository.saveAll(proficiencies);

        List<Sample> samples = sampleRepository.findByPortfolioId(portfolioId).stream()
            .peek(sample -> sample.visibility = ComponentVisibility.Visibility.PRIVATE)
            .collect(toList());
        sampleRepository.saveAll(samples);

        List<Degree> degrees = degreeRepository.findByPortfolioIdOrderByOrderIndexAsc(portfolioId).stream()
            .peek(degree -> degree.visibility = ComponentVisibility.Visibility.PRIVATE)
            .collect(toList());
        degreeRepository.saveAll(degrees);

        List<WorkExperience> workExperiences = workExperienceRepository.findByPortfolioIdOrderByOrderIndexAsc(portfolioId).stream()
            .peek(workExperience -> workExperience.visibility = ComponentVisibility.Visibility.PRIVATE)
            .collect(toList());
        workExperienceRepository.saveAll(workExperiences);
    }

    private void savePortfolioAsPublic(long portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).get();
        portfolio.visibility = PortfolioVisibility.PUBLIC;
        portfolioRepository.save(portfolio);
    }
}
