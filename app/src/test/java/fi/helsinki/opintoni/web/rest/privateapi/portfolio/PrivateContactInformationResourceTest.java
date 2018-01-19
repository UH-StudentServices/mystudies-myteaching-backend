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

package fi.helsinki.opintoni.web.rest.privateapi.portfolio;

import com.google.common.collect.Lists;
import fi.helsinki.opintoni.domain.portfolio.Portfolio;
import fi.helsinki.opintoni.domain.portfolio.SomeLink;
import fi.helsinki.opintoni.repository.portfolio.ContactInformationRepository;
import fi.helsinki.opintoni.repository.portfolio.SomeLinkRepository;
import fi.helsinki.opintoni.web.WebTestUtils;
import fi.helsinki.opintoni.web.rest.privateapi.portfolio.contactinformation.UpdateContactInformation;
import fi.helsinki.opintoni.web.rest.privateapi.portfolio.contactinformation.UpdateSomeLink;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivateContactInformationResourceTest extends AbstractPortfolioResourceTest {

    @Autowired
    private ContactInformationRepository contactInformationRepository;

    @Autowired
    private SomeLinkRepository someLinkRepository;

    @Test
    public void thatContactInformationIsUpdated() throws Exception {
        UpdateContactInformation request = new UpdateContactInformation();
        request.email = "newemail@helsinki.fi";
        request.phoneNumber = "123456789";

        mockMvc.perform(post("/api/private/v1/portfolio/2/contactinformation")
            .with(securityContext(studentSecurityContext()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("newemail@helsinki.fi"))
            .andExpect(jsonPath("$.phoneNumber").value("123456789"));
    }

    @Test
    public void thatContactInformationIsCreated() throws Exception {
        contactInformationRepository.delete(1L);

        UpdateContactInformation request = new UpdateContactInformation();
        request.email = "email@helsinki.fi";
        request.phoneNumber = "987654321";

        mockMvc.perform(post("/api/private/v1/portfolio/2/contactinformation")
            .with(securityContext(studentSecurityContext()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("email@helsinki.fi"))
            .andExpect(jsonPath("$.phoneNumber").value("987654321"));
    }

    @Test
    public void thatTeacherContactInformationIsReset() throws Exception {

        expectEmployeeContactInformationRequestToESB();

        mockMvc.perform(get("/api/private/v1/portfolio/4/contactinformation/teacher")
            .with(securityContext(teacherSecurityContext())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("olli.opettaja@helsinki.fi"))
            .andExpect(jsonPath("$.workNumber").value("54321"))
            .andExpect(jsonPath("$.workMobile").value("12345678"))
            .andExpect(jsonPath("$.title").value("universitetslektor"))
            .andExpect(jsonPath("$.faculty").value("Käyttäytymistieteellinen tiedekunta"))
            .andExpect(jsonPath("$.financialUnit").value("OIKTDK, Oikeustieteellinen tiedekunta (OIKTDK)"))
            .andExpect(jsonPath("$.workAddress").value("PL 9 (Siltavuorenpenger 1A)"))
            .andExpect(jsonPath("$.workPostcode").value("00014 HELSINGIN YLIOPISTO"));
    }

    @Test
    public void thatSomeLinksAreCreated() throws Exception {
        UpdateContactInformation request = new UpdateContactInformation();

        UpdateSomeLink facebook = new UpdateSomeLink();
        facebook.type = "FACEBOOK";
        facebook.url = "http://facebook.com";

        UpdateSomeLink twitter = new UpdateSomeLink();
        twitter.type = "TWITTER";
        twitter.url = "http://twitter.com";

        request.someLinks.addAll(Lists.newArrayList(facebook, twitter));

        mockMvc.perform(post("/api/private/v1/portfolio/2/contactinformation")
            .with(securityContext(studentSecurityContext()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.someLinks", hasSize(2)))
            .andExpect(jsonPath("$.someLinks[0].type").value("FACEBOOK"))
            .andExpect(jsonPath("$.someLinks[0].url").value("http://facebook.com"))
            .andExpect(jsonPath("$.someLinks[1].type").value("TWITTER"))
            .andExpect(jsonPath("$.someLinks[1].url").value("http://twitter.com"));
    }

    @Test
    public void thatSomeLinksAreDeleted() throws Exception {
        persistSomeLink(2L);
        assertThat(someLinkRepository.findByPortfolioId(2L).isEmpty()).isFalse();

        UpdateContactInformation request = new UpdateContactInformation();

        mockMvc.perform(post("/api/private/v1/portfolio/2/contactinformation")
            .with(securityContext(studentSecurityContext()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(request)))
            .andExpect(status().isOk());

        assertThat(someLinkRepository.findByPortfolioId(2L).isEmpty()).isTrue();
    }

    private void persistSomeLink(Long portfolioId) {
        SomeLink someLink = new SomeLink();
        someLink.portfolio = new Portfolio();
        someLink.portfolio.id = portfolioId;
        someLink.url = "http://facebook.com";
        someLink.type = SomeLink.Type.FACEBOOK;
        someLinkRepository.save(someLink);
    }
}
