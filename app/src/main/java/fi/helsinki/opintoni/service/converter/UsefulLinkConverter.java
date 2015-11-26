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

import fi.helsinki.opintoni.domain.UsefulLink;
import fi.helsinki.opintoni.dto.UsefulLinkDto;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class UsefulLinkConverter {

    public UsefulLinkDto toDto(UsefulLink usefulLink, Locale locale) {
        UsefulLinkDto usefulLinkDto = new UsefulLinkDto();
        usefulLinkDto.id = usefulLink.id;
        usefulLinkDto.createdDate = usefulLink.getCreatedDate();
        usefulLinkDto.description = usefulLink.description;
        usefulLinkDto.type = usefulLink.type.name();
        usefulLinkDto.url = getUrl(usefulLink, locale);
        return usefulLinkDto;
    }

    private String getUrl(UsefulLink usefulLink, Locale locale) {
        if (usefulLink.hasLocalizedUrl()) {
            return usefulLink.localizedUrl.getByLocale(locale);
        }
        return usefulLink.url;
    }
}
