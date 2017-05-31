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

package fi.helsinki.opintoni.service.converter;

import fi.helsinki.opintoni.dto.DegreeProgrammeDto;
import fi.helsinki.opintoni.integration.guide.GuideDegreeProgramme;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class DegreeProgrammeConverter {

    public DegreeProgrammeDto toDto(GuideDegreeProgramme guideDegreeProgramme, Locale locale) {
        DegreeProgrammeDto degreeProgrammeDto = new DegreeProgrammeDto();
        degreeProgrammeDto.code = guideDegreeProgramme.code;
        degreeProgrammeDto.name = getDegreeProgrammeName(guideDegreeProgramme, locale);
        return degreeProgrammeDto;
    }

    private String getDegreeProgrammeName(GuideDegreeProgramme guideDegreeProgramme, Locale locale) {
        String language = locale.getLanguage();
        if (language.equals("en")) {
            return guideDegreeProgramme.name.en;
        } else if (language.equals("sv")) {
            return guideDegreeProgramme.name.sv;
        } else  {
            return guideDegreeProgramme.name.fi;
        }
    }

}
