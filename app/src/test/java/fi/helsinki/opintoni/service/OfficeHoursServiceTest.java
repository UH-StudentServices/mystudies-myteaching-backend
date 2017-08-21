package fi.helsinki.opintoni.service;

import com.google.common.net.MediaType;
import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.domain.DegreeProgramme;
import fi.helsinki.opintoni.domain.OfficeHours;
import fi.helsinki.opintoni.dto.OfficeHoursDto;
import fi.helsinki.opintoni.dto.PublicOfficeHoursDto;
import fi.helsinki.opintoni.repository.DegreeProgrammeRepository;
import fi.helsinki.opintoni.repository.OfficeHoursRepository;
import fi.helsinki.opintoni.repository.UserRepository;
import fi.helsinki.opintoni.service.OfficeHoursService;
import fi.helsinki.opintoni.service.converter.OfficeHoursConverter;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.rest.privateapi.OfficeHoursResource;
import javafx.print.Printer;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.hibernate.mapping.Array;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import fi.helsinki.opintoni.sampledata.SampleDataFiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static fi.helsinki.opintoni.web.WebTestUtils.toJsonBytes;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class OfficeHoursServiceTest extends SpringTest{

    @Autowired
    OfficeHoursService officeHoursService;

    @Test
    public void showProperties() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/public/v1/officehours/")
            .with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(org.springframework.http.MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*]").exists())
            .andExpect(jsonPath("$.[*].name").exists())
            .andReturn();
    }

    @Test
    public void listOfficeHours(){List<PublicOfficeHoursDto> publicOfficeHoursDtos = officeHoursService.getAll();
        for (int i=0;i<publicOfficeHoursDtos.size();i++){
            //System.out.print(publicOfficeHoursDtos.get(i).name.toString());
        }
    }
}
