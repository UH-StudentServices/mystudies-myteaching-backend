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

package fi.helsinki.opintoni.service;

import fi.helsinki.opintoni.dto.DegreeProgrammeDto;
import fi.helsinki.opintoni.integration.guide.GuideClient;
import fi.helsinki.opintoni.service.converter.DegreeProgrammeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class DegreeProgrammeService {

    private final GuideClient guideClient;
    private final DegreeProgrammeConverter degreeProgrammeConverter;

    @Autowired
    public DegreeProgrammeService(GuideClient guideClient, DegreeProgrammeConverter degreeProgrammeConverter) {
        this.guideClient = guideClient;
        this.degreeProgrammeConverter = degreeProgrammeConverter;
    }

    public List<DegreeProgrammeDto> getDegreeProgrammes(Locale locale) {
        return guideClient.getDegreeProgrammes().stream()
            .map(guideDegreeProgramme -> degreeProgrammeConverter.toDto(guideDegreeProgramme, locale))
            .collect(Collectors.toList());
    }

}
